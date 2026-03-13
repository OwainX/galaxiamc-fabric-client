package com.galaxiamc.client.screen;

import com.galaxiamc.client.GalaxiaMCClient;
import com.galaxiamc.client.api.models.Status;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

public class PostDetailScreen extends Screen {
    private final Screen parent;
    private final Status status;

    public PostDetailScreen(Screen parent, Status status) {
        super(Text.literal(status.account.username));
        this.parent = parent;
        this.status = status;
    }

    @Override
    protected void init() {
        int actionY = height - 35;
        int buttonWidth = 60;
        int spacing = 65;
        int startX = width / 2 - (spacing * 2);

        addDrawableChild(ButtonWidget.builder(Text.literal(status.favourited ? "❤️" : "🤍"), button -> {
            toggleLike();
        }).dimensions(startX, actionY, buttonWidth, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("🔁"), button -> {
            toggleBoost();
        }).dimensions(startX + spacing, actionY, buttonWidth, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("💬"), button -> {
            openReply();
        }).dimensions(startX + spacing * 2, actionY, buttonWidth, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("🔖"), button -> {
            if (client != null && client.player != null) {
                client.player.sendMessage(Text.literal("§eBookmark feature not yet implemented"), false);
            }
        }).dimensions(startX + spacing * 3, actionY, buttonWidth, 20).build());

        addDrawableChild(ButtonWidget.builder(ScreenTexts.BACK, button -> {
            if (client != null) {
                client.setScreen(parent);
            }
        }).dimensions(10, height - 30, 60, 20).build());
    }

    private void toggleLike() {
        var future = status.favourited 
            ? GalaxiaMCClient.getApiClient().unfavouriteStatus(status.id)
            : GalaxiaMCClient.getApiClient().favouriteStatus(status.id);

        future.thenAccept(response -> {
            if (client != null) {
                client.execute(() -> {
                    if (response.isSuccess() && response.getData().isPresent()) {
                        Status updated = response.getData().get();
                        status.favourited = updated.favourited;
                        status.favouritesCount = updated.favouritesCount;
                        clearAndInit();
                    }
                });
            }
        });
    }

    private void toggleBoost() {
        var future = status.reblogged 
            ? GalaxiaMCClient.getApiClient().unreblogStatus(status.id)
            : GalaxiaMCClient.getApiClient().reblogStatus(status.id);

        future.thenAccept(response -> {
            if (client != null) {
                client.execute(() -> {
                    if (response.isSuccess() && response.getData().isPresent()) {
                        Status updated = response.getData().get();
                        status.reblogged = updated.reblogged;
                        status.reblogsCount = updated.reblogsCount;
                        clearAndInit();
                    }
                });
            }
        });
    }

    private void openReply() {
        if (client != null) {
            client.setScreen(new ComposeScreen(this, status.id, "@" + status.account.username + " "));
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        int y = 60;
        int x = 20;
        int maxWidth = width - 40;

        context.drawTextWithShadow(textRenderer, Text.literal(status.account.getDisplayNameOrUsername()), x, y, 0xFFFFFF);
        y += 12;
        context.drawTextWithShadow(textRenderer, Text.literal("@" + status.account.username), x, y, 0xAAAAAA);
        y += 20;

        String content = status.getPlainContent();
        for (String line : wrapText(content, maxWidth)) {
            context.drawTextWithShadow(textRenderer, Text.literal(line), x, y, 0xFFFFFF);
            y += 12;
        }

        y += 10;
        context.drawTextWithShadow(textRenderer, Text.literal(
            "❤️ " + status.favouritesCount + "  🔁 " + status.reblogsCount + "  💬 " + status.repliesCount
        ), x, y, 0xAAAAAA);
    }

    private String[] wrapText(String text, int maxWidth) {
        if (textRenderer.getWidth(text) <= maxWidth) {
            return new String[] { text };
        }

        java.util.List<String> lines = new java.util.ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            String testLine = currentLine.length() == 0 ? word : currentLine + " " + word;
            if (textRenderer.getWidth(testLine) <= maxWidth) {
                currentLine = new StringBuilder(testLine);
            } else {
                if (currentLine.length() > 0) {
                    lines.add(currentLine.toString());
                }
                currentLine = new StringBuilder(word);
            }
        }
        
        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }

        return lines.toArray(new String[0]);
    }

    @Override
    public void close() {
        if (client != null) {
            client.setScreen(parent);
        }
    }
}

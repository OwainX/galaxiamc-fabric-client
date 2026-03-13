package com.galaxiamc.client.screen;

import com.galaxiamc.client.GalaxiaMCClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

public class GalaxiaHubScreen extends Screen {
    private final Screen parent;

    public GalaxiaHubScreen(Screen parent) {
        super(Text.translatable("screen.galaxiamc.hub.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int startY = this.height / 2 - 60;
        int buttonWidth = 200;
        int buttonHeight = 20;
        int spacing = 25;

        addDrawableChild(ButtonWidget.builder(Text.translatable("button.galaxiamc.feed"), button -> {
            if (client != null) {
                client.setScreen(new FeedScreen(this));
            }
        }).dimensions(centerX - buttonWidth / 2, startY, buttonWidth, buttonHeight).build());

        addDrawableChild(ButtonWidget.builder(Text.translatable("button.galaxiamc.notifications"), button -> {
            if (client != null) {
                client.setScreen(new NotificationsScreen(this));
            }
        }).dimensions(centerX - buttonWidth / 2, startY + spacing, buttonWidth, buttonHeight).build());

        addDrawableChild(ButtonWidget.builder(Text.translatable("button.galaxiamc.profile"), button -> {
            if (client != null && client.player != null) {
                client.player.sendMessage(Text.literal("§eProfile screen not yet implemented"), false);
            }
        }).dimensions(centerX - buttonWidth / 2, startY + spacing * 2, buttonWidth, buttonHeight).build());

        addDrawableChild(ButtonWidget.builder(Text.translatable("button.galaxiamc.settings"), button -> {
            if (client != null && client.player != null) {
                client.player.sendMessage(Text.literal("§eSettings screen not yet implemented"), false);
            }
        }).dimensions(centerX - buttonWidth / 2, startY + spacing * 3, buttonWidth, buttonHeight).build());

        addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> {
            if (client != null) {
                client.setScreen(parent);
            }
        }).dimensions(centerX - buttonWidth / 2, startY + spacing * 4 + 10, buttonWidth, buttonHeight).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 20, 0xFFFFFF);

        String statusText;
        if (GalaxiaMCClient.getAuthManager().isLinked()) {
            statusText = "§aLinked";
        } else {
            statusText = Text.translatable("text.galaxiamc.notlinked").getString();
        }
        context.drawCenteredTextWithShadow(textRenderer, Text.literal(statusText), width / 2, 40, 0xFFFFFF);
    }

    @Override
    public void close() {
        if (client != null) {
            client.setScreen(parent);
        }
    }
}

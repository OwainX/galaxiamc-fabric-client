package com.galaxiamc.client.screen;

import com.galaxiamc.client.GalaxiaMCClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

public class ComposeScreen extends Screen {
    private final Screen parent;
    private final String replyToId;
    private TextFieldWidget contentField;
    private static final int MAX_CHARS = 500;

    public ComposeScreen(Screen parent) {
        this(parent, null, "");
    }

    public ComposeScreen(Screen parent, String replyToId, String initialText) {
        super(Text.translatable("screen.galaxiamc.compose.title"));
        this.parent = parent;
        this.replyToId = replyToId;
    }

    @Override
    protected void init() {
        int fieldWidth = Math.min(400, width - 40);
        int fieldX = (width - fieldWidth) / 2;
        int fieldY = 60;

        contentField = new TextFieldWidget(textRenderer, fieldX, fieldY, fieldWidth, 100, Text.empty());
        contentField.setMaxLength(MAX_CHARS);
        contentField.setText("");
        addSelectableChild(contentField);

        addDrawableChild(ButtonWidget.builder(Text.translatable("button.galaxiamc.post"), button -> {
            postStatus();
        }).dimensions(width / 2 - 105, height - 50, 100, 20).build());

        addDrawableChild(ButtonWidget.builder(ScreenTexts.CANCEL, button -> {
            if (client != null) {
                client.setScreen(parent);
            }
        }).dimensions(width / 2 + 5, height - 50, 100, 20).build());

        setInitialFocus(contentField);
    }

    private void postStatus() {
        String content = contentField.getText().trim();
        if (content.isEmpty()) {
            return;
        }

        GalaxiaMCClient.getApiClient().postStatus(content, replyToId).thenAccept(response -> {
            if (client != null) {
                client.execute(() -> {
                    if (response.isSuccess()) {
                        client.setScreen(parent);
                        if (client.player != null) {
                            client.player.sendMessage(Text.literal("§aPost published!"), false);
                        }
                    } else {
                        if (client.player != null) {
                            client.player.sendMessage(Text.literal("§cFailed to post: " + response.getError()), false);
                        }
                    }
                });
            }
        });
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 20, 0xFFFFFF);

        if (contentField != null) {
            contentField.render(context, mouseX, mouseY, delta);
            
            int remaining = MAX_CHARS - contentField.getText().length();
            int color = remaining < 50 ? 0xFF5555 : 0xAAAAAA;
            context.drawTextWithShadow(textRenderer, Text.literal(remaining + " / " + MAX_CHARS), 
                contentField.getX(), contentField.getY() + contentField.getHeight() + 5, color);
        }
    }

    @Override
    public void close() {
        if (client != null) {
            client.setScreen(parent);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (contentField != null && contentField.isFocused()) {
            return contentField.keyPressed(keyCode, scanCode, modifiers);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (contentField != null && contentField.isFocused()) {
            return contentField.charTyped(chr, modifiers);
        }
        return super.charTyped(chr, modifiers);
    }
}

package com.galaxiamc.client.screen;

import com.galaxiamc.client.GalaxiaMCClient;
import com.galaxiamc.client.api.models.Notification;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class NotificationsScreen extends Screen {
    private final Screen parent;
    private boolean isLoading = false;
    private final List<Notification> notifications = new ArrayList<>();

    public NotificationsScreen(Screen parent) {
        super(Text.translatable("screen.galaxiamc.notifications.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        addDrawableChild(ButtonWidget.builder(ScreenTexts.BACK, button -> {
            if (client != null) {
                client.setScreen(parent);
            }
        }).dimensions(10, height - 30, 60, 20).build());

        loadNotifications();
    }

    private void loadNotifications() {
        if (isLoading) return;
        isLoading = true;

        GalaxiaMCClient.getApiClient().getNotifications().thenAccept(response -> {
            if (client != null) {
                client.execute(() -> {
                    isLoading = false;
                    if (response.isSuccess() && response.getData().isPresent()) {
                        notifications.clear();
                        notifications.addAll(response.getData().get());
                    }
                });
            }
        });
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 15, 0xFFFFFF);

        int y = 40;
        int x = 20;

        if (notifications.isEmpty() && !isLoading) {
            context.drawCenteredTextWithShadow(textRenderer, Text.literal("No notifications"), width / 2, height / 2, 0xAAAAAA);
        }

        for (Notification notification : notifications) {
            if (y > height - 50) break;

            String icon = notification.getIcon();
            String actor = notification.account.getDisplayNameOrUsername();
            String typeText = switch (notification.getTypeEnum()) {
                case FAVOURITE -> " liked your post";
                case REBLOG -> " boosted your post";
                case FOLLOW -> " followed you";
                case MENTION -> " mentioned you";
                case POLL -> " poll ended";
                case FOLLOW_REQUEST -> " requested to follow you";
            };

            context.drawTextWithShadow(textRenderer, Text.literal(icon + " " + actor + typeText), x, y, 0xFFFFFF);
            y += 20;

            if (notification.status != null) {
                String preview = notification.status.getPlainContent();
                if (preview.length() > 60) {
                    preview = preview.substring(0, 57) + "...";
                }
                context.drawTextWithShadow(textRenderer, Text.literal(preview), x + 10, y, 0xAAAAAA);
                y += 15;
            }
        }

        if (isLoading) {
            context.drawCenteredTextWithShadow(textRenderer, Text.translatable("text.galaxiamc.loading"), width / 2, height - 15, 0xAAAAAA);
        }
    }

    @Override
    public void close() {
        if (client != null) {
            client.setScreen(parent);
        }
    }
}

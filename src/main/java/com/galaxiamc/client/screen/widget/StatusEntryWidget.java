package com.galaxiamc.client.screen.widget;

import com.galaxiamc.client.api.models.Status;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.text.Text;

public class StatusEntryWidget extends AlwaysSelectedEntryListWidget.Entry<StatusEntryWidget> {
    private final Status status;
    private final MinecraftClient client;
    private final Runnable onClick;

    public StatusEntryWidget(Status status, Runnable onClick) {
        this.status = status;
        this.client = MinecraftClient.getInstance();
        this.onClick = onClick;
    }

    @Override
    public void render(DrawContext context, int index, int y, boolean selected, float tickDelta) {
        TextRenderer textRenderer = client.textRenderer;
        int x = 0;
        int entryWidth = 0;
        int entryHeight = 32;

        AvatarWidget.render(context, status.account.avatarStatic, x + 2, y + 2, 16);

        String displayName = status.account.getDisplayNameOrUsername();
        context.drawText(textRenderer, Text.literal(displayName), x + 22, y + 2, 0xFFFFFF, false);

        String content = status.getPlainContent();
        if (content.length() > 80) {
            content = content.substring(0, 77) + "...";
        }
        context.drawText(textRenderer, Text.literal(content), x + 22, y + 14, 0xAAAAAA, false);

        if (selected) {
            context.fill(x, y, x + entryWidth, y + entryHeight, 0x40FFFFFF);
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (onClick != null) {
            onClick.run();
            return true;
        }
        return false;
    }

    @Override
    public Text getNarration() {
        return Text.literal(status.account.username + " posted: " + status.getPlainContent());
    }

    public Status getStatus() {
        return status;
    }
}

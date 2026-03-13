package com.galaxiamc.client.screen.widget;

import com.galaxiamc.client.api.models.Status;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;

import java.util.List;

public class ScrollableListWidget extends AlwaysSelectedEntryListWidget<StatusEntryWidget> {
    private Runnable onScrollToBottom;

    public ScrollableListWidget(MinecraftClient client, int width, int height, int y, int itemHeight) {
        super(client, width, height, y, itemHeight);
    }

    public void setStatuses(List<Status> statuses, StatusClickHandler clickHandler) {
        clearEntries();
        for (Status status : statuses) {
            addEntry(new StatusEntryWidget(status, () -> clickHandler.onClick(status)));
        }
    }

    public void addStatuses(List<Status> statuses, StatusClickHandler clickHandler) {
        for (Status status : statuses) {
            addEntry(new StatusEntryWidget(status, () -> clickHandler.onClick(status)));
        }
    }

    public void setOnScrollToBottom(Runnable onScrollToBottom) {
        this.onScrollToBottom = onScrollToBottom;
    }

    @Override
    protected int getScrollbarX() {
        return this.width - 6;
    }

    @Override
    public int getRowWidth() {
        return this.width - 20;
    }

    public interface StatusClickHandler {
        void onClick(Status status);
    }
}

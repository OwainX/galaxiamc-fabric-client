package com.galaxiamc.client.screen;

import com.galaxiamc.client.GalaxiaMCClient;
import com.galaxiamc.client.api.models.Status;
import com.galaxiamc.client.screen.widget.ScrollableListWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class FeedScreen extends Screen {
    private final Screen parent;
    private ScrollableListWidget listWidget;
    private boolean isHomeFeed = true;
    private boolean isLoading = false;
    private String currentMaxId = null;
    private final List<Status> loadedStatuses = new ArrayList<>();

    public FeedScreen(Screen parent) {
        super(Text.translatable("screen.galaxiamc.feed.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int tabY = 40;
        int listTop = tabY + 30;
        
        addDrawableChild(ButtonWidget.builder(Text.literal("Home"), button -> {
            isHomeFeed = true;
            refreshFeed();
        }).dimensions(width / 2 - 105, tabY, 100, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Public"), button -> {
            isHomeFeed = false;
            refreshFeed();
        }).dimensions(width / 2 + 5, tabY, 100, 20).build());

        addDrawableChild(ButtonWidget.builder(ScreenTexts.BACK, button -> {
            if (client != null) {
                client.setScreen(parent);
            }
        }).dimensions(10, height - 30, 60, 20).build());

        listWidget = new ScrollableListWidget(client, width - 20, height - listTop - 40, listTop, 32);
        listWidget.setOnScrollToBottom(this::loadMore);
        addSelectableChild(listWidget);

        refreshFeed();
    }

    private void refreshFeed() {
        loadedStatuses.clear();
        currentMaxId = null;
        if (listWidget != null) {
            listWidget.setStatuses(new ArrayList<>(), this::openPost);
        }
        loadMore();
    }

    private void loadMore() {
        if (isLoading) return;
        isLoading = true;

        var future = isHomeFeed 
            ? GalaxiaMCClient.getApiClient().getHomeFeed(currentMaxId)
            : GalaxiaMCClient.getApiClient().getPublicFeed(currentMaxId);

        future.thenAccept(response -> {
            if (client != null) {
                client.execute(() -> {
                    isLoading = false;
                    if (response.isSuccess() && response.getData().isPresent()) {
                        List<Status> newStatuses = response.getData().get();
                        if (!newStatuses.isEmpty()) {
                            loadedStatuses.addAll(newStatuses);
                            currentMaxId = newStatuses.get(newStatuses.size() - 1).id;
                            if (listWidget != null) {
                                listWidget.addStatuses(newStatuses, this::openPost);
                            }
                        }
                    }
                });
            }
        });
    }

    private void openPost(Status status) {
        if (client != null) {
            client.setScreen(new PostDetailScreen(this, status));
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        
        if (listWidget != null) {
            listWidget.render(context, mouseX, mouseY, delta);
        }

        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 15, 0xFFFFFF);

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

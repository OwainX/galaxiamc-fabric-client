package com.galaxiamc.client.screen;

import com.galaxiamc.client.api.ApiClient;
import com.galaxiamc.client.api.models.Account;
import com.galaxiamc.client.api.models.Status;
import com.galaxiamc.client.config.GalaxiaConfig;
import com.galaxiamc.client.screen.widget.ScrollableListWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ProfileScreen extends Screen {
    private final Screen parent;
    private final Account account;
    private final ApiClient apiClient;
    private ScrollableListWidget listWidget;
    private boolean isLoading = false;
    private String currentMaxId = null;
    private final List<Status> loadedStatuses = new ArrayList<>();

    public ProfileScreen(Screen parent, Account account) {
        super(Text.literal(account.username));
        this.parent = parent;
        this.account = account;
        this.apiClient = new ApiClient(GalaxiaConfig.getInstance());
    }

    @Override
    protected void init() {
        int headerHeight = 100;
        int listTop = headerHeight + 10;

        addDrawableChild(ButtonWidget.builder(Text.translatable("button.galaxiamc.follow"), button -> {
            if (client != null && client.player != null) {
                client.player.sendMessage(Text.literal("§eFollow feature not yet implemented"), false);
            }
        }).dimensions(width - 120, 60, 100, 20).build());

        addDrawableChild(ButtonWidget.builder(ScreenTexts.BACK, button -> {
            if (client != null) {
                client.setScreen(parent);
            }
        }).dimensions(10, height - 30, 60, 20).build());

        listWidget = new ScrollableListWidget(client, width - 20, height - listTop - 40, listTop, 32);
        listWidget.setOnScrollToBottom(this::loadMore);
        addSelectableChild(listWidget);

        loadMore();
    }

    private void loadMore() {
        if (isLoading) return;
        isLoading = true;

        apiClient.getAccountStatuses(account.id, currentMaxId).thenAccept(response -> {
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

        int y = 20;
        context.drawTextWithShadow(textRenderer, Text.literal(account.getDisplayNameOrUsername()), 20, y, 0xFFFFFF);
        y += 12;
        context.drawTextWithShadow(textRenderer, Text.literal("@" + account.username), 20, y, 0xAAAAAA);
        y += 15;
        
        String bio = account.note != null ? account.note.replaceAll("<[^>]*>", "") : "";
        if (!bio.isEmpty()) {
            context.drawTextWithShadow(textRenderer, Text.literal(bio), 20, y, 0xFFFFFF);
            y += 12;
        }
        
        y += 5;
        context.drawTextWithShadow(textRenderer, Text.literal(
            account.statusesCount + " posts  " + 
            account.followersCount + " followers  " + 
            account.followingCount + " following"
        ), 20, y, 0xAAAAAA);

        if (listWidget != null) {
            listWidget.render(context, mouseX, mouseY, delta);
        }
    }

    @Override
    public void close() {
        if (client != null) {
            client.setScreen(parent);
        }
    }
}

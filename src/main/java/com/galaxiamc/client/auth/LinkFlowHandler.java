package com.galaxiamc.client.auth;

import com.galaxiamc.client.api.ApiClient;
import com.galaxiamc.client.api.ApiResponse;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class LinkFlowHandler {
    private final ApiClient apiClient;
    private final AuthManager authManager;
    private String pendingCode = null;

    public LinkFlowHandler(ApiClient apiClient, AuthManager authManager) {
        this.apiClient = apiClient;
        this.authManager = authManager;
    }

    public void startLinkFlow() {
        MinecraftClient client = MinecraftClient.getInstance();
        
        apiClient.requestLinkCode().thenAccept(response -> {
            if (response.isSuccess() && response.getData().isPresent()) {
                pendingCode = response.getData().get();
                if (client.player != null) {
                    client.player.sendMessage(Text.literal("§aYour link code: §e" + pendingCode), false);
                    client.player.sendMessage(Text.literal("§7Go to galaxiamc.com/link to connect your account."), false);
                }
            } else {
                if (client.player != null) {
                    client.player.sendMessage(Text.literal("§cFailed to request link code: " + response.getError()), false);
                }
            }
        });
    }

    public String getPendingCode() {
        return pendingCode;
    }

    public void clearPendingCode() {
        pendingCode = null;
    }
}

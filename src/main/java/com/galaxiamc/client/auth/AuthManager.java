package com.galaxiamc.client.auth;

import com.galaxiamc.client.config.GalaxiaConfig;
import net.minecraft.client.MinecraftClient;

public class AuthManager {
    private final GalaxiaConfig config;

    public AuthManager(GalaxiaConfig config) {
        this.config = config;
    }

    public boolean isLinked() {
        return config.isLinked();
    }

    public void setLinkedAccount(String accessToken, String minecraftUuid) {
        config.setAccessToken(accessToken);
        config.setLinkedMinecraftUuid(minecraftUuid);
    }

    public void unlink() {
        config.setAccessToken("");
        config.setLinkedMinecraftUuid("");
    }

    public String getCurrentPlayerUuid() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            return client.player.getUuidAsString();
        }
        return "";
    }
}

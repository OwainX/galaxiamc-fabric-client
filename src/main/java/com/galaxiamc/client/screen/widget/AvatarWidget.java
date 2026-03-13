package com.galaxiamc.client.screen.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class AvatarWidget {
    private static final Map<String, Identifier> AVATAR_CACHE = new HashMap<>();
    private static final Identifier DEFAULT_AVATAR = Identifier.of("minecraft", "textures/entity/steve.png");

    public static void render(DrawContext context, String avatarUrl, int x, int y, int size) {
        // TODO: Implement proper texture rendering with MC 1.21.11 API
        // For now, draw a placeholder colored rectangle
        context.fill(x, y, x + size, y + size, 0xFF888888);
    }

    private static Identifier getOrLoadAvatar(String avatarUrl) {
        if (avatarUrl == null || avatarUrl.isEmpty()) {
            return DEFAULT_AVATAR;
        }

        if (AVATAR_CACHE.containsKey(avatarUrl)) {
            return AVATAR_CACHE.get(avatarUrl);
        }

        CompletableFuture.runAsync(() -> {
            try {
                URL url = new URL(avatarUrl);
                try (InputStream stream = url.openStream()) {
                    BufferedImage image = ImageIO.read(stream);
                    MinecraftClient.getInstance().execute(() -> {
                        // TODO: Convert BufferedImage to NativeImage and register as DynamicTexture
                        // For now, using default texture
                        AVATAR_CACHE.put(avatarUrl, DEFAULT_AVATAR);
                    });
                }
            } catch (Exception e) {
                AVATAR_CACHE.put(avatarUrl, DEFAULT_AVATAR);
            }
        });

        return DEFAULT_AVATAR;
    }

    public static void clearCache() {
        AVATAR_CACHE.clear();
    }
}

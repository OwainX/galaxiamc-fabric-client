package com.galaxiamc.client;

import com.galaxiamc.client.api.ApiClient;
import com.galaxiamc.client.auth.AuthManager;
import com.galaxiamc.client.auth.LinkFlowHandler;
import com.galaxiamc.client.config.GalaxiaConfig;
import com.galaxiamc.client.keybind.KeyBindings;
import com.galaxiamc.client.screen.GalaxiaHubScreen;
import com.mojang.brigadier.Command;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GalaxiaMCClient implements ClientModInitializer {
    public static final String MOD_ID = "galaxiamc-client";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    private static AuthManager authManager;
    private static LinkFlowHandler linkFlowHandler;

    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing GalaxiaMC Client Mod");

        GalaxiaConfig config = GalaxiaConfig.getInstance();
        authManager = new AuthManager(config);
        linkFlowHandler = new LinkFlowHandler(ApiClient.getInstance(), authManager);

        KeyBindings.register();
        registerCommands();
        registerEvents();

        LOGGER.info("GalaxiaMC Client Mod initialized");
    }

    private void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("galaxia")
                .executes(context -> {
                    MinecraftClient client = context.getSource().getClient();
                    client.execute(() -> client.setScreen(new GalaxiaHubScreen(client.currentScreen)));
                    return Command.SINGLE_SUCCESS;
                })
                .then(ClientCommandManager.literal("link")
                    .executes(context -> {
                        MinecraftClient client = context.getSource().getClient();
                        client.execute(() -> {
                            if (linkFlowHandler != null) {
                                linkFlowHandler.startLinkFlow();
                            }
                        });
                        return Command.SINGLE_SUCCESS;
                    })
                )
            );
        });
    }

    private void registerEvents() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (KeyBindings.openGalaxiaHub.wasPressed()) {
                client.setScreen(new GalaxiaHubScreen(client.currentScreen));
            }
        });
    }

    public static AuthManager getAuthManager() {
        return authManager;
    }

    public static LinkFlowHandler getLinkFlowHandler() {
        return linkFlowHandler;
    }

    public static ApiClient getApiClient() {
        return ApiClient.getInstance();
    }
}

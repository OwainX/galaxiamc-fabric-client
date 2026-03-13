package com.galaxiamc.client.keybind;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
    public static KeyBinding openGalaxiaHub;

    public static void register() {
        openGalaxiaHub = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.galaxiamc.open",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            KeyBinding.Category.create(Identifier.of("galaxiamc", "keybinds"))
        ));
    }
}

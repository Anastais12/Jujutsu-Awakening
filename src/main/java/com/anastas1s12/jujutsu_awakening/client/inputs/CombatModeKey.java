package com.anastas1s12.jujutsu_awakening.client.inputs;

import com.anastas1s12.jujutsu_awakening.cursed_energy.CursedEnergyManager;
import com.anastas1s12.jujutsu_awakening.cursed_energy.PlayerCursedEnergy;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public class CombatModeKey {
    private static final KeyMapping COMBAT_KEY = KeyBindingHelper.registerKeyBinding(
            new KeyMapping("key.jujutsu_awakening.combat_mode",
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_LEFT_ALT,
                    KeyMapping.Category.GAMEPLAY)
    );

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (COMBAT_KEY.consumeClick() && client.player != null) {
                PlayerCursedEnergy ce = CursedEnergyManager.get(client.player.getUUID());
                boolean newMode = !ce.isInCombatMode();
                ce.setInCombatMode(newMode);

                client.player.displayClientMessage(
                        Component.literal("§eCombat Mode: " + (newMode ? "§aON" : "§cOFF")),
                        true
                );
            }
        });
    }
}
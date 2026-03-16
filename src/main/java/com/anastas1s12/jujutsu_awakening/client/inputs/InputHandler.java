package com.anastas1s12.jujutsu_awakening.client.inputs;

import com.anastas1s12.jujutsu_awakening.client.hud.CombatHotbarHud;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;

public class InputHandler {
    private static double accumulatedScroll = 0;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            // Handle accumulated scroll
            if (accumulatedScroll != 0) {
                CombatHotbarHud.handleScroll(accumulatedScroll);
                accumulatedScroll = 0;
            }
        });
    }

    public static void onMouseScroll(double scrollDelta) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) return;

        // Only handle scroll when in combat mode
        // The packet will be sent from CombatHotbarHud
        accumulatedScroll += scrollDelta;
    }
}
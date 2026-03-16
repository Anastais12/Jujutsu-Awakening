package com.anastas1s12.jujutsu_awakening.network;

import com.anastas1s12.jujutsu_awakening.cursed_energy.CursedEnergyManager;
import com.anastas1s12.jujutsu_awakening.cursed_energy.PlayerCursedEnergy;
import com.anastas1s12.jujutsu_awakening.network.s2c.CombatHotbarScrollPacket;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;

public class ModNetwork {
    public static void register() {
        // Register packet type
        PayloadTypeRegistry.playC2S().register(CombatHotbarScrollPacket.TYPE, CombatHotbarScrollPacket.CODEC);

        // Server-side handler for scroll packets
        ServerPlayNetworking.registerGlobalReceiver(CombatHotbarScrollPacket.TYPE, (payload, context) -> {
            ServerPlayer player = context.player();

            context.server().execute(() -> {
                PlayerCursedEnergy ce = CursedEnergyManager.get(player.getUUID());

                if (ce.isInCombatMode()) {
                    // Cycle through 8 custom hotbar slots
                    int currentSlot = ce.getCustomHotbarSlot();
                    int newSlot = currentSlot + payload.direction();

                    // Wrap around
                    if (newSlot < 0) newSlot = 7;
                    if (newSlot > 7) newSlot = 0;

                    ce.setCustomHotbarSlot(newSlot);

                    // Sync to client if needed
                    CursedEnergyManager.sync(player);
                }
            });
        });
    }
}
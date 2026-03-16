package com.anastas1s12.jujutsu_awakening.cursed_energy;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerPlayer;  // ← new import

public class CursedEnergyTicker {
    public static void register() {

        ServerTickEvents.END_SERVER_TICK.register(server -> {

            for (ServerPlayer player : server.getPlayerList().getPlayers()) {

                PlayerCursedEnergy cursed_energy = CursedEnergyManager.get(player);

                if (cursed_energy.getEnergy() < cursed_energy.getMaxEnergy()) {
                    cursed_energy.addEnergy(1);
                }
            }
        });
    }
}
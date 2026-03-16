package com.anastas1s12.jujutsu_awakening.cursed_energy;

import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.UUID;

public class CursedEnergyManager {
    private static final HashMap<UUID, PlayerCursedEnergy> ENERGY = new HashMap<>();

    // works for both server AND client
    public static PlayerCursedEnergy get(UUID uuid) {
        return ENERGY.computeIfAbsent(uuid, u -> new PlayerCursedEnergy());
    }

    // old server call
    public static PlayerCursedEnergy get(ServerPlayer player) {
        return get(player.getUUID());
    }

    // Sync method - sends data to client
    public static void sync(ServerPlayer player) {
        PlayerCursedEnergy ce = get(player);
        // You'll need to implement a sync packet to send CE data to client
        // For now, this is a placeholder
    }
}
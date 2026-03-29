package com.anastas1s12.jujutsu_awakening.commands;

import com.anastas1s12.jujutsu_awakening.client.hud.KeyframesEditorScreen;
import com.anastas1s12.jujutsu_awakening.cursed_energy.CursedEnergyManager;
import com.anastas1s12.jujutsu_awakening.cursed_energy.PlayerCursedEnergy;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class ModCommands {

    public static void registerCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, env) -> {
            dispatcher.register(Commands.literal("jujutsu-awakening")
                    .then(Commands.literal("ce")
                            .then(Commands.literal("set")
                                    .then(Commands.argument("amount", IntegerArgumentType.integer(0, 100000))
                                            .executes(ctx -> executeCe(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "amount"), false, false))))
                            .then(Commands.literal("add")
                                    .then(Commands.argument("amount", IntegerArgumentType.integer())
                                            .executes(ctx -> executeCe(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "amount"), true, false))))
                            .then(Commands.literal("setmax")
                                    .then(Commands.argument("max", IntegerArgumentType.integer(1000, 100000))
                                            .executes(ctx -> executeCe(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "max"), false, true))))
                    )
                    .then(Commands.literal("animator")
                            .executes(context -> {
                                Minecraft.getInstance().execute(() -> {
                                    Minecraft.getInstance().setScreen(new KeyframesEditorScreen());
                                });
                                return 1;
                            }))
            );
        });
    }

    private static int executeCe(CommandSourceStack source, int value, boolean add, boolean isMax) {
        ServerPlayer player = source.getPlayer();
        if (player == null) return 0;

        PlayerCursedEnergy ce = CursedEnergyManager.get(player.getUUID());

        if (isMax) {
            ce.setMaxEnergy(value);
            source.sendSuccess(() -> Component.literal("Max CE set to " + value), true);
        } else if (add) {
            ce.addEnergy(value);
            source.sendSuccess(() -> Component.literal("Added " + value + " CE"), true);
        } else {
            ce.setEnergy(value);
            source.sendSuccess(() -> Component.literal("CE set to " + value), true);
        }
        return 1;
    }
}

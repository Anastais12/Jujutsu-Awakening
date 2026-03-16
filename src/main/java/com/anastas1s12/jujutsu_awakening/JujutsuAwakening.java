package com.anastas1s12.jujutsu_awakening;

import com.anastas1s12.jujutsu_awakening.commands.ModCommands;
import com.anastas1s12.jujutsu_awakening.cursed_energy.CursedEnergyManager;
import com.anastas1s12.jujutsu_awakening.cursed_energy.CursedEnergyTicker;
import com.anastas1s12.jujutsu_awakening.cursed_energy.PlayerCursedEnergy;
import com.anastas1s12.jujutsu_awakening.network.ModNetwork;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JujutsuAwakening implements ModInitializer {
	public static final String MOD_ID = "jujutsu_awakening";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		CursedEnergyTicker.register();

		ModCommands.registerCommands();

		ModNetwork.registerS2C();

		LOGGER.info("Jujutsu is Awakened");
	}
}
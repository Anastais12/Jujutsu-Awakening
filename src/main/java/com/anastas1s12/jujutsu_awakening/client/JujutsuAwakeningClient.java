package com.anastas1s12.jujutsu_awakening.client;

import com.anastas1s12.jujutsu_awakening.JujutsuAwakening;
import com.anastas1s12.jujutsu_awakening.client.hud.AbilityBarHud;
import com.anastas1s12.jujutsu_awakening.client.hud.CursedEnergyHud;
import com.anastas1s12.jujutsu_awakening.client.inputs.CombatModeKey;
import com.anastas1s12.jujutsu_awakening.network.ModNetwork;
import net.fabricmc.api.ClientModInitializer;

public class JujutsuAwakeningClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        CursedEnergyHud.register();
        AbilityBarHud.register();

        CombatModeKey.register();

        ModNetwork.registerC2S();

        JujutsuAwakening.LOGGER.info("Client Side is initialized");
    }
}

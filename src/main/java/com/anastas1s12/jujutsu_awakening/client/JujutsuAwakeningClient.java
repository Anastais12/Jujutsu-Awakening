package com.anastas1s12.jujutsu_awakening.client;

import com.anastas1s12.jujutsu_awakening.JujutsuAwakening;
import com.anastas1s12.jujutsu_awakening.client.hud.CombatHotbarHud;
import com.anastas1s12.jujutsu_awakening.client.hud.CursedEnergyHud;
import com.anastas1s12.jujutsu_awakening.client.hud.VanillaHotbarHud;
import com.anastas1s12.jujutsu_awakening.client.inputs.CombatModeKey;
import com.anastas1s12.jujutsu_awakening.client.inputs.InputHandler;
import net.fabricmc.api.ClientModInitializer;

public class JujutsuAwakeningClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        CursedEnergyHud.register();
        CombatHotbarHud.register();
        VanillaHotbarHud.register();

        InputHandler.register();
        CombatModeKey.register();

        JujutsuAwakening.LOGGER.info("Client Side is initialized");
    }
}

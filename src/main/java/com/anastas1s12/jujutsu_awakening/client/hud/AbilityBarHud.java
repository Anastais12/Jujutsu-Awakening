package com.anastas1s12.jujutsu_awakening.client.hud;

import com.anastas1s12.jujutsu_awakening.cursed_energy.CursedEnergyManager;
import com.anastas1s12.jujutsu_awakening.cursed_energy.PlayerCursedEnergy;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;

import static com.anastas1s12.jujutsu_awakening.JujutsuAwakening.MOD_ID;

public class AbilityBarHud {

    private static final Identifier BAR_ICON =
            Identifier.fromNamespaceAndPath(MOD_ID, "textures/gui/hotbar.png");
    private static final Identifier POINTER_ICON =
            Identifier.fromNamespaceAndPath(MOD_ID, "textures/gui/cursed_energy_inactive.png");

    public static void register() {
        HudElementRegistry.attachElementBefore(
                VanillaHudElements.HOTBAR,
                Identifier.fromNamespaceAndPath(MOD_ID, "ability_bar_hud"),
                AbilityBarHud::render
        );
    }

    public static void render(GuiGraphics context, DeltaTracker tickDelta) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) return;

        PlayerCursedEnergy ce = CursedEnergyManager.get(client.player.getUUID());
        boolean inCm = ce.isInCombatMode();

        int width = client.getWindow().getGuiScaledWidth();
        int height = client.getWindow().getGuiScaledHeight();
    }
}

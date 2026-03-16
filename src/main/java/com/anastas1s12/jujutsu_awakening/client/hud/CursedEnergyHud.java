package com.anastas1s12.jujutsu_awakening.client.hud;

import com.anastas1s12.jujutsu_awakening.cursed_energy.CursedEnergyManager;
import com.anastas1s12.jujutsu_awakening.cursed_energy.PlayerCursedEnergy;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

import static com.anastas1s12.jujutsu_awakening.JujutsuAwakening.MOD_ID;

public class CursedEnergyHud {
    private static final Identifier COMBAT_ICON =
            Identifier.fromNamespaceAndPath(MOD_ID, "textures/gui/ce/cursed_energy.png");
    private static final Identifier INACTIVE_ICON =
            Identifier.fromNamespaceAndPath(MOD_ID, "textures/gui/ce/cursed_energy_inactive.png");

    public static void register() {
        HudElementRegistry.attachElementBefore(
                VanillaHudElements.HOTBAR,
                Identifier.fromNamespaceAndPath(MOD_ID, "cursed_energy_hud"),
                CursedEnergyHud::render
        );
    }

    private static void render(GuiGraphics context, DeltaTracker tickDelta) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) return;

        PlayerCursedEnergy ce = CursedEnergyManager.get(client.player.getUUID());
        boolean inCm = ce.isInCombatMode();

        int width = client.getWindow().getGuiScaledWidth();
        int height = client.getWindow().getGuiScaledHeight();

        int iconX = width / 2 + 91 + 8;
        int iconY = height - 20;

        // TEXTURE SWITCH
        Identifier icon = inCm ? COMBAT_ICON : INACTIVE_ICON;
        context.blit(
                RenderPipelines.GUI_TEXTURED,
                icon,
                iconX,
                iconY,
                0, 0, 16, 16, 16, 16
        );

        // TEXT
        String text = ce.getEnergy() + " / " + ce.getMaxEnergy();
        int textX = iconX + 16 + 12;
        int textY = iconY + 4;

        int color = inCm ? 0xFF46b8fa : 0xFFAAAAAA;

        context.drawString(
                client.font,
                text,
                textX,
                textY,
                color,
                true
        );

        // Future ability slot indicator (will expand into full custom hotbar)
        if (inCm) {
            context.drawString(client.font, "Ability " + (ce.getSelectedAbility() + 1),
                    iconX + 16 + 12, iconY - 12, 0xFFFFFF, true);
        }
    }
}
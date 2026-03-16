package com.anastas1s12.jujutsu_awakening.client.hud;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import static com.anastas1s12.jujutsu_awakening.JujutsuAwakening.MOD_ID;

public class VanillaHotbarHud {
    private static final Identifier VANILLA_HOTBAR =
            Identifier.fromNamespaceAndPath("minecraft", "textures/gui/sprites/hud/hotbar.png");

    private static final Identifier HOTBAR_SELECTION =
            Identifier.fromNamespaceAndPath("minecraft", "textures/gui/sprites/hud/hotbar_selection.png");

    public static void register() {
        // Create a custom HudElement that replaces the vanilla one
        HudElement customElement = VanillaHotbarHud::render;

        // Use attachElementBefore with CHAT to place it at bottom left,
        // OR use the proper replace method if available in your Fabric version
        // For now, we'll attach before CHAT and handle positioning
        HudElementRegistry.attachElementBefore(
                VanillaHudElements.CHAT,
                Identifier.fromNamespaceAndPath(MOD_ID, "vanilla_hotbar_hud"),
                customElement
        );
    }

    private static void render(GuiGraphics context, DeltaTracker tickDelta) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) return;

        // Position at bottom left corner
        int x = 5; // Small padding from left edge
        int y = client.getWindow().getGuiScaledHeight() - 25; // Bottom with padding

        // Render vanilla hotbar texture (182x22) scaled down or at original size
        // Original is 182x22, we'll render at original size
        context.blit(
                RenderPipelines.GUI_TEXTURED,
                VANILLA_HOTBAR,
                x,
                y,
                0,
                0,
                182,
                22,
                182,
                22
        );

        // Render the 9 vanilla hotbar items
        var inventory = client.player.getInventory();

        for (int i = 0; i < 9; i++) {
            ItemStack stack = inventory.getItem(i);
            // Items are spaced 20 pixels apart in vanilla hotbar
            // First item at x+3, y+3 for proper centering in 20x20 slot
            int itemX = x + 3 + i * 20;
            int itemY = y + 3;

            context.renderItem(stack, itemX, itemY);
            context.renderItemDecorations(client.font, stack, itemX, itemY);
        }

        // Render selection indicator
        int selected = inventory.getSelectedSlot();
        int selectedX = x - 1 + selected * 20; // -1 for the 24x24 selection texture offset
        int selectedY = y - 1;

        context.blit(
                RenderPipelines.GUI_TEXTURED,
                HOTBAR_SELECTION,
                selectedX,
                selectedY,
                0,
                0,
                24,
                24,
                24,
                24
        );
    }
}
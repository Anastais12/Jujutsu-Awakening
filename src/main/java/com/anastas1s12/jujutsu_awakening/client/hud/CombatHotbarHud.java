package com.anastas1s12.jujutsu_awakening.client.hud;

import com.anastas1s12.jujutsu_awakening.cursed_energy.CursedEnergyManager;
import com.anastas1s12.jujutsu_awakening.cursed_energy.PlayerCursedEnergy;
import com.anastas1s12.jujutsu_awakening.network.s2c.CombatHotbarScrollPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import static com.anastas1s12.jujutsu_awakening.JujutsuAwakening.MOD_ID;

public class CombatHotbarHud {
    private static final Identifier CUSTOM_HOTBAR =
            Identifier.fromNamespaceAndPath(MOD_ID, "textures/gui/hotbar.png");

    private static final Identifier CUSTOM_SELECTION =
            Identifier.fromNamespaceAndPath(MOD_ID, "textures/gui/hotbar_selection.png");

    // Slot coordinates (left corner) - 8 slots for custom hotbar
    private static final int[][] SLOT_COORDINATES = {
            {32, 56},   // Slot 0
            {55, 56},   // Slot 1
            {78, 56},   // Slot 2
            {101, 56},  // Slot 3
            {124, 56},  // Slot 4
            {146, 56},  // Slot 5
            {170, 56},  // Slot 6
            {193, 56}   // Slot 7
    };

    // Animation state
    private static float animationProgress = 0.0f;
    private static boolean isOpening = false;
    private static boolean wasInCombat = false;
    private static final float ANIMATION_SPEED = 0.15f; // Adjust for faster/slower animation

    public static void register() {
        HudElementRegistry.attachElementBefore(
                VanillaHudElements.CHAT,
                Identifier.fromNamespaceAndPath(MOD_ID, "combat_hotbar_hud"),
                CombatHotbarHud::render
        );
    }

    public static void handleScroll(double scrollDelta) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) return;

        PlayerCursedEnergy ce = CursedEnergyManager.get(client.player.getUUID());
        if (!ce.isInCombatMode()) return;

        // Send packet to server to handle scroll
        int direction = scrollDelta > 0 ? -1 : 1; // Scroll up = previous, down = next
        ClientPlayNetworking.send(new CombatHotbarScrollPacket(direction));
    }

    private static void render(GuiGraphics context, DeltaTracker tickDelta) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) return;

        PlayerCursedEnergy ce = CursedEnergyManager.get(client.player.getUUID());
        boolean inCombat = ce.isInCombatMode();

        // Handle animation state
        if (inCombat && !wasInCombat) {
            isOpening = true;
            animationProgress = 0.0f;
        } else if (!inCombat && wasInCombat) {
            isOpening = false;
        }
        wasInCombat = inCombat;

        // Update animation progress
        if (isOpening && animationProgress < 1.0f) {
            animationProgress += ANIMATION_SPEED;
            if (animationProgress > 1.0f) animationProgress = 1.0f;
        } else if (!inCombat && animationProgress > 0.0f) {
            animationProgress -= ANIMATION_SPEED;
            if (animationProgress < 0.0f) animationProgress = 0.0f;
        }

        // Don't render if fully closed
        if (animationProgress <= 0.0f && !inCombat) return;

        int width = client.getWindow().getGuiScaledWidth();
        int height = client.getWindow().getGuiScaledHeight();

        // Calculate animated position (slide up from bottom)
        int baseY = height - 67;
        int animatedY = baseY + (int)((1.0f - animationProgress) * 67);

        // Custom hotbar render
        int customX = width / 2 - 122;
        int customY = animatedY;

        // Apply opacity based on animation
        float alpha = animationProgress;

        context.blit(
                RenderPipelines.GUI_TEXTURED,
                CUSTOM_HOTBAR,
                customX,
                customY,
                0,
                0,
                244,
                67,
                244,
                67
        );

        // Get the current custom hotbar slot from PlayerCursedEnergy
        int customSelectedSlot = ce.getCustomHotbarSlot(); // You'll need to add this method

        // Render selection textures for all 8 slots of custom hotbar
        for (int i = 0; i < 8; i++) {
            int slotX = customX + SLOT_COORDINATES[i][0];
            int slotY = customY + SLOT_COORDINATES[i][1];

            // Render selection texture (20x20) for each slot
            context.blit(
                    RenderPipelines.GUI_TEXTURED,
                    CUSTOM_SELECTION,
                    slotX,
                    slotY,
                    0,
                    0,
                    20,
                    20,
                    20,
                    20
            );
        }

        // Render the actual items in custom hotbar slots (if you have custom items)
        // This depends on how you store custom hotbar items - adjust as needed
        renderCustomHotbarItems(context, client, customX, customY);
    }

    private static void renderCustomHotbarItems(GuiGraphics context, Minecraft client, int baseX, int baseY) {
        // Get custom hotbar items from PlayerCursedEnergy or separate storage
        // This is placeholder - implement based on your actual data structure
        PlayerCursedEnergy ce = CursedEnergyManager.get(client.player.getUUID());

        for (int i = 0; i < 8; i++) {
            ItemStack stack = ce.getCustomHotbarItem(i); // You'll need to implement this

            if (!stack.isEmpty()) {
                int itemX = baseX + SLOT_COORDINATES[i][0] + 2; // Center in 20x20 slot
                int itemY = baseY + SLOT_COORDINATES[i][1] + 2;

                context.renderItem(stack, itemX, itemY);
                context.renderItemDecorations(client.font, stack, itemX, itemY);
            }
        }
    }
}
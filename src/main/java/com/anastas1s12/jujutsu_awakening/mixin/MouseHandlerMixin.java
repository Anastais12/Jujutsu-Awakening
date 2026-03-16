package com.anastas1s12.jujutsu_awakening.mixin;

import com.anastas1s12.jujutsu_awakening.client.hud.CombatHotbarHud;
import com.anastas1s12.jujutsu_awakening.cursed_energy.CursedEnergyManager;
import com.anastas1s12.jujutsu_awakening.cursed_energy.PlayerCursedEnergy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {

    @Inject(method = "onScroll", at = @At("HEAD"), cancellable = true)
    private void onScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) return;

        PlayerCursedEnergy ce = CursedEnergyManager.get(client.player.getUUID());

        // If in combat mode, handle scroll for custom hotbar
        if (ce.isInCombatMode()) {
            // Cancel vanilla hotbar scroll behavior
            ci.cancel();

            // Send to our custom handler
            CombatHotbarHud.handleScroll(vertical);
        }
        // Otherwise, let vanilla handle it (for 1-9 keys, vanilla scroll still works)
    }
}
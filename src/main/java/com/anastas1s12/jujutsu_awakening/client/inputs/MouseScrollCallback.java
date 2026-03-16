package com.anastas1s12.jujutsu_awakening.client.inputs;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;

public class MouseScrollCallback {
    private static double scrollDelta = 0;

    public static void register() {
        // Register at the Fabric level for mouse scroll
        // You'll need to use Fabric API's client events or GLFW callbacks

        // Alternative: Use Fabric's existing scroll event if available
        // Or mixin to MouseHandler
    }

    public static double getAndClearScroll() {
        double delta = scrollDelta;
        scrollDelta = 0;
        return delta;
    }

    public static void setScrollDelta(double delta) {
        scrollDelta = delta;
    }
}
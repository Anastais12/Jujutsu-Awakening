package com.anastas1s12.jujutsu_awakening.ability;


import net.minecraft.resources.Identifier;

public class Ability {
    public final String name;
    public final Identifier icon;
    public final Runnable onActivate;

    public Ability(String name, String iconPath, Runnable onActivate) {
        this.name = name;
        this.icon = Identifier.fromNamespaceAndPath("jujutsu_awakening", iconPath);
        this.onActivate = onActivate;
    }

    public void activate() {
        onActivate.run();
    }
}
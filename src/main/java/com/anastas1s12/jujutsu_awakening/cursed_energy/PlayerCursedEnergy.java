package com.anastas1s12.jujutsu_awakening.cursed_energy;

import com.mojang.serialization.DynamicOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

public class PlayerCursedEnergy {
    private int cursed_energy;
    private int maxCursed_Energy;
    private boolean inCombatMode = false;
    private int selectedAbility = 0;

    public PlayerCursedEnergy() {
        this.maxCursed_Energy = 100000;
        this.cursed_energy = maxCursed_Energy;
    }

    // COMBAT MODE
    public boolean isInCombatMode() { return inCombatMode; }
    public void setInCombatMode(boolean inCombatMode) { this.inCombatMode = inCombatMode; }

    // SELECTED ABILITY (1-9)
    public int getSelectedAbility() { return selectedAbility; }
    public void setSelectedAbility(int slot) {
        this.selectedAbility = Math.max(0, Math.min(8, slot));
    }

    // CE GETTERS / SETTERS
    public int getEnergy() { return cursed_energy; }
    public int getMaxEnergy() { return maxCursed_Energy; }

    public void setEnergy(int energy) {
        this.cursed_energy = Math.max(0, Math.min(maxCursed_Energy, energy));
    }

    public void addEnergy(int amount) { setEnergy(this.cursed_energy + amount); }

    public void setMaxEnergy(int max) {
        this.maxCursed_Energy = Math.max(1000, max);
        this.cursed_energy = Math.min(this.cursed_energy, this.maxCursed_Energy);
    }

    public boolean consume(int amount) {
        if (cursed_energy >= amount) {
            cursed_energy -= amount;
            return true;
        }
        return false;
    }

    private int customHotbarSlot = 0;
    private ItemStack[] customHotbarItems = new ItemStack[8];

    public int getCustomHotbarSlot() {
        return this.customHotbarSlot;
    }

    public void setCustomHotbarSlot(int slot) {
        if (slot >= 0 && slot < 8) {
            this.customHotbarSlot = slot;
        }
    }

    public ItemStack getCustomHotbarItem(int slot) {
        if (slot >= 0 && slot < 8) {
            return this.customHotbarItems[slot] != null ? this.customHotbarItems[slot] : ItemStack.EMPTY;
        }
        return ItemStack.EMPTY;
    }

    public void setCustomHotbarItem(int slot, ItemStack stack) {
        if (slot >= 0 && slot < 8) {
            this.customHotbarItems[slot] = stack;
        }
    }

    // NBT serialization for custom hotbar - FIXED for 1.21.1 using CODEC
    public void saveCustomHotbar(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putInt("CustomHotbarSlot", this.customHotbarSlot);
        ListTag items = new ListTag();

        DynamicOps<Tag> ops = registries.createSerializationContext(NbtOps.INSTANCE);

        for (int i = 0; i < 8; i++) {
            CompoundTag itemTag = new CompoundTag();
            if (customHotbarItems[i] != null && !customHotbarItems[i].isEmpty()) {
                // 1.21.1: Use ItemStack.CODEC to serialize
                ItemStack.CODEC.encodeStart(ops, customHotbarItems[i])
                        .result()
                        .ifPresent(encoded -> {
                            if (encoded instanceof CompoundTag) {
                                itemTag.put("item", encoded);
                            }
                        });
            }
            items.add(itemTag);
        }
        tag.put("CustomHotbarItems", items);
    }

    public void loadCustomHotbar(CompoundTag tag, HolderLookup.Provider registries) {
        // 1.21.1: getInt returns Optional<Integer>
        this.customHotbarSlot = tag.getInt("CustomHotbarSlot").orElse(0);

        // 1.21.1: getList returns Optional<ListTag>
        ListTag items = tag.getList("CustomHotbarItems").orElse(new ListTag());

        DynamicOps<Tag> ops = registries.createSerializationContext(NbtOps.INSTANCE);

        for (int i = 0; i < items.size() && i < 8; i++) {
            // 1.21.1: getCompound returns Optional<CompoundTag>
            CompoundTag itemTag = items.getCompound(i).orElse(new CompoundTag());

            if (itemTag.contains("item")) {
                Tag itemData = itemTag.get("item");
                // 1.21.1: Use ItemStack.CODEC to deserialize
                int finalI = i;
                ItemStack.CODEC.parse(ops, itemData)
                        .result()
                        .ifPresent(stack -> this.customHotbarItems[finalI] = stack);
            } else {
                this.customHotbarItems[i] = ItemStack.EMPTY;
            }
        }
    }
}
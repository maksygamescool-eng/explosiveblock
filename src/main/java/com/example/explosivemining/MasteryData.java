package com.example.explosivemining;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

/**
 * Tracks, per player and per block type, how many times that player has
 * mined that block. Stored inside the player's persistent NBT data
 * (Player#getPersistentData()), which Forge saves/loads with the player
 * automatically, so progress survives logging out and back in.
 */
public final class MasteryData {

    private static final String ROOT_KEY = "ExplosiveMiningMastery";

    private MasteryData() {
    }

    /** Increments the mine-count for this block type for this player, and returns the new total. */
    public static int incrementAndGet(Player player, ResourceLocation blockId) {
        CompoundTag persist = player.getPersistentData();
        CompoundTag root = persist.contains(ROOT_KEY) ? persist.getCompound(ROOT_KEY) : new CompoundTag();

        String key = blockId.toString();
        int newCount = (root.contains(key) ? root.getInt(key) : 0) + 1;
        root.putInt(key, newCount);

        persist.put(ROOT_KEY, root);
        return newCount;
    }

    /** Reads the current mine-count for this block type for this player without changing it. */
    public static int get(Player player, ResourceLocation blockId) {
        CompoundTag persist = player.getPersistentData();
        if (!persist.contains(ROOT_KEY)) {
            return 0;
        }
        CompoundTag root = persist.getCompound(ROOT_KEY);
        String key = blockId.toString();
        return root.contains(key) ? root.getInt(key) : 0;
    }
}

package com.example.explosivemining;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber(modid = ExplosiveMining.MODID)
public class MiningEventHandler {

    private static final Random RANDOM = new Random();

    // Bonus items handed out once a block type is "mastered". Tweak freely.
    private static final Item[] BONUS_LOOT_POOL = new Item[]{
            Items.DIAMOND,
            Items.EMERALD,
            Items.GOLD_INGOT,
            Items.IRON_INGOT,
            Items.LAPIS_LAZULI,
            Items.REDSTONE,
            Items.EXPERIENCE_BOTTLE
    };

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        if (player == null || player.isCreative() || player.isSpectator()) {
            return; // don't trigger from creative/spectator breaks
        }

        Level level = (Level) event.getLevel();
        if (level.isClientSide() || !(level instanceof ServerLevel serverLevel)) {
            return; // only run the real logic on the server
        }

        BlockState state = event.getState();
        Block block = state.getBlock();
        BlockPos origin = event.getPos();

        if (state.getDestroySpeed(level, origin) < 0) {
            return; // unbreakable block (bedrock etc.) - never touch these
        }

        ResourceLocation blockId = ForgeRegistries.BLOCKS.getKey(block);
        if (blockId == null) {
            return;
        }

        int timesMined = MasteryData.incrementAndGet(player, blockId);
        boolean mastered = timesMined >= Config.MASTERY_THRESHOLD.get();

        int size = mastered ? Config.MASTERY_AREA_SIZE.get() : Config.BASE_AREA_SIZE.get();
        int totalDrops = mastered ? Config.MASTERY_DROP_COUNT.get() : Config.BASE_DROP_COUNT.get();

        // The vanilla break event still proceeds after this handler runs, and
        // vanilla will give the normal single drop for the mined block. We only
        // need to add the *extra* copies to reach totalDrops, and separately
        // destroy the blocks around it.
        destroySurroundingArea(serverLevel, player, origin, size);
        triggerCosmeticExplosion(serverLevel, origin);

        ItemStack minedItem = new ItemStack(block.asItem());
        if (!minedItem.isEmpty()) {
            spawnItems(serverLevel, origin, minedItem, totalDrops - 1);
        }

        if (mastered) {
            Item bonus = BONUS_LOOT_POOL[RANDOM.nextInt(BONUS_LOOT_POOL.length)];
            spawnItems(serverLevel, origin, new ItemStack(bonus), 1);
        }
    }

    /** Destroys a size x size x size cube of blocks anchored at origin (origin itself is skipped; vanilla handles it). */
    private static void destroySurroundingArea(ServerLevel level, Player player, BlockPos origin, int size) {
        int half = size / 2;
        List<BlockPos> targets = new ArrayList<>();
        for (int dx = -half; dx < size - half; dx++) {
            for (int dy = -half; dy < size - half; dy++) {
                for (int dz = -half; dz < size - half; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) {
                        continue; // origin block is handled by the vanilla break itself
                    }
                    targets.add(origin.offset(dx, dy, dz));
                }
            }
        }

        for (BlockPos pos : targets) {
            BlockState targetState = level.getBlockState(pos);
            if (targetState.isAir()) {
                continue;
            }
            if (Config.SKIP_UNBREAKABLE_NEIGHBORS.get() && targetState.getDestroySpeed(level, pos) < 0) {
                continue;
            }
            // dropExp=true here just means "break it like a real mine", giving normal item drops.
            level.destroyBlock(pos, true, player);
        }
    }

    /** Cosmetic sound/particle burst. Block interaction is NONE since we handle destruction ourselves. */
    private static void triggerCosmeticExplosion(ServerLevel level, BlockPos origin) {
        level.explode(
                null,
                origin.getX() + 0.5,
                origin.getY() + 0.5,
                origin.getZ() + 0.5,
                Config.EXPLOSION_VISUAL_POWER.get().floatValue(),
                Config.DAMAGE_ENTITIES.get(),
                Level.ExplosionInteraction.NONE
        );
    }

    private static void spawnItems(ServerLevel level, BlockPos pos, ItemStack template, int amount) {
        if (amount <= 0 || template.isEmpty()) {
            return;
        }
        int maxStack = template.getMaxStackSize();
        int remaining = amount;
        while (remaining > 0) {
            int batch = Math.min(maxStack, remaining);
            ItemStack toDrop = template.copy();
            toDrop.setCount(batch);
            ItemEntity itemEntity = new ItemEntity(
                    level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, toDrop);
            level.addFreshEntity(itemEntity);
            remaining -= batch;
        }
    }
}

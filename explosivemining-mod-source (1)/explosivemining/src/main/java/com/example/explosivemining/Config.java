package com.example.explosivemining;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.IntValue BASE_AREA_SIZE = BUILDER
            .comment("Side length of the cube of blocks destroyed on a normal mine.",
                     "2 = a 2x2x2 crater around the mined block.")
            .defineInRange("baseAreaSize", 2, 1, 8);

    public static final ForgeConfigSpec.IntValue MASTERY_AREA_SIZE = BUILDER
            .comment("Side length of the cube of blocks destroyed once mastery is reached.",
                     "3 = a 3x3x3 crater around the mined block.")
            .defineInRange("masteryAreaSize", 3, 1, 8);

    public static final ForgeConfigSpec.IntValue BASE_DROP_COUNT = BUILDER
            .comment("Total copies of the mined block dropped before mastery is reached.")
            .defineInRange("baseDropCount", 4, 1, 64);

    public static final ForgeConfigSpec.IntValue MASTERY_DROP_COUNT = BUILDER
            .comment("Total copies of the mined block dropped once mastery is reached.")
            .defineInRange("masteryDropCount", 5, 1, 64);

    public static final ForgeConfigSpec.IntValue MASTERY_THRESHOLD = BUILDER
            .comment("Number of times a player must mine a specific block type",
                     "before that block type 'masters' for that player.")
            .defineInRange("masteryThreshold", 5, 1, 1000);

    public static final ForgeConfigSpec.BooleanValue DAMAGE_ENTITIES = BUILDER
            .comment("Whether the explosion effect can hurt nearby entities/players.",
                     "Block destruction is always handled manually and is unaffected by this.")
            .define("damageEntities", false);

    public static final ForgeConfigSpec.DoubleValue EXPLOSION_VISUAL_POWER = BUILDER
            .comment("Cosmetic 'power' of the explosion sound/particle effect.",
                     "Does not control how many blocks are destroyed (see areaSize options above).")
            .defineInRange("explosionVisualPower", 1.5, 0.1, 10.0);

    public static final ForgeConfigSpec.BooleanValue SKIP_UNBREAKABLE_NEIGHBORS = BUILDER
            .comment("If true, blocks like bedrock/obsidian-tier-unbreakable in the blast radius are left alone.",
                     "Recommended to keep this true.")
            .define("skipUnbreakableNeighbors", true);

    public static final ForgeConfigSpec SPEC = BUILDER.build();
}

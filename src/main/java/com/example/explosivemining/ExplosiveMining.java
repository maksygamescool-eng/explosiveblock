package com.example.explosivemining;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

/**
 * Explosive Mining
 *
 * Mining a block sets off a small explosion around it (clearing nearby blocks
 * and dropping extra copies of whatever you mined). Mine the same block type
 * enough times and it "masters": bigger explosion, more drops, plus one
 * random bonus item.
 *
 * All the tunable numbers live in {@link Config} (generated at
 * config/explosivemining-common.toml on first run).
 */
@Mod(ExplosiveMining.MODID)
public class ExplosiveMining {

    public static final String MODID = "explosivemining";

    public ExplosiveMining() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        // MiningEventHandler is annotated with @Mod.EventBusSubscriber, so Forge
        // automatically registers its static listeners on mod construction —
        // no manual registration needed here.
    }
}

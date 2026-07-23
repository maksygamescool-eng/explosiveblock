# Explosive Mining (Forge 26.1)

Mine a block → it "explodes," clearing a small crater of nearby blocks and
dropping extra copies of what you mined. Mine the *same block type* enough
times and it masters: bigger crater, more drops, plus one random bonus item.

Default numbers (all changeable in the generated config file):

| | Before mastery | After mastery (5 mines of that block, by default) |
|---|---|---|
| Crater size | 2x2x2 | 3x3x3 |
| Copies dropped | 4 | 5 + 1 random bonus item |

Progress is tracked **per player, per block type**, and is saved with the
player, so it survives logging out.

## Why you have to build this yourself

I wrote this in a sandboxed environment that can't reach Forge's or Mojang's
servers, so I can't download the Minecraft/Forge toolchain or produce a
compiled `.jar` here. You'll need to compile it locally — it's a normal
Gradle build once you have the real MDK.

Also: **Minecraft/Forge 26.1 is a very new release (post-dates my training),
so I can't guarantee every package name below is 100% exact for that build.**
I used the modding patterns that have been stable across Forge for years
(`BlockEvent.BreakEvent`, `ForgeConfigSpec`, `@Mod.EventBusSubscriber`, etc.),
but if the real 26.1 MDK renamed something, you may need a small import fix.

## How to build

1. Download the **Forge 26.1 MDK** from
   https://files.minecraftforge.net/net/minecraftforge/forge/index_26.1.html
   (click "Mdk") and unzip it into an empty folder.
2. Copy these files from this project **into that folder, overwriting the
   example mod**:
   - `src/main/java/com/example/explosivemining/*.java`
   - `src/main/resources/META-INF/mods.toml`
3. Open the MDK's own `gradle.properties` and update the mod info fields
   (`mod_id`, `mod_name`, `mod_version`, etc.) to match the values used in
   `mods.toml` here (already set to `explosivemining` / "Explosive Mining").
   If Gradle complains about the Forge version string, copy the exact
   `forge_version` from the MDK you downloaded — don't trust the number in
   this project's `gradle.properties`, verify it.
4. Build it:
   ```
   ./gradlew build
   ```
   (Linux/Mac) or `gradlew.bat build` (Windows). Requires JDK 25.
5. Your mod jar will be in `build/libs/`. Drop it in your `mods` folder
   alongside Forge 26.1.

## Tuning

After the mod loads once, edit `config/explosivemining-common.toml` in your
run/instance folder to change crater sizes, drop counts, the mastery
threshold, whether the explosion can hurt entities, and the bonus loot pool
(the pool itself is a short array in `MiningEventHandler.java` — edit and
recompile to change which items can drop).

## Notes / design choices

- Creative and spectator mode never trigger this (avoids chaos while building).
- Bedrock and other unbreakable blocks are always skipped, including as
  "neighbors" caught in the blast.
- The explosion itself does **not** damage terrain via vanilla TNT mechanics —
  blocks are broken manually (with real drops), and the `explode()` call is
  purely for sound/particles, so it won't ever eat blocks you didn't intend
  (like bedrock) or misbehave near liquids.

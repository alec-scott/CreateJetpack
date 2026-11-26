# Create Jetpack: Minecraft 26.1.2 Fabric port (on Create Fly)

| | |
|---|---|
| Base branch | upstream `main/neoforge/1.21.x` (5.2.1). Upstream's own Fabric code stops at 1.20.1 (`main/fabric/1.20.x`). |
| Target | Minecraft 26.1.2, Fabric Loader 0.20.1, Fabric API 0.155.3, Create Fly 6.0.9-4 (`../Create-Fly`) |
| Dependencies | Fabric Language Kotlin 1.14.2 (Kotlin 2.4.21), Forge Config API Port 26.1.5, Flight Lib (bundled, see below) |
| Licence | Code MIT, `assets/` All Rights Reserved (PssbleTrngle). Ask before publishing a jar. |

## Build

1. Flight Lib: `../FlightLib` (fork alec-scott/FlightLib, branch `26.1-fabric`), `./gradlew publishToMavenLocal`.
2. Create Fly jar at `../Create-Fly/build/libs/create-fly-26.1.2-6.0.9-4.jar`.
3. `JAVA_HOME=<JDK 25> ./gradlew build`, giving `build/libs/create_jetpack-fabric-<ver>.jar` with Flight Lib nested
   (jar-in-jar, as upstream).

Plain Fabric Loom (non-remapping) + Kotlin JVM replaces upstream's `com.possible-triangle.*` convention plugins.
Registrate datagen is not run: the committed `src/generated/resources` are used and edited by hand.

## Code changes

- **Registration**: Create Fly has no Registrate, so blocks, items, the block entity type and the recipe serializer are
  registered directly in `index/CJ*.kt`. Stress impact is copied from the copper backtank, as upstream.
- **Items**: Create Fly's `BacktankItem` is one `BlockItem` with an equippable component (no separate armour item and
  "placeable" item). The jetpacks follow suit: `JetpackItem(block, AllArmorMaterials.chest(...))`, so the upstream
  `jetpack_placeable` / `netherite_jetpack_placeable` helper items no longer exist. The netherite jetpack keeps the
  layered netherite diving armour texture (`LayeredArmorItem`). Rarity, fire resistance, creative tab entry (full air,
  after the netherite backtank) and Create-style tooltips are as upstream.
- **Block entity**: `JetpackBlockEntity` subclasses `BacktankBlockEntity` and reports `create_jetpack:jetpack` from
  `getType()` / `typeHolder()`, because Create Fly's constructor hard-codes `create:backtank`. The jetpack blocks are
  also added as valid blocks of `create:backtank` so the constructor's state check passes. Saved BEs use
  `create_jetpack:jetpack` (checked on the server).
- **Rendering**: Create's `BacktankRenderer`, plus a rotating shaft visual (copper or netherite shaft by block), as
  upstream. On the player's back Create Fly's backtank layer renders the jetpack block model, as upstream.
- **Enchantment filter** (`enchantments.list` / `is_blacklist` config): NeoForge's `supportsEnchantment` hook became
  Fabric's `EnchantmentEvents.ALLOW_ENCHANTING`.
- **Copy-components mechanical crafting** (backtank -> jetpack keeps air and enchantments): Create Fly's
  `MechanicalCraftingRecipe` is a final record, so the serializer `create_jetpack:copy_components_mechanical_crafting`
  reads a normal mechanical crafting recipe and remembers it (weak identity set), and `MechanicalCraftingRecipeMixin`
  copies the backtank's components onto `assemble`'s result for those recipes. Only the stack's component *patch* is
  copied: on 26.1 the full component map includes item-defining components (model, equippable), which would turn the
  jetpack into a backtank.
- **Config**: same `ModConfigSpec` code through Forge Config API Port; server-config sync payload over Fabric networking
  (sent on join and on config reload).
- **HUD**: `ControlsDisplay` is a Fabric `HudElement` after the hotbar; rewritten for the 26.1 GUI API (2D pose stack,
  pipeline blits, alpha in text colours).
- **Air check** (`JetpackItem.isUsable`): upstream only looked at the emptiest worn tank (Create sorts by air, lowest
  first), so a jetpack left on 1 air (hover costs 1, climbing 2) blocked flight even with a full backtank worn. It now
  accepts any worn tank with enough air for a climb tick (found in game 2026-11-26).
- **Key category**: Flight Lib's key category lang key is now `key.category.flightlib.jetpack` (26.1 key categories);
  Create Jetpack's lang files override its name with "Create Jetpack" as upstream did.

## Resources

- `assets/create_jetpack/items/*.json` item model definitions added (26.1).
- Recipes converted to 26.1 ingredient syntax (`"id"` / `"#tag"` strings).
- Placeable item models and lang entries removed (items no longer exist).

## Not ported / open questions

- **In-game config screen**: upstream registered Create's catnip `BaseConfigScreen`. Create Fly has no config UI, so
  there is no screen. Decided 2026-11-24 (user): no screen; settings are edited in the server's
  `config/create_jetpack-common.toml` (synced to players), defaults kept for now. Client prefs stay in `-client.toml`.
- **Trinkets slot** (2026-11-24): on Trinkets Updated 4.0.1 (Patbox, `eu.pb4.trinkets`, optional). Upstream only offered
  an opt-in Curios datapack; here the jetpacks are tagged for `chest/back` out of the box (`data/trinkets/...`).
  `JetpackItem.isValid` accepts `TrinketsSource` (upstream only accepted Curios, so its 1.20 Fabric Trinkets path never
  flew). `compat/TrinketsCompat` adds a Create backtank supplier so air in Trinkets slots counts (cosmetic slots skipped),
  and `client/compat/JetpackTrinketRenderer` draws the jetpack on the back like Create Fly's backtank layer (skipped
  when a backtank or jetpack is in the chest slot).
- Cold Sweat data files are kept (inert without Cold Sweat).

## Status

- [x] Builds (Flight Lib and Create Jetpack).
- [x] Dedicated server boot clean with the test server's mods (2026-11-24); `/reload` clean; blocks place and save
      as `create_jetpack:jetpack`; the block loot keeps stored air (`create:banktank_air`).
- [x] Client test run by Claude 2026-11-26 (results in `../port-testing/create-jetpack.md`); a few UI-only checks left
      for the user.

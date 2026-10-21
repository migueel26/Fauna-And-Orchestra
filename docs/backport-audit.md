# Backport audit — `forge-1.20.1` vs `develop` (NeoForge 1.21.1)

Comparison of **`origin/develop` @ `00f80c8`** ("Animal spawn rates are now configurable", 2026-10-21)
against **`forge-1.20.1` @ `85daea5`** (same title, same day).

Both tips implement the same feature set, so the audit went past the two tip commits and compared the
**whole trees**, since that is where forgotten backports actually show up. Loader-mandated differences
(Payload → Packet, `DeferredItem`/`DeferredHolder` → `RegistryObject`, data components → NBT keys,
`useItemOn` → `use`, `loadAdditional` → `load`, `neoforge:` → `forge:` loaders/tags, `MapCodec` →
`Codec`, `#c:` → `#forge:`/`#minecraft:` tags, convention-tag renames, and 1.21-only content such as
Breeze/Bogged/armadillo scute) are **not** listed as issues — they were verified as correct
translations and are summarised in [§5](#5-verified-in-sync).

Severity: **🔴 bug** (wrong behaviour/values) · **🟠 divergence** (works, but differs from `develop`
with no 1.20.1 reason) · **🟡 infra/cleanup**.

---

## 1. Real bugs

### 🔴 1.1 — Boots durability is wrong: the armour-material arrays are in 1.21 slot order
`item/custom/armor/ModArmorMaterials.java`

```java
private static final int[] BASE_DURABILITY = {13, 15, 16, 11};   // BOOTS, LEGGINGS, CHESTPLATE, HELMET
...
return BASE_DURABILITY[type.ordinal()] * this.durabilityMultiplier;
```

`ArmorItem.Type.ordinal()` in 1.20.1 is `HELMET(0), CHESTPLATE(1), LEGGINGS(2), BOOTS(3)` — verified
against `net/minecraft/world/item/ArmorItem.java` in the mapped 1.20.1 sources. Vanilla's
`ArmorMaterials.HEALTH_FUNCTION_FOR_TYPE` is `BOOTS=13, LEGGINGS=15, CHESTPLATE=16, HELMET=11`, so the
array above was copied in the `EnumMap` insertion order from `develop` and is **reversed** relative to
the ordinal it is indexed with. `BOOTS` therefore gets `11`, not `13`:

| Item | forge-1.20.1 | develop (`Type.BOOTS.getDurability(n)` = `13 * n`) |
|---|---|---|
| `fluffy_boots` (×25) | **275** | 325 |
| `floral_boots` (×50) | **550** | 650 |

`protectionAmounts = {1, 2, 3, 1}` has the same problem: it yields `CHESTPLATE 2 / LEGGINGS 3` whereas
`develop` defines `CHESTPLATE 3 / LEGGINGS 2`. Harmless today (only boots exist, and boots = 1 either
way) but it will be wrong the moment a chestplate or leggings is added.

**Fix:** reorder both arrays to helmet-first — `BASE_DURABILITY = {11, 16, 15, 13}` and
`protectionAmounts = {1, 3, 2, 1}` — or index them with an `EnumMap` like vanilla does.

### 🔴 1.2 — Frog Flute makes frogs run at 4× speed
`item/custom/FrogFluteItem.java:48`

```java
// develop:  moveTo(x, y, z, 4, 1.0f)   → accuracy = 4, speed = 1.0
frog.getNavigation().moveTo(player.getX(), player.getY(), player.getZ(), 4F);
```

1.20.1 has no 5-arg `moveTo`, so the `4` landed in the `double speed` slot of
`moveTo(double, double, double, double)`. The accuracy argument was dropped and the speed modifier
became **4.0 instead of 1.0**. Use `moveTo(x, y, z, 1.0D)` (1.20.1 has no accuracy overload; accuracy
`4` only relaxed the path target, so dropping it is fine).

`item/custom/HarmonicaItem.java:72` has the same shape (`moveTo(..., 1)`) but there the value happens
to equal the intended speed, so it is correct.

### 🔴 1.3 — Termite Chest drops its contents on every state change
`block/custom/TermiteChestBlock.java` (`onRemove`)

```java
// develop
Containers.dropContentsOnDestroy(state, newState, level, pos);
// forge
if (level.getBlockEntity(pos) instanceof TermiteChestBlockEntity be)
    Containers.dropContents(level, pos, be.container);
```

`dropContentsOnDestroy` is guarded by `!state.is(newState.getBlock())`; the unguarded call drops the
inventory for *any* state replacement at that position, not just removal — an item-duplication path if
the blockstate is ever changed in place (another mod, a structure, a future property).

**Correction:** `dropContentsOnDestroy` is *not* a drop-in replacement here, and `develop` is the buggier
side. It only acts when the block entity itself `instanceof Container`, and `TermiteChestBlockEntity`
implements `MenuProvider, GeoBlockEntity` and merely *holds* a `SimpleContainer` field — on both
branches. So on NeoForge the Termite Chest **never** drops its contents; this branch's explicit
`dropContents(level, pos, be.container)` is the version that actually works and only wants the guard.
Fixed by adding `!newState.is(state.getBlock())` to the existing call; `develop` needs the same
explicit call to drop anything at all.

### 🔴 1.4 — Dispensed Boogie/Discord Bombs lost their custom power and spread
`FaunaAndOrchestra.java:116-127`

`develop` implements `ProjectileItem` on both bomb items with
`uncertainty × 0.5`, `power × 1.25`. The forge port registers plain
`AbstractProjectileDispenseBehavior` subclasses, which keep vanilla's `getPower() = 1.1` /
`getUncertainty() = 6.0`. Dispensed bombs fly slower and much less accurately than on NeoForge.
Override `getPower()`/`getUncertainty()` in both anonymous classes (`1.1f * 1.25f`, `6.0f * 0.5f`).

### 🔴 1.5 — Anya's alternate dialogue line is unreachable
`screen/custom/AnyaScreen.java`

```java
// develop
Component.translatable("dialogue.faunaandorchestra.anya" + (Config.giveBook ? "1" : "1b"))
// forge
Component.translatable("dialogue.faunaandorchestra.anya1")
```

`dialogue.faunaandorchestra.anya1b` is present in both `en_us.json` and `es_es.json` on this branch but
can never be shown: with `give_book = false` the player still gets the "here's the book" line.

### 🔴 1.6 — Sea Lions lost half their spawn biomes
`data/faunaandorchestra/tags/worldgen/biomes/spawns_sea_lion.json`

`develop` lists `#c:is_beach` **and** `#c:is_shallow_ocean`; the forge file only has
`#minecraft:is_beach`. 1.20.1 has no `is_shallow_ocean` equivalent in either the vanilla or the Forge
biome tags, so the entry was dropped rather than mapped. Either add `#minecraft:is_ocean` (wider than
intended) or ship a mod-owned tag listing the shallow ocean biomes
(`ocean`, `warm_ocean`, `lukewarm_ocean`, `cold_ocean`, `frozen_ocean`).

### 🔴 1.7 — Two structures lost `project_start_to_heightmap`
`data/faunaandorchestra/worldgen/structure/ringtails.json`, `termite_nest.json`

Both are missing `"project_start_to_heightmap": "WORLD_SURFACE_WG"`, which `develop` sets on all five
structures and which the other three forge files (`inn`, `mothers_pond`, `composer_graveyard`) still
have. The field **is** supported here —
`TerrainAdjustedStructure.CODEC` declares `Heightmap.Types.CODEC.optionalFieldOf("project_start_to_heightmap")`
and passes it to `JigsawPlacement.addPieces` — so these two structures generate without heightmap
projection. Add the field back to both.

---

## 2. Behavioural divergences (no 1.20.1 reason)

### 🟠 2.1 — Entity hitboxes
`entity/ModEntities.java`

| Entity | develop | forge-1.20.1 |
|---|---|---|
| `penguin` | `sized(0.75f, 1.0f)` | `sized(0.75f, 0.75f)` |
| `wandering_koala` | `sized(0.6f, 1.25f)` | `sized(0.5f, 1.25f)` |

Every other entity's dimensions match.

### 🟠 2.2 — `LookAtPlayerGoal` priority (Lemur + Beaver)
`entity/custom/LemurEntity.java`, `entity/custom/BeaverEntity.java`

`develop` adds the `Player`-targeted `LookAtPlayerGoal` at priority **3**; forge uses **6**. Since
`FaunaRandomLookAroundGoal` sits at priority 5, the look-at-player behaviour is effectively dead on
this branch. (The `TravellingMusician`-targeted copy is priority 3 on both, which confirms 3 is the
intended value — `develop` even carries the comment `// LookAtPlayerGoal (3, TravellingMusician)`.)

### 🟠 2.3 — Player Canon melee goal
`entity/custom/PlayerCanonEntity.java`

```java
// develop: new MeleeAttackGoal(this, 1.0,  true)
// forge:   new MeleeAttackGoal(this, 1.25D, false)
```
Different chase speed **and** a flipped `followingTargetEvenIfNotSeen`.

### 🟠 2.4 — Swim-speed attribute dropped instead of mapped
`entity/custom/SeaLionEntity.java`, `entity/custom/boss/TheGreatComposer.java`

Both lose `.add(Attributes.WATER_MOVEMENT_EFFICIENCY, 1.0D)` with nothing in its place, while
`BeaverEntity` correctly maps it to `.add(ForgeMod.SWIM_SPEED.get(), 1.0D)`. The Sea Lion in
particular swims noticeably slower here than on NeoForge.

### 🟠 2.5 — Butterfly shadow radius
`client/entity/ButterflyRenderer.java` — `width * 0.65F` vs `width * 0.35F` on `develop`.

### 🟠 2.6 — Pan Flute: operator precedence and cooldown target
`item/custom/PanFluteItem.java`

```java
} else if (powers != null && !powers.isEmpty() || currentSound >= 0) {
```
Parses as `(powers != null && !powers.isEmpty()) || (currentSound >= 0)`; `currentSound` is an `int`
defaulting to `0`, so the right-hand side is always true and the branch is taken unconditionally —
`develop`'s intent was `currentSound != null && powers != null`. Add the parentheses / restore the
`&&`. Separately, the cooldown is applied to `this` instead of `ModItems.PAN_FLUTE.get()`, which
changes behaviour for `pan_flute_creative` (each item gets its own cooldown instead of sharing the
regular flute's).

### 🟠 2.7 — Quirky Frog jump power ignores the block jump factor
`entity/custom/QuirkyFrogEntity.java`

`develop` calls `super.getJumpPower(f / 0.42F)`, i.e. `f * getBlockJumpFactor() + jumpBoostPower`.
Forge uses `f + this.getJumpBoostPower()`, dropping `getBlockJumpFactor()` — the frog no longer jumps
short on honey blocks. 1.20.1 equivalent: `f * this.getBlockJumpFactor() + this.getJumpBoostPower()`.

### 🟠 2.8 — Voice items are "renamed" rather than named
`util/VesselUtil.java`

`develop` sets `DataComponents.ITEM_NAME`; forge uses `stack.setHoverName(...)`, which is the *custom*
name — it renders in italics and counts as a player-renamed stack. 1.20.1 has no `ITEM_NAME`
equivalent, so this may be deliberate, but it is a visible difference. Overriding
`Item#getName(ItemStack)` would match `develop` more closely.

### 🟠 2.9 — Smaller sound/navigation substitutions
- `entity/custom/MantisEntity.java`: `mob.makeSound(...)` → `mob.playSound(...)`; `makeSound` applies
  the mob's ambient volume/pitch, `playSound` does not. Same change in `AbstractKoalaMerchant`.
- `item/custom/HarmonicaItem.java`: `termiteBaby.stopInPlace()` → `getNavigation().stop()`. 1.20.1 has
  no `stopInPlace`, but the port also drops the zeroing of `xxa`/`yya`/`zza` and the jump control,
  so the termite can keep drifting for a tick.

### 🟠 2.10 — Container reach check
`screen/custom/*Menu.java` — `player.canInteractWithEntity(entity, 4.0)` became
`entity.distanceToSqr(player) < 32.0D` (≈5.66 blocks, measured from the entity centre rather than its
bounding box). Functionally close; noted only so the number isn't mistaken for a typo.

---

## 3. Infrastructure not backported

### 🟡 3.1 — The CI server-test job is missing
`.github/workflows/build.yml`

`develop` (`15a68c9`, `956c6ec`) added a `server-test` job that runs `./gradlew runServer --no-daemon`
with `FAUNA_IS_TEST_SERVER=true`, uploads `run/crash-reports/` + `run/logs/` on failure, and gates
`build` behind it (`needs: server-test`). This branch's workflow still only runs `./gradlew build`.
This is the single highest-value thing to port: it is exactly the guard that catches client-only code
leaking into common code, and that risk is *higher* here (e.g. `AbstractCanonEntity.setSkin` touches
`Minecraft.getInstance()` from an entity class).

### 🟡 3.2 — `CITestHandler` still keys off the generic `CI` variable
`event/CITestHandler.java:13` — `System.getenv("CI")`. `develop` renamed this to
`FAUNA_IS_TEST_SERVER` (`956c6ec`) precisely because `CI` is set by GitHub Actions for *every* job.
Harmless while 3.1 is unported; it must be fixed together with 3.1.

### 🟡 3.3 — `mods.toml` hardcodes what `processResources` is supposed to expand
`src/main/resources/META-INF/mods.toml`

`modId`, `displayName`, `authors`, `description`, `loaderVersion` and the Minecraft version range are
written literally instead of using `${mod_id}`, `${mod_name}`, `${mod_authors}`, `${mod_description}`,
`${loader_version_range}`, `${minecraft_version_range}`. Consequences:

- `versionRange = "[1.20.1,1.20.2)"` already contradicts `minecraft_version_range=[1.20.1, 1.20.6)` in
  `gradle.properties`.
- `mod_description` was reworded on `develop`; the two texts match today only by luck, and the
  in-file copy will silently drift.
- `authors = "Owlest"` vs `mod_authors` in `gradle.properties`.

Only `${mod_license}` and `${mod_version}` are actually templated.

### 🟡 3.4 — Executable bit on 141 resource files
130 of the 165 "modified" resource files between the branches differ **only** in file mode: `100755`
here vs `100644` on `develop` (all the `.ogg`, `.png`, `.geo.json`, `.animation.json` assets). Content
is byte-identical. Worth a one-off `git update-index --chmod=-x` sweep so future tree diffs stay
readable.

### 🟡 3.5 — Leftover Parchment properties
`gradle.properties` still carries `mapping_channel=parchment` / `mapping_version=2023.09.03-1.20.1`
while `build.gradle` uses `mappings channel: 'official'`. Already noted in `CLAUDE.md`; listed here for
completeness.

---

## 4. Cleanup on this branch

| Where | What |
|---|---|
| `recipe/DiscordRecipe.java` (Serializer), `block/custom/SingingCropBlock.java`, `recipe/NaturalRecipe.java` | Commented-out NeoForge `MapCodec`/`StreamCodec` blocks still in place |
| `item/custom/AbstractGeoItem.java` | Unused import `MelomancyCauldronItemRenderer` |
| `FaunaAndOrchestra.java:73` | Comment references `META-INF/neoforge.mods.toml` |
| `item/custom/PanFluteItem.java:66` | `// CHANGE THIS METHOD` marker (see 2.6) |
| `data/faunaandorchestra/painting_variant/ringtails_poster.json` | Dead file. In 1.20.1 painting variants are a built-in registry and `item/ModPaintings.java` already registers `ringtails_poster`; this JSON is never read |
| `worldgen/structure/*.json` (all 5) | `"liquid_settings": "apply_waterlogging"` is a 1.21 field; `TerrainAdjustedStructure.CODEC` has no such field, so it is silently ignored |
| `item/ModCreativeModeTabs.java` | Tab registry name is `fauna_and_orchestra_tab` vs `fauna_and_orchestra` on `develop`. Title is a literal so nothing visible breaks, but the registry ids differ across branches |
| `block/custom/spawners/PaintingSpawnerBlock.java` | Hardcoded feedback text (`"§aSelected: §e"`) instead of a lang key — and `develop`'s copy is hardcoded *in Spanish* (`"§aCuadro seleccionado: §e"`). Worth turning into a translation key on both sides |

### Shared bug (present on both branches, not a backport issue)
`util/ClientUtil.java:34` — `bone.setHidden(wornHat != boneHat || isHalloween());` does not consult
`Config.halloweenPumpkins`, while line 39 does. On 31 October with `halloween_hats = false` the real
hats are hidden and no pumpkin replaces them.

---

## 5. Worth porting *back* to `develop`

1. `assets/faunaandorchestra/textures/entity/butterfly.png` exists only on `forge-1.20.1`
   (commit "Added blue butterfly texture to git"); it is missing from `develop`.
2. `geo/item/harmonica.geo.json` has `"identifier": "geometry.unknown"` on `develop`;
   this branch has the correct `"geometry.harmonica"`.
3. `develop`'s `neoforge.mods.toml` declares only `geckolib` + `minecraft`. This branch also declares
   `patchouli` (mandatory), `jei` (optional, client) and `forge` — a better dependency list.
4. `develop` registers a `faunaandorchestra:add_graveyard` loot modifier
   (`loot_tables/AddGraveyardItemModifier.java`) that no datapack entry uses — dead code. This branch
   correctly omits it; nothing to do here, but `develop` could drop the class.

---

## 6. Verified in sync

Checked exhaustively and found **no** discrepancies beyond the loader/version translations:

- **Registries** — every `ModX.register("id", …)` id set matches (items, blocks, block entities,
  entities, sounds, effects, potions, particles, menus, recipes, structures, creative tab, biome
  modifier serialiser). Only expected deltas: `ModIngredientTypes`/`add_graveyard` (develop-only),
  `ModPaintings` (forge-only).
- **`ModX.register(...)` call list** in the mod constructor — all present, plus `ModNetwork`,
  `ModAdvancements`, `ModCompostingData` in `commonSetup`.
- **Lang** — 952 keys in `en_us.json` and 952 in `es_es.json` on both branches, no key only on one
  side. The only two value differences are the intentional Breeze → Snow Golem (Gust) and
  Breeze → Rabbit (Zephyr) swaps, and they match `util/VesselUtil.java` on both sides.
- **Recipes** — all 87 compared semantically (ingredients, tags, patterns, counts, results, NBT vs
  components). The only difference is the deliberate `c:foods/raw_fish` → `forge:raw_fishes` mapping
  for `taiyaki`, with the matching `data/forge/tags/items/raw_fishes.json`.
- **Advancements** — all 39 compared field-by-field; only `wear_costume.json` differs, and that is the
  correct 1.20.1 `"tag"` form of 1.21's `"items": "#tag"`.
- **Loot tables, loot modifiers, item/block/entity-type tags, `global_loot_modifiers.json`,
  `sounds.json`, Patchouli book (categories + entries), geo/animation/texture/model assets** — all
  byte-identical or version-format-only differences.
- **Networking** — 13 packets registered on each side with matching directions, and the same 13
  handler methods across `Client`/`ServerPacketHandler`. The payloads `develop` deleted in `016eb4e`
  (`ShowDialogueS2C`, `SyncTipCaseOwnerC2S`) are absent here too.
- **Client registration** — identical sets of menu screens, entity renderers, block-entity renderers,
  particle providers and GeckoLib item renderers (15 each).
- **`ModEventBusEvents`** — identical entity-attribute list and identical spawn placements
  (entity, placement type, heightmap, predicate) for all 10 entities.
- **Composting** — `ModCompostingData` matches `develop`'s `compostables.json` data map
  (`cotton_seeds` 0.3, `cotton` 0.65).
- **Forge-bus / client-bus event handlers**, **`util/ModTags`**, **`compat/FaunaJEIPlugin`**,
  **`entity/trades/KoalaTrades`** (every trade, cost and multiplier), **`Config`**, the mixin config
  (plus the forge-only `MixinEditBox`), and the **last-commit feature itself**
  (`ConfigurableSpawnsBiomeModifier`, `ModBiomeModifiers`, the 9 biome-modifier JSONs and the
  `animal_spawns` config block) — all faithful.
- The `develop` fixes with no same-named commit here were each checked in the tree and **are** ported:
  the GeoItemRenderer multiplayer crash fix (`ModItemRenderers` + `AbstractGeoItem`), the
  `MailboxBlock`/`OwnableBlockEntity` server crash, the Macaw cape geo/texture, the second-phase laugh
  attack difficulty, `TermiteMoundBlock.onRemove`, the `ListenerContainerBlock` debug-code removal, the
  `ListenerContainerBlockEntity` `HasNotified` persistence, the TGC title auto-translate crash, and the
  JEI output-slot/texture fix. The only unported piece of "Minor varied fixes" is
  §1.3 (`dropContentsOnDestroy`).

---

## 7. Suggested order of work

1. §1.1 boots durability, §1.2 Frog Flute speed, §1.7 structure heightmap — one-line fixes, wrong values today.
2. §3.1 + §3.2 CI server test — cheap, and prevents the next class of backport bug.
3. §1.3 Termite Chest drop guard, §1.4 dispenser config, §1.5 Anya dialogue, §1.6 Sea Lion biomes.
4. §2.1–§2.5 numeric/goal divergences — decide per item whether `develop` or this branch is right, then sync the other.
5. §3.3 `mods.toml` placeholders and §3.4 file modes.
6. §4 cleanup, then §5 back-ports to `develop`.

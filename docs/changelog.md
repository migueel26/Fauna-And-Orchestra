# Changelog

## Unreleased — backport fixes

Result of the `develop` ↔ `forge-1.20.1` audit in [`backport-audit.md`](backport-audit.md).

### Fixed

- **Armor materials** — the durability and protection arrays were in 1.21 slot order but indexed with
  1.20.1's `ArmorItem.Type.ordinal()`. Fluffy Boots 275 → 325, Floral Boots 550 → 650. (`193e14e`)
- **Frog Flute** — the dropped accuracy argument had landed in the `speed` slot of `moveTo`, so called
  frogs ran at 4× speed. (`57b3972`)
- **Termite Chest** — only drops its contents when the block is really removed, not on any state
  change. (`08b9e98`)
- **Dispensed Boogie/Discord Bombs** — restored the 1.25× power and 0.5× spread lost with
  `ProjectileItem`. (`6d34358`)
- **Anya** — shows `dialogue.…anya1b` when `give_book` is disabled; the string existed but was
  unreachable. (`11ad7b9`)
- **Sea Lions** — spawn in shallow oceans again (`c:is_shallow_ocean` had no 1.20.1 counterpart and was
  dropped). (`ee36ba0`)
- **Ringtails + Termite Nest** — restored `project_start_to_heightmap: WORLD_SURFACE_WG`. (`27dee38`)
- **Beavers and Lemurs** — `LookAtPlayerGoal` moved from priority 6 back to 3, so it is no longer
  outranked by the random look-around goal. (`c2bd118`)
- **Sea Lion and The Great Composer** — added `ForgeMod.SWIM_SPEED`, the 1.20.1 equivalent of the
  `WATER_MOVEMENT_EFFICIENCY` attribute that was dropped. (`7f6f4a7`)
- **Pan Flute** — fixed the always-true `&&`/`||` condition and made the cooldown apply to the Pan
  Flute item, so the creative one shares it. (`87371a1`)
- **Quirky Frog** — jump power takes the block jump factor into account again (honey blocks). (`960cfd0`)
- **Butterfly** — shadow radius back to `width * 0.35`. (`327f2c8`)
- **Voices** — named through `Item#getName` instead of `setHoverName`, so they are no longer italic
  "renamed" stacks. (`b496634`)

### Changed

- **CI** — backported the `server-test` job (`runServer --no-daemon`, crash/log upload, `build` gated
  behind it) and renamed `CITestHandler`'s variable from the generic `CI` to `FAUNA_IS_TEST_SERVER`.
  Added an EULA step the NeoForge side does not need. (`b517af9`)
- **`mods.toml`** — uses the `gradle.properties` values (`${mod_id}`, `${mod_name}`, `${mod_authors}`,
  `${mod_description}`, `${loader_version_range}`, `${minecraft_version_range}`,
  `${forge_version_range}`) instead of hardcoded copies. `minecraft_version_range` tightened to
  `[1.20.1,1.20.2)` to keep the range the file already declared. (`1529bd8`)

### Not changed (decided against)

- Penguin and Wandering Koala hitboxes.
- Player Canon `MeleeAttackGoal` speed and `followingTargetEvenIfNotSeen`.
- `makeSound`/`stopInPlace` substitutions in Mantis, Koala merchants and Harmonica.
- Container reach check in the entity menus.
- Executable bit on 141 resource files.
- Code cleanup: commented-out NeoForge codecs, unused import, dead `painting_variant` JSON, ignored
  `liquid_settings`.

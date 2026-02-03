package net.migueel26.faunaandorchestra.entity;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.custom.*;
import net.migueel26.faunaandorchestra.entity.custom.boss.ComposerCanonEntity;
import net.migueel26.faunaandorchestra.entity.custom.boss.TheGreatComposer;
import net.migueel26.faunaandorchestra.entity.custom.projectile.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, FaunaAndOrchestra.MOD_ID);

    public  static final Supplier<EntityType<MantisEntity>> MANTIS = ENTITY_TYPES.register("mantis",
            () -> EntityType.Builder.of(MantisEntity::new, MobCategory.CREATURE).sized(1f, 2.25f).build("mantis"));
    public static final Supplier<EntityType<PenguinEntity>> PENGUIN = ENTITY_TYPES.register("penguin",
            () -> EntityType.Builder.of(PenguinEntity::new, MobCategory.CREATURE).sized(0.75f, 0.75f).build("penguin"));

    public static final Supplier<EntityType<EmperorPenguinEntity>> EMPEROR_PENGUIN = ENTITY_TYPES.register("emperor_penguin",
            () -> EntityType.Builder.of(EmperorPenguinEntity::new, MobCategory.CREATURE).sized(0.75f, 1.0f).build("emperor_penguin"));

    public static final Supplier<EntityType<RedPandaEntity>> RED_PANDA = ENTITY_TYPES.register("red_panda",
            () -> EntityType.Builder.of(RedPandaEntity::new, MobCategory.CREATURE).sized(0.75f, 1f).build("red_panda"));
    public static final Supplier<EntityType<MacawEntity>> MACAW = ENTITY_TYPES.register("macaw",
            () -> EntityType.Builder.of(MacawEntity::new, MobCategory.CREATURE).sized(0.5f, 0.75f).build("macaw"));
    public static final Supplier<EntityType<BeaverEntity>> BEAVER = ENTITY_TYPES.register("beaver",
            () -> EntityType.Builder.of(BeaverEntity::new, MobCategory.CREATURE).sized(0.75f, 0.75f).build("beaver"));
    public static final Supplier<EntityType<LemurEntity>> LEMUR = ENTITY_TYPES.register("lemur",
            () -> EntityType.Builder.of(LemurEntity::new, MobCategory.CREATURE).sized(0.75f, 0.75f).build("lemur"));
    public static final Supplier<EntityType<MadameButterflyEntity>> MADAME_BUTTERFLY = ENTITY_TYPES.register("madame_butterfly",
            () -> EntityType.Builder.of(MadameButterflyEntity::new, MobCategory.CREATURE).sized(0.5f, 0.5f).build("madame_butterfly"));
    public  static final Supplier<EntityType<QuirkyFrogEntity>> QUIRKY_FROG = ENTITY_TYPES.register("quirky_frog",
            () -> EntityType.Builder.of(QuirkyFrogEntity::new, MobCategory.CREATURE).sized(0.75f, 0.75f).build("quirky_frog"));

    public static final Supplier<EntityType<Faust>> FAUST = ENTITY_TYPES.register("faust",
            () -> EntityType.Builder.of(Faust::new, MobCategory.CREATURE).sized(0.75f, 1.5f).build("faust"));
    public static final Supplier<EntityType<Orion>> ORION = ENTITY_TYPES.register("orion",
            () -> EntityType.Builder.of(Orion::new, MobCategory.CREATURE).sized(0.75f, 1.5f).build("orion"));
    public static final Supplier<EntityType<AnyaGhost>> ANYA_GHOST = ENTITY_TYPES.register("anya_ghost",
            () -> EntityType.Builder.of(AnyaGhost::new, MobCategory.CREATURE).sized(0.6f, 1.8f).build("anya_ghost"));
    public static final Supplier<EntityType<WiseTree>> WISE_TREE = ENTITY_TYPES.register("wise_tree",
            () -> EntityType.Builder.of(WiseTree::new, MobCategory.CREATURE).sized(0.6f, 1.0f).build("wise_tree"));


    public static final Supplier<EntityType<WanderingKoalaEntity>> WANDERING_KOALA = ENTITY_TYPES.register("wandering_koala",
            () -> EntityType.Builder.of(WanderingKoalaEntity::new, MobCategory.CREATURE).sized(0.5f, 1.25f).build("wandering_koala"));
    public static final Supplier<EntityType<SproutlingEntity>> SINGING_SPROUTLING = ENTITY_TYPES.register("singing_sproutling",
            () -> EntityType.Builder.of(SproutlingEntity::new, MobCategory.CREATURE).sized(0.35f, 0.45f).build("singing_sproutling"));
    public static final Supplier<EntityType<ButterflyEntity>> BUTTERFLY = ENTITY_TYPES.register("butterfly",
            () -> EntityType.Builder.of(ButterflyEntity::new, MobCategory.CREATURE).sized(0.35f, 0.35f).build("butterfly"));
    public static final Supplier<EntityType<WanderingNoteEntity>> WANDERING_NOTE = ENTITY_TYPES.register("wandering_note_entity",
            () -> EntityType.Builder.of(WanderingNoteEntity::new, MobCategory.AMBIENT).sized(1.0f, 1.0f).build("wandering_note_entity"));

    public static final Supplier<EntityType<MusicNoteProjectileEntity>> MUSIC_NOTE_PROJECTILE = ENTITY_TYPES.register("music_note_projectile",
            () -> EntityType.Builder.<MusicNoteProjectileEntity>of(MusicNoteProjectileEntity::new, MobCategory.MISC).sized(1.0f, 1.0f).build("music_note_projectile"));
    public static final Supplier<EntityType<PhantomNoteProjectileEntity>> PHANTOM_NOTE_PROJECTILE = ENTITY_TYPES.register("phantom_note_projectile",
            () -> EntityType.Builder.<PhantomNoteProjectileEntity>of(PhantomNoteProjectileEntity::new, MobCategory.MISC).sized(1.0f, 1.0f).build("phantom_note_projectile"));
    public static final Supplier<EntityType<SensorNote>> SENSOR_NOTE = ENTITY_TYPES.register("sensor_note",
            () -> EntityType.Builder.<SensorNote>of(SensorNote::new, MobCategory.MISC).sized(1.0f, 1.0f).build("sensor_note"));
    public static final Supplier<EntityType<ThrownBoogieBomb>> THROWN_BOOGIE_BOMB = ENTITY_TYPES.register("thrown_boogie_bomb",
            () -> EntityType.Builder.<ThrownBoogieBomb>of(ThrownBoogieBomb::new, MobCategory.MISC).sized(1.0f, 1.0f).build("thrown_boogie_bomb"));
    public static final Supplier<EntityType<ThrownDiscordBomb>> THROWN_DISCORD_BOMB = ENTITY_TYPES.register("thrown_discord_bomb",
            () -> EntityType.Builder.<ThrownDiscordBomb>of(ThrownDiscordBomb::new, MobCategory.MISC).sized(1.0f, 1.0f).build("thrown_discord_bomb"));

    public static final Supplier<EntityType<TheGreatComposer>> THE_GREAT_COMPOSER = ENTITY_TYPES.register("the_great_composer",
            () -> EntityType.Builder.of(TheGreatComposer::new, MobCategory.CREATURE).sized(1.0f, 2.0f).clientTrackingRange(4).updateInterval(10).build("the_great_composer"));
    public static final Supplier<EntityType<ComposerCanonEntity>> THE_GREAT_COMPOSER_CANON = ENTITY_TYPES.register("the_great_composer_canon",
            () -> EntityType.Builder.of(ComposerCanonEntity::new, MobCategory.MONSTER).sized(1.25f, 1.0f).build("the_great_composer_canon"));
    public static final Supplier<EntityType<PlayerCanonEntity>> PLAYER_CANON = ENTITY_TYPES.register("player_canon",
            () -> EntityType.Builder.of(PlayerCanonEntity::new, MobCategory.MONSTER).sized(0.6f, 1.8f).build("player_canon"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}

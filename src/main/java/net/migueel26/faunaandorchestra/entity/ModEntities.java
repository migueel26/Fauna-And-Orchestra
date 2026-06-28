package net.migueel26.faunaandorchestra.entity;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.custom.*;
import net.migueel26.faunaandorchestra.entity.custom.boss.ComposerCanonEntity;
import net.migueel26.faunaandorchestra.entity.custom.boss.TheGreatComposer;
import net.migueel26.faunaandorchestra.entity.custom.jazzy_dammys.DanB;
import net.migueel26.faunaandorchestra.entity.custom.jazzy_dammys.Delroy;
import net.migueel26.faunaandorchestra.entity.custom.jazzy_dammys.Denise;
import net.migueel26.faunaandorchestra.entity.custom.jazzy_dammys.Denzel;
import net.migueel26.faunaandorchestra.entity.custom.koala_workers.FarmerKoalaEntity;
import net.migueel26.faunaandorchestra.entity.custom.koala_workers.MelomancerKoalaEntity;
import net.migueel26.faunaandorchestra.entity.custom.koala_workers.TailorKoalaEntity;
import net.migueel26.faunaandorchestra.entity.custom.koala_workers.WorkerKoalaEntity;
import net.migueel26.faunaandorchestra.entity.custom.misc.FloatingBlossomEntity;
import net.migueel26.faunaandorchestra.entity.custom.misc.SensorNote;
import net.migueel26.faunaandorchestra.entity.custom.projectile.MusicNoteProjectileEntity;
import net.migueel26.faunaandorchestra.entity.custom.projectile.PhantomNoteProjectileEntity;
import net.migueel26.faunaandorchestra.entity.custom.projectile.ThrownBoogieBomb;
import net.migueel26.faunaandorchestra.entity.custom.projectile.ThrownDiscordBomb;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, FaunaAndOrchestra.MOD_ID);

    public  static final RegistryObject<EntityType<MantisEntity>> MANTIS = ENTITY_TYPES.register("mantis",
            () -> EntityType.Builder.of(MantisEntity::new, MobCategory.CREATURE).sized(1f, 2.25f).build("mantis"));
    public static final RegistryObject<EntityType<PenguinEntity>> PENGUIN = ENTITY_TYPES.register("penguin",
            () -> EntityType.Builder.of(PenguinEntity::new, MobCategory.CREATURE).sized(0.75f, 0.75f).build("penguin"));
    public static final RegistryObject<EntityType<RedPandaEntity>> RED_PANDA = ENTITY_TYPES.register("red_panda",
            () -> EntityType.Builder.of(RedPandaEntity::new, MobCategory.CREATURE).sized(0.75f, 1f).build("red_panda"));
    public static final RegistryObject<EntityType<MacawEntity>> MACAW = ENTITY_TYPES.register("macaw",
            () -> EntityType.Builder.of(MacawEntity::new, MobCategory.CREATURE).sized(0.5f, 0.75f).build("macaw"));
    public static final RegistryObject<EntityType<BeaverEntity>> BEAVER = ENTITY_TYPES.register("beaver",
            () -> EntityType.Builder.of(BeaverEntity::new, MobCategory.CREATURE).sized(0.75f, 0.75f).build("beaver"));
    public static final RegistryObject<EntityType<LemurEntity>> LEMUR = ENTITY_TYPES.register("lemur",
            () -> EntityType.Builder.of(LemurEntity::new, MobCategory.CREATURE).sized(0.75f, 0.75f).build("lemur"));
    public static final RegistryObject<EntityType<MadameButterflyEntity>> MADAME_BUTTERFLY = ENTITY_TYPES.register("madame_butterfly",
            () -> EntityType.Builder.of(MadameButterflyEntity::new, MobCategory.CREATURE).sized(0.5f, 0.5f).build("madame_butterfly"));
    public static final RegistryObject<EntityType<SeaLionEntity>> SEA_LION = ENTITY_TYPES.register("sea_lion",
            () -> EntityType.Builder.of(SeaLionEntity::new, MobCategory.CREATURE).sized(0.75f, 1.25f).build("sea_lion"));
    public  static final RegistryObject<EntityType<QuirkyFrogEntity>> QUIRKY_FROG = ENTITY_TYPES.register("quirky_frog",
            () -> EntityType.Builder.of(QuirkyFrogEntity::new, MobCategory.CREATURE).sized(0.75f, 0.75f).build("quirky_frog"));

    public static final RegistryObject<EntityType<Faust>> FAUST = ENTITY_TYPES.register("faust",
            () -> EntityType.Builder.of(Faust::new, MobCategory.CREATURE).sized(0.75f, 1.5f).build("faust"));
    public static final RegistryObject<EntityType<Orion>> ORION = ENTITY_TYPES.register("orion",
            () -> EntityType.Builder.of(Orion::new, MobCategory.CREATURE).sized(0.75f, 1.5f).build("orion"));
    public static final RegistryObject<EntityType<AnyaGhost>> ANYA_GHOST = ENTITY_TYPES.register("anya_ghost",
            () -> EntityType.Builder.of(AnyaGhost::new, MobCategory.CREATURE).sized(0.6f, 1.8f).build("anya_ghost"));
    public static final RegistryObject<EntityType<WiseTree>> WISE_TREE = ENTITY_TYPES.register("wise_tree",
            () -> EntityType.Builder.of(WiseTree::new, MobCategory.CREATURE).sized(0.6f, 1.0f).build("wise_tree"));
    public static final RegistryObject<EntityType<DanB>> DAN_B = ENTITY_TYPES.register("dan_b",
            () -> EntityType.Builder.of(DanB::new, MobCategory.CREATURE).sized(0.75f, 1.35f).build("dan_b"));
    public static final RegistryObject<EntityType<Delroy>> DELROY = ENTITY_TYPES.register("delroy",
            () -> EntityType.Builder.of(Delroy::new, MobCategory.CREATURE).sized(0.75f, 1f).build("delroy"));
    public static final RegistryObject<EntityType<Denise>> DENISE = ENTITY_TYPES.register("denise",
            () -> EntityType.Builder.of(Denise::new, MobCategory.CREATURE).sized(0.75f, 1f).build("denise"));
    public static final RegistryObject<EntityType<Denzel>> DENZEL = ENTITY_TYPES.register("denzel",
            () -> EntityType.Builder.of(Denzel::new, MobCategory.CREATURE).sized(0.75f, 1.2f).build("denzel"));

    public static final RegistryObject<EntityType<WanderingKoalaEntity>> WANDERING_KOALA = ENTITY_TYPES.register("wandering_koala",
            () -> EntityType.Builder.of(WanderingKoalaEntity::new, MobCategory.CREATURE).sized(0.5f, 1.25f).build("wandering_koala"));
    public static final RegistryObject<EntityType<ButlerKoalaEntity>> BUTLER_KOALA = ENTITY_TYPES.register("butler_koala",
            () -> EntityType.Builder.of(ButlerKoalaEntity::new, MobCategory.CREATURE).sized(0.5f, 1.0f).build("butler_koala"));
    public static final RegistryObject<EntityType<WorkerKoalaEntity>> WORKER_KOALA = ENTITY_TYPES.register("worker_koala",
            () -> EntityType.Builder.of(WorkerKoalaEntity::new, MobCategory.CREATURE).sized(0.5f, 1.0f).build("worker_koala"));
    public static final RegistryObject<EntityType<TailorKoalaEntity>> TAILOR_KOALA = ENTITY_TYPES.register("tailor_koala",
            () -> EntityType.Builder.of(TailorKoalaEntity::new, MobCategory.CREATURE).sized(0.4f, 1.0f).build("tailor_koala"));
    public static final RegistryObject<EntityType<MelomancerKoalaEntity>> MELOMANCER_KOALA = ENTITY_TYPES.register("melomancer_koala",
            () -> EntityType.Builder.of(MelomancerKoalaEntity::new, MobCategory.CREATURE).sized(0.4f, 1.0f).build("melomancer_koala"));
    public static final RegistryObject<EntityType<FarmerKoalaEntity>> FARMER_KOALA = ENTITY_TYPES.register("farmer_koala",
            () -> EntityType.Builder.of(FarmerKoalaEntity::new, MobCategory.CREATURE).sized(0.4f, 1.0f).build("farmer_koala"));

    public static final RegistryObject<EntityType<SproutlingEntity>> SINGING_SPROUTLING = ENTITY_TYPES.register("singing_sproutling",
            () -> EntityType.Builder.of(SproutlingEntity::new, MobCategory.CREATURE).sized(0.35f, 0.45f).build("singing_sproutling"));
    public static final RegistryObject<EntityType<LivingMusicEntity>> LIVING_MUSIC = ENTITY_TYPES.register("living_music",
            () -> EntityType.Builder.of(LivingMusicEntity::new, MobCategory.CREATURE).sized(0.35f, 0.45f).build("living_music"));
    public static final RegistryObject<EntityType<ButterflyEntity>> BUTTERFLY = ENTITY_TYPES.register("butterfly",
            () -> EntityType.Builder.of(ButterflyEntity::new, MobCategory.CREATURE).sized(0.35f, 0.35f).build("butterfly"));
    public static final RegistryObject<EntityType<WanderingNoteEntity>> WANDERING_NOTE = ENTITY_TYPES.register("wandering_note_entity",
            () -> EntityType.Builder.of(WanderingNoteEntity::new, MobCategory.AMBIENT).sized(1.0f, 1.0f).build("wandering_note_entity"));

    public static final RegistryObject<EntityType<MusicNoteProjectileEntity>> MUSIC_NOTE_PROJECTILE = ENTITY_TYPES.register("music_note_projectile",
            () -> EntityType.Builder.<MusicNoteProjectileEntity>of(MusicNoteProjectileEntity::new, MobCategory.MISC).sized(1.0f, 1.0f).build("music_note_projectile"));
    public static final RegistryObject<EntityType<PhantomNoteProjectileEntity>> PHANTOM_NOTE_PROJECTILE = ENTITY_TYPES.register("phantom_note_projectile",
            () -> EntityType.Builder.<PhantomNoteProjectileEntity>of(PhantomNoteProjectileEntity::new, MobCategory.MISC).sized(1.0f, 1.0f).build("phantom_note_projectile"));
    public static final RegistryObject<EntityType<SensorNote>> SENSOR_NOTE = ENTITY_TYPES.register("sensor_note",
            () -> EntityType.Builder.<SensorNote>of(SensorNote::new, MobCategory.MISC).sized(1.0f, 1.0f).build("sensor_note"));
    public static final RegistryObject<EntityType<FloatingBlossomEntity>> FLOATING_BLOSSOM = ENTITY_TYPES.register("floating_blossom",
            () -> EntityType.Builder.<FloatingBlossomEntity>of(FloatingBlossomEntity::new, MobCategory.MISC).sized(1.0f, 1.25f).build("floating_blossom"));
    public static final RegistryObject<EntityType<ThrownBoogieBomb>> THROWN_BOOGIE_BOMB = ENTITY_TYPES.register("thrown_boogie_bomb",
            () -> EntityType.Builder.<ThrownBoogieBomb>of(ThrownBoogieBomb::new, MobCategory.MISC).sized(1.0f, 1.0f).build("thrown_boogie_bomb"));
    public static final RegistryObject<EntityType<ThrownDiscordBomb>> THROWN_DISCORD_BOMB = ENTITY_TYPES.register("thrown_discord_bomb",
            () -> EntityType.Builder.<ThrownDiscordBomb>of(ThrownDiscordBomb::new, MobCategory.MISC).sized(1.0f, 1.0f).build("thrown_discord_bomb"));

    public static final RegistryObject<EntityType<TheGreatComposer>> THE_GREAT_COMPOSER = ENTITY_TYPES.register("the_great_composer",
            () -> EntityType.Builder.of(TheGreatComposer::new, MobCategory.CREATURE).sized(1.0f, 2.0f).clientTrackingRange(4).updateInterval(10).build("the_great_composer"));
    public static final RegistryObject<EntityType<ComposerCanonEntity>> THE_GREAT_COMPOSER_CANON = ENTITY_TYPES.register("the_great_composer_canon",
            () -> EntityType.Builder.of(ComposerCanonEntity::new, MobCategory.MONSTER).sized(1.25f, 1.0f).build("the_great_composer_canon"));
    public static final RegistryObject<EntityType<PlayerCanonEntity>> PLAYER_CANON = ENTITY_TYPES.register("player_canon",
            () -> EntityType.Builder.of(PlayerCanonEntity::new, MobCategory.MONSTER).sized(0.6f, 1.8f).build("player_canon"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}

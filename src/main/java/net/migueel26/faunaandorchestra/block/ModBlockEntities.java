package net.migueel26.faunaandorchestra.block;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.block.custom.MailboxBlock;
import net.migueel26.faunaandorchestra.block.entity.*;
import net.migueel26.faunaandorchestra.block.entity.spawners.PaintingSpawnerBlockEntity;
import net.migueel26.faunaandorchestra.block.entity.spawners.TavernSpawnerBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, FaunaAndOrchestra.MOD_ID);

    public static final Supplier<BlockEntityType<ComposerGravestoneBlockEntity>> COMPOSER_GRAVESTONE_BE =
            BLOCK_ENTITES.register("composer_gravestone_be", () -> BlockEntityType.Builder.of(
                    ComposerGravestoneBlockEntity::new,
                    ModBlocks.COMPOSER_GRAVESTONE.get(),
                    ModBlocks.GRAVESTONE.get())
            .build(null));

    public static final Supplier<BlockEntityType<TipCaseBlockEntity>> TIP_CASE_BE =
            BLOCK_ENTITES.register("tip_case_be", () -> BlockEntityType.Builder.of(
                    TipCaseBlockEntity::new,
                    ModBlocks.TIP_CASE.get()
            ).build(null));

    public static final Supplier<BlockEntityType<ListenerBlockEntity>> LISTENER_BE =
            BLOCK_ENTITES.register("listener_be", () -> BlockEntityType.Builder.of(
                    ListenerBlockEntity::new,
                    ModBlocks.LISTENER.get()
            ).build(null));

    public static final Supplier<BlockEntityType<ListenerContainerBlockEntity>> LISTENER_CONTAINER_BE =
            BLOCK_ENTITES.register("listener_container_be", () -> BlockEntityType.Builder.of(
                    ListenerContainerBlockEntity::new,
                    ModBlocks.LISTENER_CONTAINER.get()
            ).build(null));

    public static final Supplier<BlockEntityType<MelomancyCauldronBlockEntity>> MELOMANCY_CAULDRON_BE =
            BLOCK_ENTITES.register("melomancy_cauldron_be", () -> BlockEntityType.Builder.of(
                    MelomancyCauldronBlockEntity::new,
                    ModBlocks.MELOMANCY_CAULDRON.get()
            ).build(null));

    public static final Supplier<BlockEntityType<SingingCropBlockEntity>> SINGING_CROP_BE =
            BLOCK_ENTITES.register("singing_crop_be", () -> BlockEntityType.Builder.of(
                    SingingCropBlockEntity::new,
                    ModBlocks.SINGING_CROP.get()
            ).build(null));

    public static final Supplier<BlockEntityType<AltarOfThePanFluteBlockEntity>> ALTAR_OF_THE_PAN_FLUTE_BE =
            BLOCK_ENTITES.register("altar_of_the_pan_flute_be", () -> BlockEntityType.Builder.of(
                    AltarOfThePanFluteBlockEntity::new,
                    ModBlocks.ALTAR_OF_THE_PAN_FLUTE.get()
            ).build(null));

    public static final Supplier<BlockEntityType<VoiceChamberBlockEntity>> VOICE_CHAMBER_BE =
            BLOCK_ENTITES.register("voice_chamber_be", () -> BlockEntityType.Builder.of(
                    VoiceChamberBlockEntity::new,
                    ModBlocks.VOICE_CHAMBER.get()
            ).build(null));

    public static final Supplier<BlockEntityType<DiscordNucleiBlockEntity>> DISCORD_NUCLEI_BE =
            BLOCK_ENTITES.register("discord_nuclei_be", () -> BlockEntityType.Builder.of(
                    DiscordNucleiBlockEntity::new,
                    ModBlocks.DISCORD_NUCLEI.get()
            ).build(null));

    public static final Supplier<BlockEntityType<TheGreatHeadBlockEntity>> THE_GREAT_HEAD_BE =
            BLOCK_ENTITES.register("the_great_head_be", () -> BlockEntityType.Builder.of(
                    TheGreatHeadBlockEntity::new,
                    ModBlocks.THE_GREAT_HEAD.get()
            ).build(null));

    public static final Supplier<BlockEntityType<MotherStatueBlockEntity>> MOTHER_STATUE_BE =
            BLOCK_ENTITES.register("mother_statue_be", () -> BlockEntityType.Builder.of(
                    MotherStatueBlockEntity::new,
                    ModBlocks.MOTHER_STATUE.get()
            ).build(null));

    public static final Supplier<BlockEntityType<HangingJarBlockEntity>> HANGING_JAR_BE =
            BLOCK_ENTITES.register("hanging_jar_be", () -> BlockEntityType.Builder.of(
                    HangingJarBlockEntity::new,
                    ModBlocks.HANGING_JAR.get()
            ).build(null));

    public static final Supplier<BlockEntityType<JarRackBlockEntity>> JAR_RACK_BE =
            BLOCK_ENTITES.register("jar_rack_be", () -> BlockEntityType.Builder.of(
                    JarRackBlockEntity::new,
                    ModBlocks.JAR_RACK.get()
            ).build(null));

    public static final Supplier<BlockEntityType<BambooTrapBlockEntity>> BAMBOO_TRAP_BE =
            BLOCK_ENTITES.register("bamboo_trap_be", () -> BlockEntityType.Builder.of(
                    BambooTrapBlockEntity::new,
                    ModBlocks.BAMBOO_TRAP.get()
            ).build(null));

    public static final Supplier<BlockEntityType<BeaverStatueBlockEntity>> BEAVER_STATUE_BE =
            BLOCK_ENTITES.register("beaver_statue_be", () -> BlockEntityType.Builder.of(
                    BeaverStatueBlockEntity::new,
                    ModBlocks.BEAVER_STATUE.get()
            ).build(null));

    public static final Supplier<BlockEntityType<SewingMachineBlockEntity>> SEWING_MACHINE_BE =
            BLOCK_ENTITES.register("sewing_machine_be", () -> BlockEntityType.Builder.of(
                    SewingMachineBlockEntity::new,
                    ModBlocks.SEWING_MACHINE.get()
            ).build(null));

    public static final Supplier<BlockEntityType<MailboxBlockEntity>> MAILBOX_BE =
            BLOCK_ENTITES.register("mailbox_be", () -> BlockEntityType.Builder.of(
                    MailboxBlockEntity::new,
                    ModBlocks.MAILBOX.get()
            ).build(null));

    public static final Supplier<BlockEntityType<FloraEnhancerBlockEntity>> FLORA_ENHANCER =
            BLOCK_ENTITES.register("flora_enhancer_be", () -> BlockEntityType.Builder.of(
                    FloraEnhancerBlockEntity::new,
                    ModBlocks.FLORA_ENHANCER.get()
            ).build(null));

    public static final Supplier<BlockEntityType<CrawlingDiscordBlockEntity>> CRAWLING_DISCORD_BE =
            BLOCK_ENTITES.register("crawling_discord_be", () -> BlockEntityType.Builder.of(
                    CrawlingDiscordBlockEntity::new,
                    ModBlocks.CRAWLING_DISCORD.get()
            ).build(null));

    // MISC (CREATIVE ONLY)
    public static final Supplier<BlockEntityType<TavernSpawnerBlockEntity>> TAVERN_SPAWNER_BE =
            BLOCK_ENTITES.register("tavern_spawner_be", () -> BlockEntityType.Builder.of(
                    TavernSpawnerBlockEntity::new,
                    ModBlocks.TAVERN_SPAWNER.get()
            ).build(null));

    public static final Supplier<BlockEntityType<PaintingSpawnerBlockEntity>> PAINTING_SPAWNER_BE =
            BLOCK_ENTITES.register("painting_spawner_be", () -> BlockEntityType.Builder.of(
                    PaintingSpawnerBlockEntity::new,
                    ModBlocks.PAINTING_SPAWNER.get()
            ).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITES.register(eventBus);
    }
}

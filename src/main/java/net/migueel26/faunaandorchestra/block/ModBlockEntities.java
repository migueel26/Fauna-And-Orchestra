package net.migueel26.faunaandorchestra.block;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.block.entity.*;
import net.minecraft.core.registries.BuiltInRegistries;
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

    public static final Supplier<BlockEntityType<TheGreatHeadBlockEntity>> THE_GREAT_HEAD_BE =
            BLOCK_ENTITES.register("the_great_head_be", () -> BlockEntityType.Builder.of(
                    TheGreatHeadBlockEntity::new,
                    ModBlocks.THE_GREAT_HEAD.get()
            ).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITES.register(eventBus);
    }
}

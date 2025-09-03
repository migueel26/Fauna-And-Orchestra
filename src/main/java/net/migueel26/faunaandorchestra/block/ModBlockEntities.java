package net.migueel26.faunaandorchestra.block;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.block.entity.ComposerGravestoneBlockEntity;
import net.migueel26.faunaandorchestra.block.entity.ListenerBlockEntity;
import net.migueel26.faunaandorchestra.block.entity.ListenerContainerBlockEntity;
import net.migueel26.faunaandorchestra.block.entity.TipCaseBlockEntity;
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

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITES.register(eventBus);
    }
}

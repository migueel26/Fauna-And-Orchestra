package net.migueel26.faunaandorchestra.block;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.block.custom.ComposerGravestoneBlock;
import net.migueel26.faunaandorchestra.block.entity.ComposerGravestoneBlockEntity;
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
                    ComposerGravestoneBlockEntity::new, ModBlocks.GRAVESTONE.get()).build(null)
            );

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITES.register(eventBus);
    }
}

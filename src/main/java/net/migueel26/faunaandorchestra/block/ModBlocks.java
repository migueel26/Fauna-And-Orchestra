package net.migueel26.faunaandorchestra.block;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.block.custom.*;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(FaunaAndOrchestra.MOD_ID);

    public static final DeferredBlock<Block> COMPOSER_GRAVESTONE = registerBlock("composer_gravestone",
            () -> new ComposerGravestoneBlock(BlockBehaviour.Properties.of()
                    .noOcclusion()
                    .strength(-1.0F, 3600000.0F)
                    .noLootTable()));

    public static final DeferredBlock<Block> GRAVESTONE = registerBlock("gravestone",
            () -> new RegularGravestoneBlock(BlockBehaviour.Properties.of()
                    .noOcclusion()
                    .strength(2.0F, 6.0F)));

    public static final DeferredBlock<Block> TIP_CASE = registerBlock("tip_case",
            () -> new TipCaseBlock(BlockBehaviour.Properties.of()
                    .noOcclusion()
                    .strength(0.2F)
                    .ignitedByLava()
                    .pushReaction(PushReaction.DESTROY)
                    .sound(SoundType.WOOL)));

    public static final DeferredBlock<Block> LISTENER = registerBlock("listener",
            () -> new ListenerBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F)
                    .sound(SoundType.WOOD)));

    public static final DeferredBlock<Block> LISTENER_CONTAINER = registerBlock("listener_container",
            () -> new ListenerContainerBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F)
                    .sound(SoundType.WOOD)));

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        ModItems.ITEMS.register(name, () -> new BlockItem(toReturn.get(), new Item.Properties()));
        return toReturn;
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}

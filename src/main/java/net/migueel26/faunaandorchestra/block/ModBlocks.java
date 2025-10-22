package net.migueel26.faunaandorchestra.block;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.block.custom.*;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.worldgen.tree.ModTreeGrowers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
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

    public static final DeferredBlock<Block> MELOMANCY_CAULDRON = registerBlock("melomancy_cauldron",
            () -> new MelomancyCauldronBlock(BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .strength(2.0F)
                    .noOcclusion()
                    .sound(SoundType.STONE)));

    public static final DeferredBlock<Block> ALTAR = registerBlock("altar",
            () -> new AltarBlock(BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .strength(2.0F)
                    .noOcclusion()
                    .sound(SoundType.STONE)));

    public static final DeferredBlock<Block> ALTAR_OF_THE_BUTTERFLIES = registerBlock("altar_of_the_butterflies",
            () -> new AltarOfTheButterfliesBlock(BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .strength(2.0F)
                    .noOcclusion()
                    .sound(SoundType.STONE)
                    .randomTicks()));

    public static final DeferredBlock<Block> ALTAR_OF_THE_PAN_FLUTE = registerBlock("altar_of_the_pan_flute",
            () -> new AltarOfThePanFluteBlock(BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .strength(2.0F)
                    .noOcclusion()
                    .sound(SoundType.STONE)));

    public static final DeferredBlock<Block> VOICE_CHAMBER = registerBlock("voice_chamber",
            () -> new VoiceChamberBlock(BlockBehaviour.Properties.of()
                    .noOcclusion()
                    .strength(1.0F, 6.0F)));

    public static final DeferredBlock<Block> CRAWLING_DISCORD = registerBlock("crawling_discord",
            () -> new CrawlingDiscordBlock(BlockBehaviour.Properties.of()
                    .strength(2.0f, 6.0f)
                    .sound(SoundType.SCULK)));

    public static final DeferredBlock<Block> DAM_BLOCK = registerBlock("dam_block",
            () -> new DamBlock(BlockBehaviour.Properties.of()
                            .strength(0.7F)
                            .sound(SoundType.MANGROVE_ROOTS)
                            .noOcclusion()
                            .ignitedByLava()
            ));

    public static final DeferredBlock<Block> SINGING_CROP = registerBlock("singing_crop",
            () -> new SingingCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BEETROOTS)));

    public static final DeferredBlock<Block> GINGKO_BILOBA_LEAVES = registerBlock("gingko_biloba_leaves",
            () -> new LeavesBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .strength(0.2F)
                    .randomTicks()
                    .sound(SoundType.GRASS)
                    .noOcclusion()
                    .isValidSpawn(Blocks::ocelotOrParrot)
                    .ignitedByLava()
                    .pushReaction(PushReaction.DESTROY)) {
                @Override
                public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return true;
                }

                @Override
                public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 60;
                }

                @Override
                public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 30;
                }
            });

    public static final DeferredBlock<Block> GINGKO_BILOBA_SAPLING = registerBlock("gingko_biloba_sapling",
            () -> new SaplingBlock(ModTreeGrowers.GINGKO_BILOBA_TREE, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING)));

    public static final DeferredBlock<Block> THE_GREAT_HEAD = registerBlock("the_great_head",
            () -> new TheGreatHeadBlock(BlockBehaviour.Properties.of()
                    .strength(1.0F)));

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        ModItems.ITEMS.register(name, () -> new BlockItem(toReturn.get(), new Item.Properties()));
        return toReturn;
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}

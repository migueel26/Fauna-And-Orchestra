package net.migueel26.faunaandorchestra.block;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.block.custom.*;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.worldgen.tree.ModTreeGrowers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
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
                    .noLootTable()
                    .mapColor(MapColor.STONE)));

    public static final DeferredBlock<Block> GRAVESTONE = registerBlock("gravestone",
            () -> new RegularGravestoneBlock(BlockBehaviour.Properties.of()
                    .noOcclusion()
                    .strength(2.0F, 6.0F)
                    .mapColor(MapColor.STONE)));

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
                    .sound(SoundType.WOOD)
                    .mapColor(MapColor.GOLD)));

    public static final DeferredBlock<Block> LISTENER_CONTAINER = registerBlock("listener_container",
            () -> new ListenerContainerBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F)
                    .sound(SoundType.WOOD)
                    .mapColor(MapColor.WOOD)));

    public static final DeferredBlock<Block> MELOMANCY_CAULDRON = registerBlock("melomancy_cauldron",
            () -> new MelomancyCauldronBlock(BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .strength(2.0F)
                    .noOcclusion()
                    .sound(SoundType.STONE)
                    .mapColor(MapColor.STONE)));

    public static final DeferredBlock<Block> ALTAR = registerBlock("altar",
            () -> new AltarBlock(BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .strength(2.0F)
                    .noOcclusion()
                    .sound(SoundType.STONE)
                    .mapColor(MapColor.STONE)));

    public static final DeferredBlock<Block> ALTAR_OF_THE_BUTTERFLIES = registerBlock("altar_of_the_butterflies",
            () -> new AltarOfTheButterfliesBlock(BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .strength(2.0F)
                    .noOcclusion()
                    .sound(SoundType.STONE)
                    .randomTicks()
                    .mapColor(MapColor.STONE)));

    public static final DeferredBlock<Block> ALTAR_OF_THE_PAN_FLUTE = registerBlock("altar_of_the_pan_flute",
            () -> new AltarOfThePanFluteBlock(BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .strength(2.0F)
                    .noOcclusion()
                    .sound(SoundType.STONE)
                    .mapColor(MapColor.STONE)));

    public static final DeferredBlock<Block> VOICE_CHAMBER = registerBlock("voice_chamber",
            () -> new VoiceChamberBlock(BlockBehaviour.Properties.of()
                    .noOcclusion()
                    .strength(1.0F, 6.0F)));

    public static final DeferredBlock<Block> CRAWLING_DISCORD = registerBlock("crawling_discord",
            () -> new CrawlingDiscordBlock(BlockBehaviour.Properties.of()
                    .strength(2.0f, 6.0f)
                    .sound(SoundType.SCULK)
                    .mapColor(MapColor.COLOR_BLACK)));

    public static final DeferredBlock<Block> FLOWER_DISCORD_BLOCK = registerBlock("flower_discord_block",
            () -> new FlowerGrowerDiscordBlock(BlockBehaviour.Properties.ofFullCopy(CRAWLING_DISCORD.get())));

    public static final DeferredBlock<Block> DISCORD_BLOCK = registerBlock("discord_block",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(CRAWLING_DISCORD.get())) {
                @Override
                public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
                    if (entity instanceof LivingEntity) entity.hurt(level.damageSources().magic(), 2.0F);
                    if (!level.isClientSide()) {
                        ((ServerLevel) level).sendParticles(ParticleTypes.SCULK_SOUL,
                                entity.getX(), entity.getY(), entity.getZ(),
                                3, 0.1f, 0.1f, 0.1f, 0.01f);
                    }
                    super.stepOn(level, pos, state, entity);
                }
            });

    public static final DeferredBlock<Block> DISCORDED_FLOWER = registerBlock("discorded_flower",
            () -> new DiscordedFlowerBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.SCULK_VEIN)
                    .offsetType(BlockBehaviour.OffsetType.XZ)
                    .pushReaction(PushReaction.DESTROY)
                    .randomTicks()
                    .mapColor(MapColor.PLANT)));

    public static final DeferredBlock<Block> DISCORD_NUCLEI = registerBlock("discord_nuclei",
            () -> new DiscordNucleiBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SCULK_SHRIEKER)));

    public static final DeferredBlock<Block> DAM_BLOCK = registerBlock("dam_block",
            () -> new DamBlock(BlockBehaviour.Properties.of()
                            .strength(0.7F)
                            .sound(SoundType.MANGROVE_ROOTS)
                            .noOcclusion()
                            .ignitedByLava()
                            .mapColor(MapColor.TERRACOTTA_BROWN)
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

    public static final DeferredBlock<Block> MOTHER_STATUE = registerBlock("mother_statue",
            () -> new MotherStatueBlock(BlockBehaviour.Properties.of()
                    .noOcclusion()
                    .strength(2.0F, 6.0F)
                    .mapColor(MapColor.STONE)));
    public static final DeferredBlock<Block> HANGING_JAR = registerBlock("hanging_jar",
        () -> new HangingJarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)));

    public static final DeferredBlock<Block> JAR_RACK = registerBlock("jar_rack",
            () -> new JarRackBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F)
                    .noOcclusion()
                    .sound(SoundType.WOOD)
                    .mapColor(MapColor.WOOD)));

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        ModItems.ITEMS.register(name, () -> new BlockItem(toReturn.get(), new Item.Properties()));
        return toReturn;
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}

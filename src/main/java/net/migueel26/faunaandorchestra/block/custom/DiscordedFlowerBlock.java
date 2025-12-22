package net.migueel26.faunaandorchestra.block.custom;

import net.migueel26.faunaandorchestra.block.ModBlocks;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.particles.ModParticleTypes;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DiscordedFlowerBlock extends Block {
    protected final Map<Item, Integer> FOOD = Map.ofEntries(
            Map.entry(Items.ROTTEN_FLESH, 1),
            Map.entry(Items.BONE, 1),
            Map.entry(Items.SPIDER_EYE, 2),
            Map.entry(Items.NETHER_WART, 2),
            Map.entry(Items.PORKCHOP, 2),
            Map.entry(Items.CHICKEN, 2),
            Map.entry(Items.RABBIT, 2),
            Map.entry(Items.BEEF, 2),
            Map.entry(Items.SUSPICIOUS_STEW, 3),
            Map.entry(Items.LEATHER, 3),
            Map.entry(Items.RABBIT_FOOT, 5),
            Map.entry(Items.SCUTE, 10),
            Map.entry(Items.SLIME_BALL, 10),
            Map.entry(Items.MAGMA_CREAM, 10),
            Map.entry(Items.GHAST_TEAR, 30),
            Map.entry(Items.PHANTOM_MEMBRANE, 40),
            Map.entry(Items.NAUTILUS_SHELL, 40),
            Map.entry(Items.ECHO_SHARD, 75),
            Map.entry(Items.NETHER_STAR, 100)
    );

    protected final Map<Integer, Integer> NEW_GENERATIONS = Map.ofEntries(
            Map.entry(5, 3),
            Map.entry(40, 4),
            Map.entry(80, 5),
            Map.entry(120, 8),
            Map.entry(160, 10),
            Map.entry(200, 13),
            Map.entry(240, 18),
            Map.entry(280, 23),
            Map.entry(320, 28),
            Map.entry(360, 34),
            Map.entry(400, 45)
    );

    protected final List<Integer> GENERATIONS_INDEX = new ArrayList<>(List.of(5, 40, 80, 120, 160, 200, 240, 280, 320, 360, 400));
    public static final IntegerProperty HUNGER = IntegerProperty.create("hunger", 0, 400);
    protected static final VoxelShape SHAPE = Block.box(5.0, 0.0, 5.0, 11.0, 10.0, 11.0);
    ;

    public DiscordedFlowerBlock(Properties properties) {
        super(properties);

        this.registerDefaultState(this.getStateDefinition().any().setValue(HUNGER, 0));
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack stack = player.getItemInHand(hand);

        int food = FOOD.getOrDefault(stack.getItem(), -1);
        if (food != -1) {
            level.playSound(player, pos, ModSounds.DISCORDED_FLOWER_EAT.get(), SoundSource.BLOCKS, 0.5f, 1.0f + (level.random.nextFloat() - 0.5f));
            if (!level.isClientSide()) {
                ((ServerLevel) level).sendParticles(ParticleTypes.SCULK_SOUL, pos.getCenter().x, pos.getCenter().y, pos.getCenter().z, 10, 0.25f, 0.25f, 0.25f, 0.05);
            }
            stack.shrink(1);
            feed(state, level, pos, food);
            return InteractionResult.SUCCESS;
        }
        return super.use(state, level, pos, player, hand, hitResult);
    }

    private void feed(BlockState state, Level level, BlockPos pos, int food) {
        int hunger = state.getValue(HUNGER);
        int newHunger = hunger + food;

        if (newHunger >= 400 || hunger >= 400) {
            level.setBlock(pos.below(), ModBlocks.FLOWER_DISCORD_BLOCK.get().defaultBlockState()
                    .setValue(FlowerGrowerDiscordBlock.MAX_GENERATION, NEW_GENERATIONS.get(400)), 3);
            if (!level.isClientSide()) {
                ((ServerLevel) level).sendParticles(ParticleTypes.SOUL_FIRE_FLAME, pos.getCenter().x, pos.getCenter().y, pos.getCenter().z, 80, 0.5, 0.5, 0.5, 0.3);
            }
            level.playSound(null, pos, SoundEvents.WARDEN_EMERGE, SoundSource.NEUTRAL);
            popResourceFromFace(level, pos, Direction.UP, new ItemStack(ModItems.PETALS_OF_DEATH.get()));
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        } else {
            int goal = GENERATIONS_INDEX.get(0);

            for (int i = 0; i < NEW_GENERATIONS.keySet().size() && goal == GENERATIONS_INDEX.get(0); i++) {
                if (GENERATIONS_INDEX.get(i) <= hunger && hunger < GENERATIONS_INDEX.get(i + 1)) {
                    goal = GENERATIONS_INDEX.get(i+1);
                }
            }

            if (newHunger >= goal) {
                int lastGoal = GENERATIONS_INDEX.get(0);
                for (int i = 0; i < NEW_GENERATIONS.keySet().size() && lastGoal == GENERATIONS_INDEX.get(0); i++) {
                    if (GENERATIONS_INDEX.get(i) < newHunger && newHunger <= GENERATIONS_INDEX.get(i + 1)) {
                        lastGoal = GENERATIONS_INDEX.get(i);
                    }
                }
                level.setBlock(pos.below(), ModBlocks.FLOWER_DISCORD_BLOCK.get().defaultBlockState()
                        .setValue(FlowerGrowerDiscordBlock.MAX_GENERATION, NEW_GENERATIONS.get(lastGoal)), 3);
            }
            level.setBlock(pos, state.setValue(HUNGER, newHunger), 3);
        }


    }

    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Vec3 vec3 = state.getOffset(level, pos);
        return SHAPE.move(vec3.x, vec3.y, vec3.z);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos blockpos = pos.below();
        BlockState belowBlockState = level.getBlockState(blockpos);
        return belowBlockState.is(ModBlocks.DISCORD_BLOCK.get()) || belowBlockState.is(ModBlocks.FLOWER_DISCORD_BLOCK.get());
    }

    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        return !state.canSurvive(level, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, pos.getCenter().x, pos.getCenter().y, pos.getCenter().z, 5, 0.3, 0.3, 0.3, 0);
        super.randomTick(state, level, pos, random);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HUNGER);
    }
}

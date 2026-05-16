package net.migueel26.faunaandorchestra.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class FlowerPathBlock extends HorizontalDirectionalBlock implements SimpleWaterloggedBlock {
    protected static final MapCodec<FlowerPathBlock> CODEC = simpleCodec(FlowerPathBlock::new);

    public static final int SPREAD_DELAY = 4;     // How fast the circle spreads
    public static final int SHRINK_DELAY = 8;     // Time difference between rings
    public static final int STAY_TIME = 40;       // Time the circle stays full

    public static final int DEFAULT_MAX_GENERATION = 3;

    public static final IntegerProperty GENERATION = IntegerProperty.create("generation", 0, 10);
    public static final BooleanProperty FATHER = BooleanProperty.create("father");
    public static final IntegerProperty MAX_GENERATION = IntegerProperty.create("max_generation", 0, 10);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);

    public FlowerPathBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(GENERATION, 0)
                .setValue(FATHER, false)
                .setValue(MAX_GENERATION, DEFAULT_MAX_GENERATION)
                .setValue(WATERLOGGED, Boolean.FALSE));
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        boolean flag = fluidstate.getType() == Fluids.WATER;
        return super.getStateForPlacement(context).setValue(WATERLOGGED, Boolean.valueOf(flag));
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState state2, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos2) {
        if (state.getValue(WATERLOGGED)) {
            levelAccessor.scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
        }

        return super.updateShape(state, direction, state2, levelAccessor, blockPos, blockPos2);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (!oldState.is(this) && !state.getValue(FATHER)) {
            level.scheduleTick(pos, this, SPREAD_DELAY);
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        boolean isFather = state.getValue(FATHER);
        int myGen = state.getValue(GENERATION);
        int maxGen = state.getValue(MAX_GENERATION);

        if (!isFather) {
            if (myGen < maxGen) {
                for (Direction dir : Direction.Plane.HORIZONTAL) {
                    BlockPos neighborPos = pos.relative(dir);
                    BlockState neighborState = level.getBlockState(neighborPos);

                    if (neighborState.canBeReplaced()) {
                        level.setBlock(neighborPos, this.defaultBlockState()
                                .setValue(GENERATION, myGen + 1)
                                .setValue(MAX_GENERATION, maxGen)
                                .setValue(FATHER, false)
                                .setValue(FACING, Direction.Plane.HORIZONTAL.getRandomDirection(level.random))
                                .setValue(WATERLOGGED, neighborState.getFluidState().is(Fluids.WATER)), 3);
                    }
                }
            }

            level.setBlock(pos, state.setValue(FATHER, true), 3);

            int deathDelay = STAY_TIME + (maxGen - myGen) * (SHRINK_DELAY + SPREAD_DELAY);

            level.scheduleTick(pos, this, deathDelay);

        } else {
            level.removeBlock(pos, false);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(GENERATION, FATHER, MAX_GENERATION, FACING, WATERLOGGED);
    }
}
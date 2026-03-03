package net.migueel26.faunaandorchestra.block.custom;

import com.mojang.serialization.MapCodec;
import net.migueel26.faunaandorchestra.block.entity.MotherStatueBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class MotherStatueBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final MapCodec<MotherStatueBlock> CODEC = simpleCodec(MotherStatueBlock::new);
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    public static final VoxelShape LOWER_NORTH = Shapes.or(
            Block.box(4, 0, 7, 16, 16, 9),
            Block.box(0, 0, 3, 6.5, 7, 13),
            Block.box(0, 7, 4, 4.5, 16, 12)
    );

    public static final VoxelShape UPPER_NORTH = Shapes.or(
            Block.box(0, 0, 4, 4.5, 5, 12),
            Block.box(4.5, 0, 7, 16, 2, 9),
            Block.box(10.5, 2, 7, 16, 8, 9),
            Block.box(0, 5, 7, 5.5, 13, 14)
    );

    public static final VoxelShape LOWER_SOUTH = Shapes.or(
            Block.box(0, 0, 7, 12, 16, 9),
            Block.box(9.5, 0, 3, 16, 7, 13),
            Block.box(11.5, 7, 4, 16, 16, 12)
    );

    public static final VoxelShape UPPER_SOUTH = Shapes.or(
            Block.box(11.5, 0, 4, 16, 5, 12),
            Block.box(0, 0, 7, 11.5, 2, 9),
            Block.box(0, 2, 7, 5.5, 8, 9),
            Block.box(10.5, 5, 2, 16, 13, 7)
    );

    public static final VoxelShape LOWER_EAST = Shapes.or(
            Block.box(7, 0, 4, 9, 16, 16),
            Block.box(3, 0, 0, 13, 7, 6.5),
            Block.box(4, 7, 0, 12, 16, 4.5)
    );

    public static final VoxelShape UPPER_EAST = Shapes.or(
            Block.box(4, 0, 0, 12, 5, 4.5),
            Block.box(7, 0, 4.5, 9, 2, 16),
            Block.box(7, 2, 10.5, 9, 8, 16),
            Block.box(2, 5, 0, 9, 13, 5.5)
    );

    public static final VoxelShape LOWER_WEST = Shapes.or(
            Block.box(7, 0, 0, 9, 16, 12),
            Block.box(3, 0, 9.5, 13, 7, 16),
            Block.box(4, 7, 11.5, 12, 16, 16)
    );

    public static final VoxelShape UPPER_WEST = Shapes.or(
            Block.box(4, 0, 11.5, 12, 5, 16),
            Block.box(7, 0, 0, 9, 2, 11.5),
            Block.box(7, 2, 0, 9, 8, 5.5),
            Block.box(7, 5, 10.5, 14, 13, 16));

    public MotherStatueBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        DoubleBlockHalf half = state.getValue(HALF);
        Direction facing = state.getValue(FACING);

        if (half == DoubleBlockHalf.LOWER) {
            return switch (facing) {
                case SOUTH -> LOWER_SOUTH;
                case EAST -> LOWER_EAST;
                case WEST -> LOWER_WEST;
                default -> LOWER_NORTH;
            };
        } else {
            return switch (facing) {
                case SOUTH -> UPPER_SOUTH;
                case EAST -> UPPER_EAST;
                case WEST -> UPPER_WEST;
                default -> UPPER_NORTH;
            };
        }
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MotherStatueBlockEntity(pos, state);
    }

    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        DoubleBlockHalf doubleblockhalf = state.getValue(HALF);
        if (facing.getAxis() != Direction.Axis.Y || doubleblockhalf == DoubleBlockHalf.LOWER != (facing == Direction.UP) || facingState.is(this) && facingState.getValue(HALF) != doubleblockhalf) {
            return doubleblockhalf == DoubleBlockHalf.LOWER && facing == Direction.DOWN && !state.canSurvive(level, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, facing, facingState, level, currentPos, facingPos);
        } else {
            return Blocks.AIR.defaultBlockState();
        }
    }

    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        BlockPos blockpos = pos.above();
        level.setBlock(blockpos, this.defaultBlockState().setValue(HALF, DoubleBlockHalf.UPPER).setValue(FACING, state.getValue(FACING)), 3);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos blockpos = context.getClickedPos();
        Level level = context.getLevel();
        Direction direction = context.getHorizontalDirection().getOpposite();
        return blockpos.getY() < level.getMaxBuildHeight() - 1 && level.getBlockState(blockpos.above()).canBeReplaced(context) ? this.defaultBlockState().setValue(FACING, direction) : null;
    }

    @Override
    public @Nullable PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.IGNORE;
    }

    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @javax.annotation.Nullable BlockEntity te, ItemStack stack) {
        super.playerDestroy(level, player, pos, Blocks.AIR.defaultBlockState(), te, stack);
    }

    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        if (state.getValue(HALF) != DoubleBlockHalf.UPPER) {
            return super.canSurvive(state, level, pos);
        } else {
            BlockState blockstate = level.getBlockState(pos.below());
            if (state.getBlock() != this) {
                return super.canSurvive(state, level, pos);
            } else {
                return blockstate.is(this) && blockstate.getValue(HALF) == DoubleBlockHalf.LOWER;
            }
        }
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, HALF);
    }
}

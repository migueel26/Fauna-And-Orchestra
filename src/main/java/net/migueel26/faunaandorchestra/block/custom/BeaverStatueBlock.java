package net.migueel26.faunaandorchestra.block.custom;

import com.mojang.serialization.MapCodec;
import net.migueel26.faunaandorchestra.block.ModBlocks;
import net.migueel26.faunaandorchestra.block.entity.BeaverStatueBlockEntity;
import net.migueel26.faunaandorchestra.entity.custom.BeaverEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.List;
import java.util.function.Predicate;

public class BeaverStatueBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final MapCodec<BeaverStatueBlock> CODEC = simpleCodec(BeaverStatueBlock::new);
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    public static final BooleanProperty ENABLED = BlockStateProperties.ENABLED;
    protected final VoxelShape LOWER_SHAPE = Shapes.or(
            Block.box(1.5f, 0f, 1.5f, 14.5f, 4f, 14.5f),
            Block.box(3.75f, 4f, 3.75f, 12.25f, 12f, 12.25f),
            Block.box(2.75f, 12f, 2.75f, 13.25f, 16f, 13.25f)
    );
    protected final VoxelShape UPPER_SHAPE = Block.box(6f, 0f, 6f, 9f, 12f, 9f);
    public BeaverStatueBlock(Properties properties) {
        super(properties);

        this.registerDefaultState(this.getStateDefinition().any().setValue(HALF, DoubleBlockHalf.LOWER).setValue(ENABLED, true));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        DoubleBlockHalf half = state.getValue(HALF);

        return switch (half) {
            case LOWER -> LOWER_SHAPE;
            case UPPER -> UPPER_SHAPE;
        };
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        state = state.cycle(ENABLED);
        level.setBlock(pos, state, 10);

        if (state.getValue(HALF) == DoubleBlockHalf.LOWER)  {
            this.transitionAnimation(state, level, pos);
        } else if (level.getBlockState(pos.below()).is(ModBlocks.BEAVER_STATUE)) {
            this.transitionAnimation(state, level, pos.below());
        }
        this.playSound(player, level, pos, state.getValue(ENABLED));

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private void transitionAnimation(BlockState state, Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof BeaverStatueBlockEntity blockEntity) {
            DustParticleOptions particle;
            if (state.getValue(ENABLED)) {
                blockEntity.activate();
                particle = new DustParticleOptions(new Vector3f(0, 255, 0), 1.0f);
            } else {
                blockEntity.deactivate();
                particle = new DustParticleOptions(new Vector3f(255, 0, 0), 1.0f);
            }

            if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
                BlockPos above = pos.above();
                serverLevel.sendParticles(particle, above.getCenter().x, above.getY(), above.getCenter().z, 20, 0.25f, 0.75f, 0.25f, 0.0f);
            }
        }
    }

    private void playSound(@Nullable Entity source, Level level, BlockPos pos, boolean isActivating) {
        level.playSound(source, pos, isActivating ? SoundEvents.VILLAGER_CELEBRATE : SoundEvents.VILLAGER_NO, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.1F + 0.9F);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        List<BeaverEntity> beavers = level.getEntitiesOfClass(BeaverEntity.class, new AABB(pos).inflate(160f), getBeaverCondition(state));
        for (BeaverEntity beaver : beavers) {
            Boolean isEnabled = state.getValue(ENABLED);
            beaver.setCanBuild(isEnabled);
            DustParticleOptions particle = new DustParticleOptions(new Vector3f(isEnabled ? 0 : 255, isEnabled ? 255 : 0, 0), 1.0f);
            level.sendParticles(particle, beaver.getX(), beaver.getY(), beaver.getZ(), 20, 0.4f, 0.3f, 0.4f, 0.0f);
        }
        super.randomTick(state, level, pos, random);
    }

    protected Predicate<? super BeaverEntity> getBeaverCondition(BlockState state) {
        return state.getValue(ENABLED) ? beaverEntity -> !beaverEntity.canBuild() : BeaverEntity::canBuild;
    }

    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        DoubleBlockHalf half = state.getValue(HALF);

        if (facing.getAxis() == Direction.Axis.Y && (half == DoubleBlockHalf.LOWER) == (facing == Direction.UP)) {
            if (facingState.is(this) && facingState.getValue(HALF) != half) {
                return state.setValue(ENABLED, facingState.getValue(ENABLED));
            } else {
                return Blocks.AIR.defaultBlockState();
            }
        } else if (half == DoubleBlockHalf.LOWER && facing == Direction.DOWN && !state.canSurvive(level, currentPos)) {
            return Blocks.AIR.defaultBlockState();
        }

        return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        BlockPos blockpos = pos.above();
        level.setBlock(blockpos, this.defaultBlockState().setValue(HALF, DoubleBlockHalf.UPPER).setValue(FACING, state.getValue(FACING)).setValue(ENABLED, state.getValue(ENABLED)), 3);
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
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new BeaverStatueBlockEntity(blockPos, blockState);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, HALF, ENABLED);
    }
}

package net.migueel26.faunaandorchestra.block.custom;

import com.mojang.serialization.MapCodec;
import net.migueel26.faunaandorchestra.block.entity.TermiteChestBlockEntity;
import net.migueel26.faunaandorchestra.entity.custom.TermiteEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TermiteChestBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final MapCodec<TermiteChestBlock> CODEC = simpleCodec(TermiteChestBlock::new);
    protected static final VoxelShape SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);
    public static final int MAX_DISTANCE_TO_LISTENER = 20;

    public TermiteChestBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof TermiteChestBlockEntity termiteChestBlockEntity) {
            BlockPos blockpos = pos.above();
            if (level.getBlockState(blockpos).isRedstoneConductor(level, blockpos)) {
                return InteractionResult.sidedSuccess(level.isClientSide);
            } else if (level.isClientSide) {
                return InteractionResult.SUCCESS;
            } else {
                ((ServerPlayer) player).openMenu(new SimpleMenuProvider(termiteChestBlockEntity, termiteChestBlockEntity.getDisplayName()), pos);
                if (!termiteChestBlockEntity.hasTermite()) {
                    termiteChestBlockEntity.insertAnim();
                }
                return InteractionResult.CONSUME;
            }
        } else {
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        // We delete the Termites associated with this Termite Chest when it is destroyed
        AABB aabb = AABB.ofSize(pos.getCenter(), MAX_DISTANCE_TO_LISTENER + 10, MAX_DISTANCE_TO_LISTENER + 10, MAX_DISTANCE_TO_LISTENER + 10);
        List<TermiteEntity> termites = level.getEntitiesOfClass(TermiteEntity.class, aabb,
                (termite) -> termite.getOriginPos().equals(pos));
        for (TermiteEntity termite : termites) {
            termite.discard();
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public @Nullable PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.IGNORE;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getHorizontalDirection().getOpposite();
        return this.defaultBlockState().setValue(FACING, direction);
    }


    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new TermiteChestBlockEntity(blockPos, blockState);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}

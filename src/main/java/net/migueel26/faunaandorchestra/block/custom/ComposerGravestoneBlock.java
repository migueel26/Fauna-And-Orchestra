package net.migueel26.faunaandorchestra.block.custom;

import com.mojang.serialization.MapCodec;
import net.migueel26.faunaandorchestra.block.entity.ComposerGravestoneBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ComposerGravestoneBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final MapCodec<ComposerGravestoneBlock> CODEC = simpleCodec(ComposerGravestoneBlock::new);
    public static final EnumProperty<BedPart> PART = BlockStateProperties.BED_PART;
    public static final BooleanProperty OPENED = BlockStateProperties.OPEN;
    private static final VoxelShape SOUTH_SHAPE = Block.box(1, 0, 1, 15, 8, 16);
    private static final VoxelShape NORTH_SHAPE = Block.box(1, 0, 0, 15, 8, 15);
    private static final VoxelShape WEST_SHAPE = Block.box(0, 0, 1, 15, 8, 15);
    private static final VoxelShape EAST_SHAPE = Block.box(1, 0, 1, 16, 8, 15);


    public ComposerGravestoneBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(PART, BedPart.FOOT).setValue(OPENED, false));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (getConnectedDirection(state)) {
            case NORTH -> NORTH_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
            case WEST -> WEST_SHAPE;
            default -> EAST_SHAPE;
        };
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ComposerGravestoneBlockEntity(pos, state);
    }

    @Override
    protected @NotNull BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (direction == getNeighbourDirection(state.getValue(PART), state.getValue(FACING))) {
            if (neighborState.is(this) && neighborState.getValue(PART) != state.getValue(PART)) {
                return state.setValue(OPENED, neighborState.getValue(OPENED));
            } else {
                return Blocks.AIR.defaultBlockState();
            }
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide()) {
            BlockPos blockPos = pos.relative(state.getValue(FACING).getOpposite());
            level.setBlock(blockPos, state.setValue(PART, BedPart.HEAD), 3);
            level.blockUpdated(pos, Blocks.AIR);
            state.updateNeighbourShapes(level, pos, 3);
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getHorizontalDirection();
        BlockPos blockpos = context.getClickedPos();
        BlockPos blockpos1 = blockpos.relative(direction);
        Level level = context.getLevel();
        return level.getBlockState(blockpos1).canBeReplaced(context) && level.getWorldBorder().isWithinBounds(blockpos1)
                ? this.defaultBlockState().setValue(FACING, direction.getOpposite())
                : null;
    }

    public static Direction getConnectedDirection(BlockState state) {
        Direction direction = state.getValue(FACING);
        return state.getValue(PART) == BedPart.FOOT ? direction.getOpposite() : direction;
    }

    @Override
    public @Nullable PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof ComposerGravestoneBlockEntity composerGravestoneBlockEntity) {
            Vec3 vecPos;
            BlockPos neighbourPos = pos.relative(state.getValue(FACING));

            if (state.getValue(PART) == BedPart.HEAD) {
                // We get the foot, which renders and plays the animation
                vecPos = neighbourPos.getBottomCenter();
                composerGravestoneBlockEntity = (ComposerGravestoneBlockEntity) level.getBlockEntity(neighbourPos);
            } else {
                vecPos = pos.getBottomCenter();
            }

            Vec3 center;
            int diffX = neighbourPos.getX() - pos.getX();
            int diffZ = neighbourPos.getZ() - pos.getZ();
            double xOffset = 0.25;
            double zOffset = 0.25;

            if (diffX > 0) {
                center = vecPos.subtract(0.5, 0, 0);
                xOffset = 0.4;
            } else if (diffX < 0) {
                center = vecPos.add(0.5, 0, 0);
                xOffset = 0.4;
            } else if (diffZ > 0) {
                center = vecPos.subtract(0, 0, 0.5);
                zOffset = 0.4;
            } else {
                center = vecPos.add(0, 0, 0.5);
                zOffset = 0.4;
            }

            composerGravestoneBlockEntity.shake();
            player.displayClientMessage(Component.translatable("text.faunaandorchestra.locked_tomb"), true);
            player.playSound(SoundEvents.GRINDSTONE_USE, 1.0F, 0.5F);
            if (!level.isClientSide()) {
                ((ServerLevel) level).sendParticles(new BlockParticleOption(ParticleTypes.FALLING_DUST, Blocks.STONE.defaultBlockState()),
                        center.x, center.y + 0.5F, center.z, 20,
                        xOffset, 0, zOffset, 1.0F);
            }
        }
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    private static Direction getNeighbourDirection(BedPart part, Direction direction) {
        return part == BedPart.HEAD ? direction : direction.getOpposite();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART, OPENED);
    }
}

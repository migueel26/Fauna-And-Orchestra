package net.migueel26.faunaandorchestra.block.custom;

import com.mojang.serialization.MapCodec;
import net.migueel26.faunaandorchestra.block.entity.ComposerGravestoneBlockEntity;
import net.migueel26.faunaandorchestra.block.entity.TipCaseBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TipCaseBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final MapCodec<TipCaseBlock> CODEC = simpleCodec(TipCaseBlock::new);
    public static final EnumProperty<BedPart> PART = BlockStateProperties.BED_PART;
    public static final IntegerProperty TIPS = IntegerProperty.create("tip_amount", 0, 64);
    private static final VoxelShape SOUTH_SHAPE = Shapes.or(
            Block.box(2.5, 0, 2.5, 12.5, 4, 10),
            Block.box(3.75, 0, 10, 11.25, 4, 15));
    private static final VoxelShape NORTH_SHAPE = Block.box(1, 0, 0, 15, 8, 15);
    private static final VoxelShape WEST_SHAPE = Block.box(0, 0, 1, 15, 8, 15);
    private static final VoxelShape EAST_SHAPE = Block.box(1, 0, 1, 16, 8, 15);
    public TipCaseBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(PART, BedPart.FOOT).setValue(TIPS, 0));
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

    public static Direction getConnectedDirection(BlockState state) {
        Direction direction = state.getValue(FACING);
        return state.getValue(PART) == BedPart.FOOT ? direction.getOpposite() : direction;
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TipCaseBlockEntity(pos, state);
    }

    @Override
    protected @NotNull BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (direction == getNeighbourDirection(state.getValue(PART), state.getValue(FACING))) {
            if (neighborState.is(this) && neighborState.getValue(PART) != state.getValue(PART)) {
                return state.setValue(TIPS, neighborState.getValue(TIPS));
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
            ((TipCaseBlockEntity) level.getBlockEntity(blockPos)).setOwner(placer.getUUID());
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

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        if (player.getUUID() == ((TipCaseBlockEntity) blockEntity).getOwner()) {
            level.addFreshEntity(new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.GOLD_INGOT,
                    state.getValue(TIPS))));
        }
        super.playerDestroy(level, player, pos, state, blockEntity, tool);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {

        if (player.getItemInHand(hand).is(Items.GOLD_INGOT) && state.getValue(TIPS) < 64) {
            stack.consume(1, player);
            level.setBlock(pos, state.setValue(TIPS, state.getValue(TIPS) + 1), 3);

        } else if (player.getItemInHand(hand).isEmpty() && state.getValue(TIPS) > 0 && !level.isClientSide()) {
            if (((TipCaseBlockEntity) level.getBlockEntity(pos)).getOwner() == player.getUUID()) {
                int tips = state.getValue(TIPS);
                player.setItemInHand(hand, new ItemStack(Items.GOLD_INGOT, tips));
                level.setBlock(pos, state.setValue(TIPS, 0), 3);
            } else {
                player.displayClientMessage(Component.translatable("block.faunaandorchestra.tip_case_theft"), true);
            }
        }

        if (!level.isClientSide()) {
            Vec3 center = pos.getCenter();
            ((ServerLevel) level).sendParticles(ParticleTypes.WAX_ON, center.x, center.y, center.z,
                    2, 0.15,0.15, 0.15, 0.5);
        } else {
            level.playSound(player, pos, SoundEvents.AMETHYST_BLOCK_HIT, SoundSource.BLOCKS, 1.0F, 1.0F);
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
        builder.add(FACING, PART, TIPS);
    }
}

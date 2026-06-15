package net.migueel26.faunaandorchestra.block.custom;

import com.mojang.serialization.MapCodec;
import net.migueel26.faunaandorchestra.block.entity.ComposerGravestoneBlockEntity;
import net.migueel26.faunaandorchestra.block.entity.TipCaseBlockEntity;
import net.migueel26.faunaandorchestra.entity.custom.Faust;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class TipCaseBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final MapCodec<TipCaseBlock> CODEC = simpleCodec(TipCaseBlock::new);
    public static final EnumProperty<BedPart> PART = BlockStateProperties.BED_PART;
    public static final int FIRST_REWARD = 6;
    public static final int SECOND_REWARD = 24;
    public static final int THIRD_REWARD = 64;
    public static final BooleanProperty FIRST = BooleanProperty.create("first");
    public static final BooleanProperty SECOND = BooleanProperty.create("second");
    public static final BooleanProperty THIRD = BooleanProperty.create("third");
    private static final VoxelShape SOUTH_SHAPE = Shapes.or(
            Block.box(2.5, 0, 2.5, 13.5, 5, 11),
            Block.box(3.75, 0, 10, 12.25, 5, 16)
    );
    private static final VoxelShape NORTH_SHAPE = Shapes.or(
            Block.box(3.75, 0, 0, 12.25, 5, 5),
            Block.box(2.5, 0, 5, 13.5, 5, 13.5)
    );
    private static final VoxelShape WEST_SHAPE = Shapes.or(
            Block.box(0, 0, 3.75, 5, 5, 12.25),
            Block.box(5, 0, 2.5, 13.5, 5, 13.5)
    );
    private static final VoxelShape EAST_SHAPE = Shapes.or(
            Block.box(2.5, 0, 2.5, 11, 5, 13.5),
            Block.box(10, 0, 3.75, 16, 5, 12.25)
    );

    public static final VoxelShape NORTH_SHAPE_H = Block.box(5.5, 0, 0, 10.5, 5, 13.9);
    public static final VoxelShape SOUTH_SHAPE_H = Block.box(5.5, 0, 2.1, 10.5, 5, 16);
    public static final VoxelShape EAST_SHAPE_H = Block.box(2.1, 0, 5.5, 16, 5, 10.5);
    public static final VoxelShape WEST_SHAPE_H = Block.box(0, 0, 5.5, 13.9, 5, 10.5);

    public TipCaseBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(PART, BedPart.FOOT)
                .setValue(FIRST, true)
                .setValue(SECOND, true)
                .setValue(THIRD, true));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (getConnectedDirection(state)) {
            case NORTH -> state.getValue(PART) == BedPart.FOOT ? NORTH_SHAPE : NORTH_SHAPE_H;
            case SOUTH -> state.getValue(PART) == BedPart.FOOT ? SOUTH_SHAPE : SOUTH_SHAPE_H;
            case WEST -> state.getValue(PART) == BedPart.FOOT ? WEST_SHAPE : WEST_SHAPE_H;
            default -> state.getValue(PART) == BedPart.FOOT ? EAST_SHAPE : EAST_SHAPE_H;
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
            if (!neighborState.is(this) || neighborState.getValue(PART) == state.getValue(PART)) {
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
        if (player.getUUID().equals(((TipCaseBlockEntity) blockEntity).getOwner())) {
            level.addFreshEntity(new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.GOLD_INGOT,
                    ((TipCaseBlockEntity) blockEntity).getTips())));
        }
        super.playerDestroy(level, player, pos, state, blockEntity, tool);
    }

    @Override
    public @Nullable PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.DESTROY;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof TipCaseBlockEntity tipCase) {
            int tips = tipCase.getTips();
            if (player.getItemInHand(hand).is(Items.GOLD_INGOT) && tips < THIRD_REWARD) {
                // Tip gold
                stack.consume(1, player);
                int nextTips = tips + 1;

                updateTipsBothHalves(level, pos, state, nextTips);

                if (!level.isClientSide()) {
                    Entity entity = null;
                    if (tipCase.getOwner() != null) entity = ((ServerLevel) level).getEntity(tipCase.getOwner());
                    if (entity instanceof Faust) giveRingtailsTipAward(state, level, pos, player, nextTips);
                }

            } else if (player.getItemInHand(hand).is(Items.GOLD_INGOT) && tips == THIRD_REWARD) {
                // Try to tip gold but it's full
                return ItemInteractionResult.FAIL;

            } else if (player.getItemInHand(hand).isEmpty() && tips > 0 && !level.isClientSide()) {
                // Try to get gold (you may be the owner or not)
                UUID tipCaseUUID = ((TipCaseBlockEntity) level.getBlockEntity(pos)).getOwner();
                UUID playerUUID = player.getUUID();

                if (tipCaseUUID.equals(playerUUID)) {
                    player.setItemInHand(hand, new ItemStack(Items.GOLD_INGOT, tips));
                    updateTipsBothHalves(level, pos, state, 0);
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
        }
        return ItemInteractionResult.SUCCESS;
    }

    private static void giveRingtailsTipAward(BlockState state, Level level, BlockPos pos, Player player, int tips) {
        if (tips == FIRST_REWARD && state.getValue(TipCaseBlock.FIRST)) {
            popResourceFromFace(level, pos, Direction.UP, new ItemStack(ModItems.MUSIC_BOTTLE.get()));
            level.playSound(null, pos,
                    ModSounds.SUCCESSFUL_TAME.get(), SoundSource.NEUTRAL, 1.0F, 1.0F);
        } else if (tips == SECOND_REWARD && state.getValue(TipCaseBlock.SECOND)) {
            popResourceFromFace(level, pos, Direction.UP, new ItemStack(ModItems.SHEET_FRAGMENTS.get()));
            level.playSound(null, pos,
                    ModSounds.SUCCESSFUL_TAME.get(), SoundSource.NEUTRAL, 1.0F, 1.0F);
        } else if (tips == THIRD_REWARD && state.getValue(TipCaseBlock.THIRD)) {
            popResourceFromFace(level, pos, Direction.UP, new ItemStack(ModItems.RINGTAILS_POSTER.get()));
            level.playSound(null, pos,
                    ModSounds.SUCCESSFUL_TAME.get(), SoundSource.NEUTRAL, 1.0F, 1.0F);
        }
    }

    private void updateTipsBothHalves(Level level, BlockPos pos, BlockState state, int newTips) {
        if (level.getBlockEntity(pos) instanceof TipCaseBlockEntity be) {
            be.setTips(newTips);
        }

        BlockPos otherHalfPos = pos.relative(getConnectedDirection(state));
        if (level.getBlockEntity(otherHalfPos) instanceof TipCaseBlockEntity otherBe) {
            otherBe.setTips(newTips);
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        TipCaseBlockEntity blockEntity = (TipCaseBlockEntity) level.getBlockEntity(pos);
        if (!level.isClientSide()) {
            // If Faust no longer exists, the tip case is owner-free
            if (blockEntity.getOwner() != null && faustIsRemoved(level, blockEntity)) {
                blockEntity.setOwner(null);
                BlockPos blockPos = pos.relative(state.getValue(FACING).getOpposite());
                ((TipCaseBlockEntity) level.getBlockEntity(blockPos)).setOwner(null);

            }
        }

        super.tick(state, level, pos, random);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("block.faunaandorchestra.tip_case.desc")
                    .withStyle(ChatFormatting.GRAY));
        } else {
            tooltipComponents.add(Component.translatable("tooltip.faunaandorchestra.shift"));
        }
    }

    private static boolean faustIsRemoved(ServerLevel level, TipCaseBlockEntity blockEntity) {
        return (level.getEntity(blockEntity.getOwner()) instanceof Faust faust)
                && (faust.isDeadOrDying() || faust.isRemoved());
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
        builder.add(FACING, PART, FIRST, SECOND, THIRD);
    }
}

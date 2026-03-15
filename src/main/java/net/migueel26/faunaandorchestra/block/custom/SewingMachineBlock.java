package net.migueel26.faunaandorchestra.block.custom;

import com.mojang.serialization.MapCodec;
import net.migueel26.faunaandorchestra.block.entity.SewingMachineBlockEntity;
import net.migueel26.faunaandorchestra.component.ModDataComponents;
import net.migueel26.faunaandorchestra.entity.custom.koala_workers.TailorKoalaEntity;
import net.migueel26.faunaandorchestra.util.ModTags;
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
import net.minecraft.world.entity.Entity;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class SewingMachineBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final MapCodec<SewingMachineBlock> CODEC = simpleCodec(SewingMachineBlock::new);
    public static final EnumProperty<BedPart> PART = BlockStateProperties.BED_PART;
    public static final BooleanProperty SEWING = BooleanProperty.create("sewing");
    // TABLE SHAPE
    public static final VoxelShape TOP = Block.box(1, 6, 1, 15, 9, 15);
    public static final VoxelShape CORNER_SW = Block.box(13, 0, 1, 15, 6, 3);
    public static final VoxelShape CORNER_SE = Block.box(1, 0, 1, 3, 6, 3);
    public static final VoxelShape CORNER_NW = Block.box(13, 0, 13, 15, 6, 15);
    public static final VoxelShape CORNER_NE = Block.box(1, 0, 13, 3, 6, 15);
    public static final VoxelShape SHAPE_TABLE = Shapes.or(TOP, CORNER_SW, CORNER_NE, CORNER_SE, CORNER_NW);
    // STOOL SHAPE
    public static final VoxelShape TOP_STOOL_NORTH = Block.box(4.5, 2.5, 9, 11.5, 4, 16);
    public static final VoxelShape TOP_STOOL_SOUTH = Block.box(4.5, 2.5, 0, 11.5, 4, 7);
    public static final VoxelShape TOP_STOOL_EAST = Block.box(0, 2.5, 4.5, 7, 4, 11.5);
    public static final VoxelShape TOP_STOOL_WEST = Block.box(9, 2.5, 4.5, 16, 4, 11.5);
    public SewingMachineBlock(Properties properties) {
        super(properties);

        this.registerDefaultState(this.getStateDefinition().any().setValue(PART, BedPart.FOOT).setValue(SEWING, false));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(PART)) {
            case HEAD -> SHAPE_TABLE;
            case FOOT -> switch (state.getValue(FACING)) {
                case NORTH -> TOP_STOOL_NORTH;
                case SOUTH -> TOP_STOOL_SOUTH;
                case WEST -> TOP_STOOL_WEST;
                default -> TOP_STOOL_EAST;
            };
        };
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        BlockPos tablePos = pos;
        if (state.getValue(PART) == BedPart.FOOT) {
            tablePos = pos.relative(state.getValue(FACING));
        }
        if (level.getBlockEntity(tablePos) instanceof SewingMachineBlockEntity blockEntity) {
            if (stack.is(ModTags.Items.IS_BATON) && !level.isClientSide()) {
                // ASSIGN
                UUID uuid = stack.get(ModDataComponents.MUSICIAN_UUID);
                if (uuid != null && level instanceof ServerLevel serverLevel) {
                    Entity entity = serverLevel.getEntity(uuid);
                    if (entity instanceof TailorKoalaEntity koala && koala.isAlive() && entity.distanceToSqr(pos.getCenter()) < 150) {
                        // We clear the UUID
                        stack.set(ModDataComponents.MUSICIAN_UUID, null);
                        BlockPos stoolPos = pos.equals(tablePos) ? pos.relative(state.getValue(FACING)) : pos;

                        koala.getNavigation().moveTo(stoolPos.getCenter().x, stoolPos.getY(), stoolPos.getCenter().z, 0, 1.0f);

                        return ItemInteractionResult.SUCCESS;
                    }
                }
            } else {
                // EMPTY HAND AND NOT SEWING
                blockEntity.oops();
                player.displayClientMessage(Component.translatable("block.faunaandorchestra.sewing_machine.cant_sew"), true);
                level.playSound(player, tablePos, SoundEvents.GRINDSTONE_USE, SoundSource.BLOCKS);
                if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.FALLING_DUST, Blocks.STONE.defaultBlockState()),
                            tablePos.getCenter().x, tablePos.getCenter().y, tablePos.getCenter().z, 5, 0.3f, 0.2f, 0.3f, 1.0f);
                }

                return ItemInteractionResult.SUCCESS;
            }
        }

        return ItemInteractionResult.FAIL;
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        // We set the working station to the koala when it arrives the stool
        if (state.getValue(PART) == BedPart.FOOT && entity instanceof TailorKoalaEntity koala && !koala.hasWorkingStation()) {
            koala.setWorkingStation(pos);

            Direction facing = state.getValue(FACING);

            double y = pos.getY() + 0.25;
            double x = pos.getX();
            double z = pos.getZ();

            // We calculate the stool position depending on the direction
            switch (facing) {
                case NORTH -> {
                    x += 0.5;
                    z += 0.75125;
                }
                case SOUTH -> {
                    x += 0.5;
                    z += 0.24875;
                }
                case EAST -> {
                    x += 0.24875;
                    z += 0.5;
                }
                case WEST -> {
                    x += 0.75125;
                    z += 0.5;
                }
                default -> {
                    x += 0.5;
                    z += 0.5;
                }
            }

            koala.moveTo(x, y, z);
        }
        super.entityInside(state, level, pos, entity);
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new SewingMachineBlockEntity(blockPos, blockState);
    }

    @Override
    protected @NotNull BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (direction == getNeighbourDirection(state.getValue(PART), state.getValue(FACING))) {
            if (neighborState.is(this) && neighborState.getValue(PART) != state.getValue(PART)) {
                return state.setValue(SEWING, neighborState.getValue(SEWING));
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
        return PushReaction.IGNORE;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    protected static Direction getNeighbourDirection(BedPart part, Direction direction) {
        return part == BedPart.HEAD ? direction : direction.getOpposite();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART, SEWING);
    }
}

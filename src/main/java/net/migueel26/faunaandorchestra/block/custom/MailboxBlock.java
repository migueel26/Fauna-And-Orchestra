package net.migueel26.faunaandorchestra.block.custom;

import com.mojang.serialization.MapCodec;
import net.migueel26.faunaandorchestra.block.entity.MailboxBlockEntity;
import net.migueel26.faunaandorchestra.entity.ModEntities;
import net.migueel26.faunaandorchestra.entity.custom.misc.MailbirdMacawEntity;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.migueel26.faunaandorchestra.util.BlocksUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleMenuProvider;
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
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class MailboxBlock extends HorizontalDirectionalBlock implements EntityBlock {
    protected static final int TIME_TO_SEND = 10; //300; // SECONDS
    public static final MapCodec<MailboxBlock> CODEC = simpleCodec(MailboxBlock::new);
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    public static final BooleanProperty MAILBIRD = BooleanProperty.create("mailbird");
    public static final IntegerProperty TIME_AWAY = IntegerProperty.create("time_away", 0, 300);
    protected final VoxelShape LOWER_SHAPE = Shapes.or(
            Block.box(5f, 0f, 5f, 11f, 2f, 11f),
            Block.box(6.5f, 2f, 6.5f, 9.5f, 14f, 9.5f),
            Block.box(3.5f, 14f, 3.5f, 12.5f, 16f, 12.5f)
    );
    protected final VoxelShape UPPER_SHAPE = Block.box(4.25f, 0f, 4.25f, 11.75f, 6f, 11.75f);

    public MailboxBlock(Properties properties) {
        super(properties);

        this.registerDefaultState(this.getStateDefinition().any().setValue(HALF, DoubleBlockHalf.LOWER).setValue(MAILBIRD, true).setValue(TIME_AWAY, 0));
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (level.getBlockEntity(pos) instanceof MailboxBlockEntity blockEntity && state.getValue(MAILBIRD) && state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            macawArriveAnimation(state, level, pos, blockEntity);
        }
    }

    private static void macawArriveAnimation(BlockState state, Level level, BlockPos pos, MailboxBlockEntity blockEntity) {
        blockEntity.arrive();
        if (!level.isClientSide()) {
            Direction direction = state.getValue(FACING).getOpposite();
            Vec3 particlePos = pos.above().relative(direction, 2).getCenter();
            ((ServerLevel) level).sendParticles(ParticleTypes.CLOUD, particlePos.x, particlePos.y + 1, particlePos.z,
                    20, 0.5, 0.5, 0.5, 0.15);
            level.playSound(null, pos, SoundEvents.PARROT_AMBIENT, SoundSource.BLOCKS, 2.0f, 1.0f);
        }
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
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        BlockPos bePos = state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos : pos.below();

        // We always access the lower half inventory
        if (state.getValue(MAILBIRD) && level.getBlockEntity(bePos) instanceof MailboxBlockEntity mailbox) {
            if (!level.isClientSide()) {
                player.openMenu(new SimpleMenuProvider(mailbox, mailbox.getDisplayName()), bePos);
            } else {
                player.playSound(SoundEvents.BARREL_OPEN, 1.5f, 1.0f + ((level.random.nextFloat()/2)-0.25f));
            }
        } else {
            player.displayClientMessage(Component.translatable("block.faunaandorchestra.mailbox.no_mailbird"), true);
        }
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int time = state.getValue(TIME_AWAY);
        BlockPos topPos = state.getValue(HALF) == DoubleBlockHalf.UPPER ? pos : pos.above();

        if (time == 0) {
            MailbirdMacawEntity entity = new MailbirdMacawEntity(ModEntities.MACAW.get(), level);

            // Above to place it on top of the mailbox
            entity.moveTo(topPos.above(), 0f, 0f);
            entity.setYHeadRot(getYRot(state.getValue(FACING)));
            entity.setYBodyRot(entity.getYRot());
            level.addFreshEntity(entity);
            entity.flyAway();

            level.scheduleTick(pos, this, 20);
            level.setBlock(pos, state.setValue(MAILBIRD, false).setValue(TIME_AWAY, 1), 3);

            level.playSound(null, pos, SoundEvents.PARROT_AMBIENT, SoundSource.BLOCKS);
        } else if (time == TIME_TO_SEND) {
            BlockPos lowerPos = state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos : pos.below();
            // The lower inventory
            if (level.getBlockEntity(lowerPos) instanceof MailboxBlockEntity blockEntity) {
                blockEntity.deliverMail();
                macawArriveAnimation(state, level, pos, blockEntity);

                // Reset the mailbox
                level.setBlock(pos, state.setValue(MAILBIRD, true).setValue(TIME_AWAY, 0), 3);
            }
        } else {
            level.setBlock(pos, state.setValue(MAILBIRD, false).setValue(TIME_AWAY, time + 1), 3);
            level.scheduleTick(pos, this, 20);
        }

        super.tick(state, level, pos, random);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!newState.is(state.getBlock())) {
            if (level.getBlockEntity(pos) instanceof MailboxBlockEntity mailbox) {
                BlocksUtil.dropContents(level, pos, mailbox.inventory);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        DoubleBlockHalf half = state.getValue(HALF);

        if (facing.getAxis() == Direction.Axis.Y && (half == DoubleBlockHalf.LOWER) == (facing == Direction.UP)) {
            if (facingState.is(this) && facingState.getValue(HALF) != half) {
                return state.setValue(MAILBIRD, facingState.getValue(MAILBIRD)).setValue(TIME_AWAY, facingState.getValue(TIME_AWAY));
            } else {
                return Blocks.AIR.defaultBlockState();
            }
        } else if (half == DoubleBlockHalf.LOWER && facing == Direction.DOWN && !state.canSurvive(level, currentPos)) {
            return Blocks.AIR.defaultBlockState();
        } else if (half == DoubleBlockHalf.UPPER && !level.getBlockState(currentPos.above()).isEmpty()) {
            return Blocks.AIR.defaultBlockState();
        }

        return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        BlockPos blockpos = pos.above();
        level.setBlock(blockpos, this.defaultBlockState().setValue(HALF, DoubleBlockHalf.UPPER).setValue(FACING, state.getValue(FACING)).setValue(MAILBIRD, state.getValue(MAILBIRD)), 3);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos blockpos = context.getClickedPos();
        Level level = context.getLevel();
        Direction direction = context.getHorizontalDirection().getOpposite();
        return blockpos.getY() < level.getMaxBuildHeight() - 1 && level.getBlockState(blockpos.above()).canBeReplaced(context) && level.getBlockState(blockpos.above(2)).isEmpty() ? this.defaultBlockState().setValue(FACING, direction) : null;
    }

    @Override
    public @Nullable PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.IGNORE;
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

    private float getYRot(Direction facing) {
        return switch (facing) {
            case NORTH -> 180f;
            case SOUTH -> 0f;
            case WEST  -> 90f;
            case EAST  -> -90f;
            default -> 0f;
        };
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
        return new MailboxBlockEntity(blockPos, blockState);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, HALF, MAILBIRD, TIME_AWAY);
    }
}

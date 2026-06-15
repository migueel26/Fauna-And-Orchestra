package net.migueel26.faunaandorchestra.block.custom;

import com.mojang.serialization.MapCodec;
import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.block.ModBlockEntities;
import net.migueel26.faunaandorchestra.block.entity.MailboxBlockEntity;
import net.migueel26.faunaandorchestra.entity.ModEntities;
import net.migueel26.faunaandorchestra.entity.custom.jazzy_dammys.DanB;
import net.migueel26.faunaandorchestra.entity.custom.misc.MailbirdMacawEntity;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.migueel26.faunaandorchestra.util.AdvancementUtil;
import net.migueel26.faunaandorchestra.util.BlocksUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
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

import java.util.List;

public class MailboxBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final int TIME_TO_SEND = 200; //6000; // SECONDS
    public static final MapCodec<MailboxBlock> CODEC = simpleCodec(MailboxBlock::new);
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    public static final BooleanProperty MAILBIRD = BooleanProperty.create("mailbird");
    protected final VoxelShape LOWER_SHAPE = Shapes.or(
            Block.box(5f, 0f, 5f, 11f, 2f, 11f),
            Block.box(6.5f, 2f, 6.5f, 9.5f, 14f, 9.5f),
            Block.box(3.5f, 14f, 3.5f, 12.5f, 16f, 12.5f)
    );
    protected final VoxelShape UPPER_SHAPE = Block.box(4.25f, 0f, 4.25f, 11.75f, 6f, 11.75f);

    public MailboxBlock(Properties properties) {
        super(properties);

        this.registerDefaultState(this.getStateDefinition().any().setValue(HALF, DoubleBlockHalf.LOWER).setValue(MAILBIRD, true));
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (level.getBlockEntity(pos) instanceof MailboxBlockEntity blockEntity && state.getValue(MAILBIRD) && state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            blockEntity.macawArriveAnimation(state, level, pos);
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
                addDanLetter(mailbox, player);
                player.openMenu(new SimpleMenuProvider(mailbox, mailbox.getDisplayName()), bePos);
            } else {
                player.playSound(SoundEvents.BARREL_OPEN, 1.5f, 1.0f + ((level.random.nextFloat()/2)-0.25f));
            }
        } else {
            player.displayClientMessage(Component.translatable("block.faunaandorchestra.mailbox.no_mailbird"), true);
        }
        return ItemInteractionResult.SUCCESS;
    }

    private void addDanLetter(MailboxBlockEntity blockEntity, Player player) {
        CompoundTag persistentData = player.getPersistentData();
        CompoundTag data = persistentData.getCompound(ServerPlayer.PERSISTED_NBT_TAG);
        int myths = data.getInt(DanB.MYTHS_DATA_KEY);
        int slot = blockEntity.getEmptySlotIndex();

        if (slot == -1 || !blockEntity.getOwner().equals(player.getUUID())) return;

        ItemStack stack = new ItemStack(Items.PAPER);

        stack.applyComponents(DataComponentMap.builder()
                        .set(DataComponents.ITEM_NAME, Component.translatable("item.faunaandorchestra.letter_dan"))
                        .set(DataComponents.LORE, new ItemLore(List.of(Component.translatable("item.faunaandorchestra.letter_dan.desc").withStyle(ChatFormatting.GRAY))))
                .build());

        boolean addLetter = false;

        if (canReceiveMythZero(player, myths)) {
            data.putInt(DanB.MYTHS_DATA_KEY, myths | 1);
            addLetter = true;
        } else if (canReceiveMythOne(player, myths)) {
            data.putInt(DanB.MYTHS_DATA_KEY, myths | 2);
            addLetter = true;
        } else if (canReceiveMythTwo(player, myths)) {
            data.putInt(DanB.MYTHS_DATA_KEY, myths | 4);
            addLetter = true;
        }

        if (addLetter) {
            persistentData.put(ServerPlayer.PERSISTED_NBT_TAG, data);

            if (AdvancementUtil.hasAdvancement(player, FaunaAndOrchestra.MOD_ID, "meet_jazzy_dammys")) {
                blockEntity.inventory.setStackInSlot(slot, stack);
            }
        }
    }

    public boolean canReceiveMythZero(Player player, int myths) {
        return (myths & 1) == 0
                && !AdvancementUtil.hasAdvancement(player, FaunaAndOrchestra.MOD_ID, "dan_myth0")
                && AdvancementUtil.hasAdvancement(player, ResourceLocation.DEFAULT_NAMESPACE, "story/lava_bucket");
    }

    public boolean canReceiveMythOne(Player player, int myths) {
        return (myths & 2) == 0
                && !AdvancementUtil.hasAdvancement(player, FaunaAndOrchestra.MOD_ID, "dan_myth1")
                && AdvancementUtil.hasAdvancement(player, ResourceLocation.DEFAULT_NAMESPACE, "adventure/hero_of_the_village");
    }

    public boolean canReceiveMythTwo(Player player, int myths) {
        return (myths & 4) == 0
                && !AdvancementUtil.hasAdvancement(player, FaunaAndOrchestra.MOD_ID, "dan_myth2")
                && AdvancementUtil.hasAdvancement(player, ResourceLocation.DEFAULT_NAMESPACE, "nether/explore_nether");
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return type == ModBlockEntities.MAILBOX_BE.get() ?
                (BlockEntityTicker<T>) (lvl, p, st, be) -> MailboxBlockEntity.tick(lvl, p, st, (MailboxBlockEntity) be) : null;
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
                return state.setValue(MAILBIRD, facingState.getValue(MAILBIRD));
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
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            ((MailboxBlockEntity) level.getBlockEntity(pos)).setOwner(placer.getUUID());
            ((MailboxBlockEntity) level.getBlockEntity(blockpos)).setOwner(placer.getUUID());
        }
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

    public static float getYRot(Direction facing) {
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
        builder.add(FACING, HALF, MAILBIRD);
    }
}

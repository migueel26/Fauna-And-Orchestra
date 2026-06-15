package net.migueel26.faunaandorchestra.block.custom;

import net.migueel26.faunaandorchestra.block.ModBlockEntities;
import net.migueel26.faunaandorchestra.block.ModBlocks;
import net.migueel26.faunaandorchestra.block.entity.ListenerContainerBlockEntity;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ListenerContainerBlock extends Block implements EntityBlock, ListeningBlock {
    public static final BooleanProperty LISTENING = BooleanProperty.create("listening");
    public static final BooleanProperty BOTTLE = BooleanProperty.create("bottle");

    public static final VoxelShape BOTTOM = Block.box(0, 0, 0, 16, 2, 16);
    public static final VoxelShape TOP = Block.box(0, 14, 0, 16, 16, 16);
    public static final VoxelShape CORNER_SW = Block.box(14, 2, 0, 14, 14, 2);
    public static final VoxelShape CORNER_SE = Block.box(0, 2, 0, 2, 14, 2);
    public static final VoxelShape CORNER_NW = Block.box(14, 2, 14, 16, 14, 16);
    public static final VoxelShape CORNER_NE = Block.box(0, 2, 14, 2, 14, 16);
    public static final VoxelShape SHAPE = Shapes.or(BOTTOM, TOP, CORNER_SW, CORNER_NE, CORNER_SE, CORNER_NW);

    public ListenerContainerBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(LISTENING, false)
                .setValue(BOTTLE, false));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("block.faunaandorchestra.listener_container.desc")
                    .withStyle(ChatFormatting.GRAY));
        } else {
            tooltipComponents.add(Component.translatable("tooltip.faunaandorchestra.shift"));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (!oldState.is(ModBlocks.LISTENER_CONTAINER) && level.getBlockState(pos.above()).is(ModBlocks.LISTENER)) {
            if (!level.isClientSide()) {
                ((ServerLevel) level).sendParticles(ParticleTypes.POOF, pos.getCenter().x, pos.getY() + 0.5, pos.getCenter().z, 25, 0.1, 0.5, 0.1, 0.2);
                level.playSound(null, pos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 1.0F, 2.0F);
            }
        }

        if (level.getBlockEntity(pos) instanceof ListenerContainerBlockEntity be && level.getBlockState(pos.above()).is(ModBlocks.LISTENER)) {
            tryToStartListening((ServerLevel) level, pos, be);
        }

        super.onPlace(state, level, pos, oldState, movedByPiston);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!newState.is(ModBlocks.LISTENER_CONTAINER) && state.getValue(BOTTLE) && level.getBlockEntity(pos) instanceof ListenerContainerBlockEntity be) {
            if (be.getDroplets() == 64) {
                popResourceFromFace(level, pos, Direction.UP, new ItemStack(ModItems.MUSIC_BOTTLE.get()));
            } else {
                popResourceFromFace(level, pos, Direction.UP, new ItemStack(Items.GLASS_BOTTLE));
            }

            if (level.getBlockState(pos.above()).is(ModBlocks.LISTENER)) {
                level.setBlock(pos.above(), level.getBlockState(pos.above()).setValue(ListenerBlock.LISTENING, false), 3);
            }
    }

        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public @Nullable PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return type == ModBlockEntities.LISTENER_CONTAINER_BE.get() ?
                (BlockEntityTicker<T>) (lvl, p, st, be) -> ListenerContainerBlockEntity.tick(lvl, p, st, (ListenerContainerBlockEntity) be) : null;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof ListenerContainerBlockEntity containerBE) {
            ItemStack item = player.getItemInHand(hand);
            boolean hasBottle = state.getValue(BOTTLE);
            int currentDrops = containerBE.getDroplets();

            if (item.is(Items.GLASS_BOTTLE)) {
                if (hasBottle && currentDrops == 64) {
                    item.consume(1, player);
                    player.addItem(new ItemStack(ModItems.MUSIC_BOTTLE.get(), 1));
                    containerBE.setDroplets(0);
                    return ItemInteractionResult.SUCCESS;
                } else if (!hasBottle) {
                    item.consume(1, player);
                    level.setBlock(pos, state.setValue(BOTTLE, true), 3);
                    return ItemInteractionResult.SUCCESS;
                }
            } else if (item.isEmpty() || item.is(ModItems.MUSIC_BOTTLE.get())) {
                if (hasBottle && currentDrops == 64) {
                    player.addItem(new ItemStack(ModItems.MUSIC_BOTTLE.get(), 1));
                    containerBE.setDroplets(0);
                    level.setBlock(pos, state.setValue(BOTTLE, false), 3);
                    return ItemInteractionResult.SUCCESS;
                }
            }
        }

        return ItemInteractionResult.FAIL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ListenerContainerBlockEntity(pos, state);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LISTENING, BOTTLE);
    }
}
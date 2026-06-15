package net.migueel26.faunaandorchestra.block.custom;

import com.mojang.serialization.MapCodec;
import net.migueel26.faunaandorchestra.block.ModBlocks;
import net.migueel26.faunaandorchestra.block.entity.ListenerBlockEntity;
import net.migueel26.faunaandorchestra.block.entity.ListenerContainerBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ListenerBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final MapCodec<ListenerBlock> CODEC = simpleCodec(ListenerBlock::new);
    public static final BooleanProperty LISTENING = BooleanProperty.create("listening");
    public static final VoxelShape BASE = Block.box(1, 0, 1, 15,4.5, 15);
    public ListenerBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(LISTENING, false));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return BASE;
    }

    @Override
    public @Nullable PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        BlockState listenerContainerState = level.getBlockState(pos.below());

        if (!oldState.is(ModBlocks.LISTENER)
                && listenerContainerState.is(ModBlocks.LISTENER_CONTAINER)) {
            if (!level.isClientSide()) {
                ((ServerLevel) level).sendParticles(ParticleTypes.POOF, pos.getCenter().x, pos.getY() + 0.5, pos.getCenter().z, 25, 0.1, 0.5, 0.1, 0.2);
                level.playSound(null, pos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 1.0F, 2.0F);
            }
            if (level.getBlockEntity(pos.below()) instanceof ListenerContainerBlockEntity listenerContainerBE
            && listenerContainerBE.isListening() && listenerContainerState.getValue(ListenerContainerBlock.BOTTLE)) {
                level.setBlock(pos, state.setValue(LISTENING, true), 3);
            }
        } else {
            super.onPlace(state, level, pos, oldState, movedByPiston);
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getHorizontalDirection().getOpposite();
        return this.defaultBlockState().setValue(FACING, direction);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("block.faunaandorchestra.listener.desc")
                    .withStyle(ChatFormatting.GRAY));
        } else {
            tooltipComponents.add(Component.translatable("tooltip.faunaandorchestra.shift"));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
            return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ListenerBlockEntity(pos, state);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LISTENING);
    }
}

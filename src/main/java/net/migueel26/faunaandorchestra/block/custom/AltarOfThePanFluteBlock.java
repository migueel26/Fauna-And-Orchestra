package net.migueel26.faunaandorchestra.block.custom;

import com.mojang.serialization.MapCodec;
import net.migueel26.faunaandorchestra.block.ModBlocks;
import net.migueel26.faunaandorchestra.block.entity.AltarOfThePanFluteBlockEntity;
import net.migueel26.faunaandorchestra.component.ModDataComponents;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class AltarOfThePanFluteBlock extends AltarBlock implements EntityBlock {
    public static final BooleanProperty PAN_FLUTE = BooleanProperty.create("pan_flute");
    public AltarOfThePanFluteBlock(Properties properties) {
        super(properties);

        this.registerDefaultState(this.getStateDefinition().any().setValue(PAN_FLUTE, false));
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.getValue(PAN_FLUTE) && !newState.is(ModBlocks.ALTAR_OF_THE_PAN_FLUTE)) {
            popResourceFromFace(level, pos, Direction.UP, new ItemStack(ModItems.PAN_FLUTE.get()));
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof AltarOfThePanFluteBlockEntity altar) {
            if (stack.is(ModItems.PAN_FLUTE) && !state.getValue(PAN_FLUTE)) {
                // Place Pan Flute
                altar.setPowers(stack.get(ModDataComponents.PAN_FLUTE_LIST));
                player.setItemSlot(hand.equals(InteractionHand.MAIN_HAND) ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND, ItemStack.EMPTY);
                level.setBlock(pos, state.setValue(PAN_FLUTE, true), 3);
                return ItemInteractionResult.SUCCESS;

            } else if (stack.isEmpty() && state.getValue(PAN_FLUTE)) {
                // Get Pan Flute
                player.setItemInHand(hand, new ItemStack(ModItems.PAN_FLUTE, 1,
                        DataComponentPatch.builder().set(ModDataComponents.PAN_FLUTE_LIST.get(), altar.getPowers()).build()));
                altar.setPowers(new ArrayList<>());
                level.setBlock(pos, state.setValue(PAN_FLUTE, false), 3);
                return ItemInteractionResult.SUCCESS;

            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PAN_FLUTE);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AltarOfThePanFluteBlockEntity(pos, state);
    }
}

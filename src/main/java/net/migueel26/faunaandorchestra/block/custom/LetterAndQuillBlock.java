package net.migueel26.faunaandorchestra.block.custom;

import com.mojang.serialization.MapCodec;
import net.migueel26.faunaandorchestra.screen.custom.LetterAndQuillMenu;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LetterAndQuillBlock extends HorizontalDirectionalBlock {
    protected static final VoxelShape NORTH_SHAPE = Shapes.or(
            Block.box(4.5, 0, 2, 11.5, 0.5, 13),
            Block.box(0.75, 0, 11.25, 3.5, 2, 14)
    );
    protected static final VoxelShape SOUTH_SHAPE = Shapes.or(
            Block.box(4.5, 0, 3, 11.5, 0.5, 14),
            Block.box(12.5, 0, 2, 15.25, 2, 4.75)
    );
    protected static final VoxelShape EAST_SHAPE = Shapes.or(
            Block.box(3, 0, 4.5, 14, 0.5, 11.5),
            Block.box(2, 0, 0.75, 4.75, 2, 3.5)
    );
    protected static final VoxelShape WEST_SHAPE = Shapes.or(
            Block.box(2, 0, 4.5, 13, 0.5, 11.5),
            Block.box(11.25, 0, 12.5, 14, 2, 15.25)
    );
    private static final Component CONTAINER_TITLE = Component.translatable("container.letter_and_quill");

    public LetterAndQuillBlock(Properties properties) {
        super(properties);

        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction direction = state.getValue(HorizontalDirectionalBlock.FACING);
        return switch (direction) {
            case SOUTH -> SOUTH_SHAPE;
            case EAST -> EAST_SHAPE;
            case WEST -> WEST_SHAPE;
            default -> NORTH_SHAPE;
        };
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getHorizontalDirection().getOpposite();
        return this.defaultBlockState().setValue(FACING, direction);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            player.openMenu(state.getMenuProvider(level, pos));
            return InteractionResult.CONSUME;
        }
    }

    @Nullable
    public MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        return new SimpleMenuProvider((id, inventory, player) -> new LetterAndQuillMenu(id, inventory, ContainerLevelAccess.create(level, pos)), CONTAINER_TITLE);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        return !state.canSurvive(level, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return !state.getCollisionShape(level, pos).getFaceShape(Direction.UP).isEmpty() || state.isFaceSturdy(level, pos, Direction.UP);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos blockpos = pos.below();
        return this.mayPlaceOn(level.getBlockState(blockpos), level, blockpos);
    }

    @Override
    public void appendHoverText(ItemStack stack, BlockGetter blockGetter, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("block.faunaandorchestra.letter_and_quill.tooltip"));
        } else {
            tooltipComponents.add(Component.translatable("tooltip.faunaandorchestra.shift"));
        }
        super.appendHoverText(stack, blockGetter, tooltipComponents, tooltipFlag);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}

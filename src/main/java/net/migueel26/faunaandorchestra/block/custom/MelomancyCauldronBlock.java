package net.migueel26.faunaandorchestra.block.custom;

import com.mojang.serialization.MapCodec;
import net.migueel26.faunaandorchestra.block.entity.MelomancyCauldronBlockEntity;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class MelomancyCauldronBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final MapCodec<MelomancyCauldronBlock> CODEC = simpleCodec(MelomancyCauldronBlock::new);
    public static final IntegerProperty LIQUID = IntegerProperty.create("liquid", 0, 3);
    public VoxelShape FLOOR = Block.box(2.5, 4.0, 2.525, 13.5, 5.0, 13.25);
    public VoxelShape EAST_SIDE = Block.box(2.5, 4.0, 2.525, 3.5, 11.75, 13.25);
    public VoxelShape WEST_SIDE = Block.box(12.5, 4.0, 2.525, 13.5, 11.75, 13.25);
    public VoxelShape NORTH_SIDE = Block.box(2.5, 4.0, 2.525, 12.5, 11.75, 3.525);
    public VoxelShape NORTH_TOP_SIDE = Block.box(1.65, 11.75, 1.65, 14.35, 14.45, 2.55);
    public VoxelShape SOUTH_TOP_SIDE = Block.box(1.65, 11.75, 13.25, 14.35, 14.45, 14.25);
    public VoxelShape EAST_TOP_SIDE = Block.box(1.65, 11.75, 1.65, 2.65, 14.45, 14.25);
    public VoxelShape WEST_TOP_SIDE = Block.box(13.35, 11.75, 1.65, 14.35, 14.45, 14.25);

    public VoxelShape SOUTH_SIDE = Block.box(2.5, 4.0, 12.25, 13.5, 11.75, 13.25);
    public VoxelShape SHAPE = Shapes.or(FLOOR, EAST_SIDE, WEST_SIDE, NORTH_SIDE, SOUTH_SIDE,
            EAST_TOP_SIDE, WEST_TOP_SIDE, NORTH_TOP_SIDE, SOUTH_TOP_SIDE);
    public MelomancyCauldronBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(LIQUID, 0));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        int liquid = state.getValue(LIQUID);
        if (stack.is(ModItems.MUSIC_BOTTLE) && state.getValue(LIQUID) < 3) {
            stack.consume(1, player);
            level.setBlock(pos, state.setValue(LIQUID, liquid+1), 3);
            return ItemInteractionResult.SUCCESS;
        } else {
            return ItemInteractionResult.FAIL;
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
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
            return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MelomancyCauldronBlockEntity(pos, state);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIQUID);
    }
}

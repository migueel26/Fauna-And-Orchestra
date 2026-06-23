package net.migueel26.faunaandorchestra.block.custom;

import net.migueel26.faunaandorchestra.block.ModBlockEntities;
import net.migueel26.faunaandorchestra.block.entity.FloraEnhancerBlockEntity;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.util.BlocksUtil;
import net.migueel26.faunaandorchestra.util.ModTags;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FloraEnhancerBlock extends HorizontalDirectionalBlock implements EntityBlock, ListeningBlock {
    public static final Integer MAX_MOISTURE = 3;
    public static final Integer DEFAULT_WET_TIME = 600;

    protected static VoxelShape SHAPE = Shapes.or(
        Block.box(1.75, 0, 1.75, 14.25, 2, 14.25),
        Block.box(7,2, 7.5, 9, 5, 8.5)
    );
    public FloraEnhancerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof FloraEnhancerBlockEntity blockEntity) {
            if (!level.isClientSide()) {
                int moisture = blockEntity.getMoisture();
                int wetTime = blockEntity.getWetTime();

                if (moisture == MAX_MOISTURE && wetTime == 0) {
                    player.addItem(new ItemStack(ModItems.FLORA_FORTA.get()));
                    blockEntity.setMoisture(0);
                }

                if (blockEntity.getWetTime() == 0) {
                    if (blockEntity.getSheetMusic().isEmpty()) {
                        ItemStack sheet = getNewSheetMusic(level);
                        blockEntity.setSheetMusic(sheet);
                    }

                    player.displayClientMessage(getSheetMusicMessage(blockEntity.getSheetMusic()), true);
                    tryToStartListening((ServerLevel) level, pos, blockEntity);
                } else {
                    player.displayClientMessage(Component.translatable("block.faunaandorchestra.flora_enhancer.wet"), true);
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    public static @NotNull ItemStack getNewSheetMusic(Level level) {
        ItemStack sheet;
        do {
            sheet = BlocksUtil.getRandomItemFromTag(ModTags.Items.SHEET_MUSIC, level);
        } while (sheet.is(ModItems.RESURRECTION_SONG.get()));
        return sheet;
    }

    private Component getSheetMusicMessage(ItemStack sheetMusic) {
        String name = sheetMusic.getHoverName().getString();
        return Component.translatable("block.faunaandorchestra.flora_enhancer.sheet_music").append(name.substring(0, name.length() - 6) + "?");
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (level.getBlockEntity(pos) instanceof FloraEnhancerBlockEntity be) {
            if (!level.isClientSide && be.getMoisture() == MAX_MOISTURE) {
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(ModItems.FLORA_FORTA.get()));
            }
        }
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState potState = level.getBlockState(pos.below());
        return potState.is(BlockTags.FLOWER_POTS) && !potState.is(Blocks.FLOWER_POT);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
        if (!state.canSurvive(level, currentPos)) {
            if (level.getBlockEntity(currentPos) instanceof FloraEnhancerBlockEntity be) {
                if (be.getMoisture() == MAX_MOISTURE) {
                    if (!level.isClientSide()) {
                        Containers.dropItemStack((Level) level, currentPos.getX(), currentPos.getY(), currentPos.getZ(), new ItemStack(ModItems.FLORA_FORTA.get()));
                    }
                }
            }
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, direction, neighborState, level, currentPos, neighborPos);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter blockGetter, List<Component> tooltipComponents, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("block.faunaandorchestra.flora_enhancer.desc"));
        } else {
            tooltipComponents.add(Component.translatable("tooltip.faunaandorchestra.shift"));
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return type == ModBlockEntities.FLORA_ENHANCER.get() ?
                (BlockEntityTicker<T>) (lvl, pos, st, be) -> FloraEnhancerBlockEntity.tick(lvl, pos, st, (FloraEnhancerBlockEntity) be) : null;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new FloraEnhancerBlockEntity(blockPos, blockState);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}

package net.migueel26.faunaandorchestra.block.custom;

import com.mojang.serialization.MapCodec;
import net.migueel26.faunaandorchestra.block.entity.FloraEnhancerBlockEntity;
import net.migueel26.faunaandorchestra.entity.custom.ConductorEntity;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.util.BlocksUtil;
import net.migueel26.faunaandorchestra.util.ModTags;
import net.migueel26.faunaandorchestra.util.MusicUtil;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FloraEnhancerBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final MapCodec<FloraEnhancerBlock> CODEC = simpleCodec(FloraEnhancerBlock::new);
    public static final IntegerProperty MOISTURE = BlockStateProperties.MOISTURE;
    public static final IntegerProperty WET_TIME = IntegerProperty.create("wet_time", 0, 600);
    public static final Integer MAX_MOSITURE = 3;
    public static final Integer DEFAULT_WET_TIME = 600;
    protected static VoxelShape SHAPE = Shapes.or(
        Block.box(1.75, 0, 1.75, 14.25, 2, 14.25),
        Block.box(7,2, 7.5, 9, 5, 8.5)
    );
    public FloraEnhancerBlock(Properties properties) {
        super(properties);

        this.registerDefaultState(this.stateDefinition.any().setValue(MOISTURE, 0).setValue(WET_TIME, 0));
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        int wetTime = state.getValue(WET_TIME);
        int moisture = state.getValue(MOISTURE);
        if (!level.isClientSide()) {
            if (moisture == MAX_MOSITURE && wetTime == 0) {
                player.addItem(new ItemStack(ModItems.FLORA_FORTA.get()));
                level.setBlockAndUpdate(pos, state.setValue(MOISTURE, 0));
            }
            if (wetTime == 0 && level.getBlockEntity(pos) instanceof FloraEnhancerBlockEntity blockEntity) {
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
        return ItemInteractionResult.SUCCESS;
    }

    public static @NotNull ItemStack getNewSheetMusic(Level level) {
        ItemStack sheet;
        do {
            sheet = BlocksUtil.getRandomItemFromTag(ModTags.Items.SHEET_MUSIC, level);
        } while (sheet.is(ModItems.RESURRECTION_SONG));
        return sheet;
    }

    private Component getSheetMusicMessage(ItemStack sheetMusic) {
        String name = sheetMusic.getHoverName().getString();
        return Component.translatable("block.faunaandorchestra.flora_enhancer.sheet_music").append(name.substring(0, name.length() - 6) + "?");
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (blockEntity instanceof FloraEnhancerBlockEntity) {
            if (!level.isClientSide && state.getValue(MOISTURE).equals(MAX_MOSITURE)) {
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(ModItems.FLORA_FORTA.get()));
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int wetTime = state.getValue(WET_TIME);
        int moisture = state.getValue(MOISTURE);

        if (wetTime > 0) {
            level.setBlockAndUpdate(pos, state.setValue(WET_TIME, wetTime - 1));
            level.scheduleTick(pos, this, 20);
        } else if (moisture < MAX_MOSITURE) {
            level.setBlockAndUpdate(pos, state.setValue(MOISTURE, moisture + 1));

            if (level.getBlockEntity(pos) instanceof FloraEnhancerBlockEntity blockEntity) {
                blockEntity.setSheetMusic(getNewSheetMusic(level));

                tryToStartListening(level, pos, blockEntity);
            }
        }
    }

    private static void tryToStartListening(ServerLevel level, BlockPos pos, FloraEnhancerBlockEntity blockEntity) {
        ConductorEntity conductor = MusicUtil.lookForConductor(level, AABB.ofSize(pos.getCenter(), 0.5f, 0.5f, 0.5f));

        if (conductor != null) {
            blockEntity.onStartListening(conductor);
        }
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState potState = level.getBlockState(pos.below());
        return potState.is(BlockTags.FLOWER_POTS) && !potState.is(Blocks.FLOWER_POT);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
        if (!state.canSurvive(level, currentPos)) {
            if (state.getValue(MOISTURE) == 3) {
                if (!level.isClientSide()) {
                    Containers.dropItemStack((Level) level, currentPos.getX(), currentPos.getY(), currentPos.getZ(), new ItemStack(ModItems.FLORA_FORTA.get()));
                }
            }
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, direction, neighborState, level, currentPos, neighborPos);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("block.faunaandorchestra.flora_enhancer.desc"));
        } else {
            tooltipComponents.add(Component.translatable("tooltip.faunaandorchestra.shift"));
        }
    }

    @Override
    protected MapCodec<? extends FloraEnhancerBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new FloraEnhancerBlockEntity(blockPos, blockState);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(MOISTURE, FACING, WET_TIME);
    }
}

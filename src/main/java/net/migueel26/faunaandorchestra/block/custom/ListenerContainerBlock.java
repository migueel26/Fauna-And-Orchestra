package net.migueel26.faunaandorchestra.block.custom;

import net.migueel26.faunaandorchestra.block.ModBlocks;
import net.migueel26.faunaandorchestra.block.entity.ListenerContainerBlockEntity;
import net.migueel26.faunaandorchestra.entity.custom.ConductorEntity;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.particles.ModParticleTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class ListenerContainerBlock extends Block implements EntityBlock {
    public static final BooleanProperty LISTENING = BooleanProperty.create("listening");
    public static final BooleanProperty BOTTLE = BooleanProperty.create("bottle");
    public static final IntegerProperty DROPLETS = IntegerProperty.create("droplets", 0, 64);
    public static final VoxelShape BOTTOM = Block.box(0, 0, 0, 16, 2, 16);
    public static final VoxelShape TOP = Block.box(0, 14, 0, 16, 16, 16);
    public static final VoxelShape CORNER_SW = Block.box(14, 2, 0, 14, 14, 2);
    public static final VoxelShape CORNER_SE = Block.box(0, 2, 0, 2, 14, 2);
    public static final VoxelShape CORNER_NW = Block.box(14, 2, 14, 16, 14, 16);
    public static final VoxelShape CORNER_NE = Block.box(0, 2, 14, 2, 14, 16);
    public static final VoxelShape SHAPE = Shapes.or(BOTTOM, TOP, CORNER_SW, CORNER_NE, CORNER_SE, CORNER_NW);
    public static final int NEXT_TICK_SCHEDULED = 20;
    int seconds;


    public ListenerContainerBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(LISTENING, false)
                .setValue(BOTTLE, false)
                .setValue(DROPLETS, 0));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public void appendHoverText(ItemStack stack, BlockGetter blockGetter, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("block.faunaandorchestra.listener_container.desc")
                    .withStyle(ChatFormatting.GRAY));
        } else {
            tooltipComponents.add(Component.translatable("tooltip.faunaandorchestra.shift"));
        }
        super.appendHoverText(stack, blockGetter, tooltipComponents, tooltipFlag);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (!oldState.is(ModBlocks.LISTENER_CONTAINER.get()) && level.getBlockState(pos.above()).is(ModBlocks.LISTENER.get())) {
            if (!level.isClientSide()) {
                ((ServerLevel) level).sendParticles(ParticleTypes.POOF, pos.getCenter().x, pos.getY() + 0.5, pos.getCenter().z, 25, 0.1, 0.5, 0.1, 0.2);
                level.playSound(null, pos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 1.0F, 2.0F);
            }
        }

        super.onPlace(state, level, pos, oldState, movedByPiston);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!newState.is(ModBlocks.LISTENER_CONTAINER.get()) && state.getValue(BOTTLE)) {
            if (state.getValue(DROPLETS) == 64) {
                popResourceFromFace(level, pos, Direction.UP, new ItemStack(ModItems.MUSIC_BOTTLE.get()));
            } else {
                popResourceFromFace(level, pos, Direction.UP, new ItemStack(Items.GLASS_BOTTLE));
            }

            if (level.getBlockState(pos.above()).is(ModBlocks.LISTENER.get())) {
                level.setBlock(pos.above(), level.getBlockState(pos.above()).setValue(ListenerBlock.LISTENING, false), 3);
            }
    }

        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public @Nullable PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // Already assembled
        int drops = state.getValue(DROPLETS);

        // We check if there's an orchestra nearby
        Optional<ConductorEntity> conductor = level.getEntitiesOfClass(ConductorEntity.class, AABB.ofSize(pos.getCenter(), 10, 10, 10))
                .stream().filter(ConductorEntity::isConducting)
                .filter(ConductorEntity::isOrchestraFull)
                .findAny();

        if (conductor.isPresent()) {
            // If there is an orchestra nearby, it's listening
            updateListeningAssembledListener(level, pos, state, true);
            if (drops < 64 && state.getValue(BOTTLE)) level.sendParticles(ModParticleTypes.DRIPPING_MUSIC.get(), pos.getCenter().x, pos.getY() + 0.75, pos.getCenter().z, 3, 0, 0, 0, 0.1);
        } else {
            // If there isn't, it is not listening
            updateListeningAssembledListener(level, pos, state, false);
        }


        if (seconds % 5 == 0 && state.getValue(LISTENING) && drops < 64 && state.getValue(BOTTLE)) {
            // If it's listening, add a droplet every 5 seconds
            level.setBlock(pos, state.setValue(DROPLETS, drops + 1), 3);
        }

        seconds++;
        checkIfStillAssembled(level, pos, state);

        super.tick(state, level, pos, random);
    }

    private static void updateListeningAssembledListener(ServerLevel level, BlockPos pos, BlockState state, boolean listening) {
        if (level.getBlockState(pos.above()).is(ModBlocks.LISTENER.get())) {
            level.setBlock(pos.above(), level.getBlockState(pos.above()).setValue(ListenerBlock.LISTENING, listening), 3);
        }
        level.setBlock(pos, state.setValue(LISTENING, listening), 3);
    }

    private void checkIfStillAssembled(ServerLevel level, BlockPos pos, BlockState state) {
        if (level.getBlockState(pos.above()).is(ModBlocks.LISTENER.get()) && state.getValue(BOTTLE)) {
            level.scheduleTick(pos, this, NEXT_TICK_SCHEDULED);
        } else {
            // If it's not, 0 seconds and not schedule tick
            seconds = 0;
            updateListeningAssembledListener(level, pos, state, false);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack item = player.getItemInHand(hand);
        if (item.is(Items.GLASS_BOTTLE)) {
            if (isFull(state)) {
                if (!player.getAbilities().instabuild) {
                    item.shrink(1);
                }
                player.addItem(new ItemStack(ModItems.MUSIC_BOTTLE.get(), 1));
                level.setBlock(pos, state.setValue(DROPLETS, 0), 3);
                return InteractionResult.SUCCESS;
            } else if (isContainerEmpty(state)) {
                item.shrink(1);
                level.setBlock(pos, state.setValue(BOTTLE, true), 3);
                level.scheduleTick(pos, this, NEXT_TICK_SCHEDULED);
                return InteractionResult.SUCCESS;
            }
        } else if (item.isEmpty() || item.is(ModItems.MUSIC_BOTTLE.get())) {
            if (isFull(state)) {
                player.addItem(new ItemStack(ModItems.MUSIC_BOTTLE.get(), 1));
                level.setBlock(pos, state.setValue(DROPLETS, 0).setValue(BOTTLE, false), 3);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.FAIL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ListenerContainerBlockEntity(pos, state);
    }

    private boolean isFull(BlockState state) {
        return state.getValue(DROPLETS) == 64 && state.getValue(BOTTLE);
    }

    private boolean isContainerEmpty(BlockState state) {
        return !state.getValue(BOTTLE);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LISTENING, BOTTLE, DROPLETS);
    }
}
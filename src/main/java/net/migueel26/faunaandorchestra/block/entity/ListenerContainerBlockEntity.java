package net.migueel26.faunaandorchestra.block.entity;

import net.migueel26.faunaandorchestra.block.ModBlockEntities;
import net.migueel26.faunaandorchestra.block.custom.ListenerBlock;
import net.migueel26.faunaandorchestra.block.custom.ListenerContainerBlock;
import net.migueel26.faunaandorchestra.entity.custom.ListeningBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;
import net.migueel26.faunaandorchestra.block.ModBlocks;
import net.migueel26.faunaandorchestra.entity.custom.ConductorEntity;
import net.migueel26.faunaandorchestra.particles.ModParticleTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class ListenerContainerBlockEntity extends BlockEntity implements GeoBlockEntity, ListeningBlockEntity {
    private final static RawAnimation LISTEN = RawAnimation.begin().thenPlay("listen");
    private final static RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    private final AnimationController<ListenerContainerBlockEntity> controller = new AnimationController<>(this, "listener_controller", 5, this::animController);
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    private int tickCount = 0;
    private int droplets = 0;
    private boolean isListeningToOrchestra = false;

    public ListenerContainerBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.LISTENER_CONTAINER_BE.get(), pos, blockState);
    }

    protected <E extends ListenerContainerBlockEntity> PlayState animController(final AnimationState<E> state) {
        if (getBlockState().getValue(ListenerContainerBlock.LISTENING)) {
            state.getController().setAnimation(LISTEN);
        } else {
            state.getController().setAnimation(IDLE);
        }
        return PlayState.CONTINUE;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ListenerContainerBlockEntity entity) {
        if (level.isClientSide()) return;

        boolean isBottle = state.getValue(ListenerContainerBlock.BOTTLE);
        boolean isAssembled = state.getValue(ListenerContainerBlock.LISTENING) && level.getBlockState(pos.above()).getOptionalValue(ListenerBlock.LISTENING).orElse(false);
        int drops = entity.getDroplets();
        boolean hasListenerAbove = level.getBlockState(pos.above()).is(ModBlocks.LISTENER);

        if (!isBottle || !hasListenerAbove) {
            if (isAssembled) updateListeningAssembledListener(level, pos, state, false);
            entity.tickCount = 0;
            return;
        }

        if (entity.isListening()) {
            if (!isAssembled) updateListeningAssembledListener(level, pos, state, true);

            if (drops < 64) {
                if (entity.tickCount % 20 == 0) {
                    ((ServerLevel) level).sendParticles(ModParticleTypes.DRIPPING_MUSIC.get(), pos.getCenter().x, pos.getY() + 0.75, pos.getCenter().z, 3, 0, 0, 0, 0.1);
                }

                entity.tickCount++;
                if (entity.tickCount >= 100) {
                    entity.setDroplets(drops + 1);
                    entity.tickCount = 0;
                }
            }
        } else {
            if (isAssembled) updateListeningAssembledListener(level, pos, state, false);
            entity.tickCount = 0;
        }
    }

    private static void updateListeningAssembledListener(Level level, BlockPos pos, BlockState state, boolean listening) {
        if (level.getBlockState(pos.above()).is(ModBlocks.LISTENER)) {
            level.setBlock(pos.above(), level.getBlockState(pos.above()).setValue(ListenerBlock.LISTENING, listening), 3);
        }
        level.setBlock(pos, state.setValue(ListenerContainerBlock.LISTENING, listening), 3);
    }

    public int getDroplets() {
        return this.droplets;
    }

    public void setDroplets(int droplets) {
        this.droplets = droplets;
        this.markUpdated();
    }

    private void markUpdated() {
        this.setChanged();
        this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("TickCount", this.tickCount);
        tag.putInt("Droplets", this.droplets);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.tickCount = tag.getInt("TickCount");
        this.droplets = tag.getInt("Droplets");
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(controller);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    @Override
    public void onStartListening(ConductorEntity conductor) {
        this.isListeningToOrchestra = true;
    }

    @Override
    public void onStopListening() {
        this.isListeningToOrchestra = false;
    }

    @Override
    public boolean isListening() {
        return isListeningToOrchestra;
    }
}

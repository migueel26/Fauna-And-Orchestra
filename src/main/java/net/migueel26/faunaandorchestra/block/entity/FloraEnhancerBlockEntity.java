package net.migueel26.faunaandorchestra.block.entity;

import net.migueel26.faunaandorchestra.block.ModBlockEntities;
import net.migueel26.faunaandorchestra.block.custom.FloraEnhancerBlock;
import net.migueel26.faunaandorchestra.entity.custom.ConductorEntity;
import net.migueel26.faunaandorchestra.entity.custom.ListeningBlockEntity;
import net.migueel26.faunaandorchestra.util.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;

public class FloraEnhancerBlockEntity extends BlockEntity implements GeoBlockEntity, ListeningBlockEntity {
    private final AnimationController<FloraEnhancerBlockEntity> controller = new AnimationController<>(this, "flora_enhancer_controller", 0, this::animController);
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    private int moisture = 0;
    private int wetTime = 0;
    private int tickCount = 0;
    public final ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        protected int getStackLimit(int slot, ItemStack stack) {
            return 1;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return stack.is(ModTags.Items.SHEET_MUSIC);
        }
    };

    public FloraEnhancerBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.FLORA_ENHANCER.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, FloraEnhancerBlockEntity entity) {
        if (level.isClientSide()) return;

        if (entity.wetTime > 0) {
            if (entity.tickCount > 0) {
                entity.tickCount--;
            } else {
                entity.wetTime--;
                entity.tickCount = 20;

                // Cuando el tiempo de mojado termina, aumentamos el crecimiento automáticamente
                if (entity.wetTime == 0 && entity.moisture < FloraEnhancerBlock.MAX_MOISTURE) {
                    entity.moisture++;
                    entity.setSheetMusic(FloraEnhancerBlock.getNewSheetMusic(level));
                    entity.tryToStartListening((ServerLevel) level, pos);
                }
                entity.markUpdated();
            }
        }
    }

    public int getMoisture() { return this.moisture; }
    public void setMoisture(int moisture) {
        this.moisture = moisture;
        markUpdated();
    }

    public int getWetTime() { return this.wetTime; }
    public void setWetTime(int wetTime) {
        this.wetTime = wetTime;
        markUpdated();
    }

    public ItemStack getSheetMusic() {
        return inventory.getStackInSlot(0);
    }

    public void setSheetMusic(ItemStack stack) {
        inventory.setStackInSlot(0, stack);
    }

    public void clearContents() {
        inventory.setStackInSlot(0, ItemStack.EMPTY);
    }

    public void markUpdated() {
        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Inventory", inventory.serializeNBT(registries));
        tag.putInt("Moisture", this.moisture);
        tag.putInt("WetTime", this.wetTime);
        tag.putInt("TickDelay", this.tickCount);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        this.moisture = tag.getInt("Moisture");
        this.wetTime = tag.getInt("WetTime");
        this.tickCount = tag.getInt("TickDelay");
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        return saveWithoutMetadata(pRegistries);
    }


    protected <E extends FloraEnhancerBlockEntity> PlayState animController(final AnimationState<E> state) {
        return PlayState.STOP;
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
        if (getSheetMusic().is(Items.AIR)) {
            setSheetMusic(FloraEnhancerBlock.getNewSheetMusic(level));
        }

        if (getSheetMusic().is(conductor.getSheetMusic()) &&
                this.moisture < FloraEnhancerBlock.MAX_MOISTURE &&
                this.wetTime == 0) {
            // Water
            this.wetTime = FloraEnhancerBlock.DEFAULT_WET_TIME;
            this.tickCount = 20;
            markUpdated();
        }
    }

    @Override
    public void onStopListening() {

    }

    @Override
    public boolean isListening() {
        return false;
    }
}

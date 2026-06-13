package net.migueel26.faunaandorchestra.block.entity;

import net.migueel26.faunaandorchestra.block.ModBlockEntities;
import net.migueel26.faunaandorchestra.block.custom.DiscordNucleiBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;

public class DiscordNucleiBlockEntity extends BlockEntity implements GeoBlockEntity {
    private final static RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    private final static RawAnimation UNSTABLE = RawAnimation.begin().thenPlay("unstable");
    private final static RawAnimation VERY_UNSTABLE = RawAnimation.begin().thenPlay("very_unstable");

    private int essence = 0;
    private int instability = 0;
    private int actionTimer = -1;

    private final AnimationController<DiscordNucleiBlockEntity> controller = new AnimationController<>(this, "discord_nuclei_controller", 5, this::animController);
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    public final ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        protected int getStackLimit(int slot, ItemStack stack) {
            return 1;
        }

        @Override
        protected void onContentsChanged(int slot) {
            markUpdated();
        }
    };
    private float rotation;

    public DiscordNucleiBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.DISCORD_NUCLEI_BE.get(), pos, blockState);
    }

    protected <E extends DiscordNucleiBlockEntity> PlayState animController(final AnimationState<E> state) {
        if (instability < 20) state.getController().setAnimation(IDLE);
        else if (instability < 60) state.getController().setAnimation(UNSTABLE);
        else state.getController().setAnimation(VERY_UNSTABLE);
        return PlayState.CONTINUE;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, DiscordNucleiBlockEntity entity) {
        if (level.isClientSide()) return;

        if (entity.instability > 0 && entity.essence > 0) {
            if (entity.actionTimer > 0) {
                entity.actionTimer--;
            } else if (entity.actionTimer == 0) {
                entity.instability++;
                if (entity.instability >= 100) {
                    DiscordNucleiBlock.instabilityExplosion(level, pos);
                } else {
                    entity.actionTimer = DiscordNucleiBlock.getNextUnstableTick(entity.instability - 1, entity.instability);
                    entity.markUpdated();
                }
            }
        }
    }

    public int getEssence() {
        return essence;
    }
    public int getInstability() {
        return instability;
    }

    public void setEssence(int essence) {
        this.essence = essence;
        markUpdated();
    }

    public void setInstability(int instability) {
        this.instability = Math.max(0, instability);
        markUpdated();
    }

    public void setActionTimer(int time) {
        this.actionTimer = time;
        this.setChanged();
    }

    private void markUpdated() {
        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    public float getRenderingRotation() {
        rotation += 0.5f;
        if(rotation >= 360) {
            rotation = 0;
        }
        return rotation;
    }

    public void clearContents() {
        inventory.setStackInSlot(0, ItemStack.EMPTY);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Inventory", inventory.serializeNBT(registries));
        tag.putInt("Essence", this.essence);
        tag.putInt("Instability", this.instability);
        tag.putInt("ActionTimer", this.actionTimer);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        this.essence = tag.getInt("Essence");
        this.instability = tag.getInt("Instability");
        this.actionTimer = tag.getInt("ActionTimer");
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

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(controller);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
}

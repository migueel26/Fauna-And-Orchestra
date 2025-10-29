package net.migueel26.faunaandorchestra.block.entity;

import net.migueel26.faunaandorchestra.block.ModBlockEntities;
import net.migueel26.faunaandorchestra.networking.SyncTipCaseOwnerPayloadS2C;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;

public class TipCaseBlockEntity extends BlockEntity implements GeoBlockEntity {
    UUID owner;
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    public TipCaseBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.TIP_CASE_BE.get(), pos, blockState);
        this.owner = null;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        if (tag.hasUUID("Owner")) {
            this.owner = tag.getUUID("Owner");
        }
        super.loadAdditional(tag, registries);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        if (owner != null) tag.putUUID("Owner", owner);
        super.saveAdditional(tag, registries);
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(@Nullable UUID owner) {
        this.owner = owner;
        PacketDistributor.sendToAllPlayers(new SyncTipCaseOwnerPayloadS2C(
                worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(),
                owner));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
}

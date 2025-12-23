package net.migueel26.faunaandorchestra.block.entity;

import net.migueel26.faunaandorchestra.block.ModBlockEntities;
import net.migueel26.faunaandorchestra.networking.ModNetwork;
import net.migueel26.faunaandorchestra.networking.packets.SyncTipCaseOwnerS2CPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
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
    public void load(CompoundTag tag) {
        if (tag.hasUUID("Owner")) {
            this.owner = tag.getUUID("Owner");
        }
        super.load(tag);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        if (owner != null) tag.putUUID("Owner", owner);
        super.saveAdditional(tag);
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(@Nullable UUID owner) {
        this.owner = owner;
        ModNetwork.sendToClients(new SyncTipCaseOwnerS2CPacket(
                worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(),
                owner));
        markUpdated();
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putUUID("Owner", owner);
        return compoundTag;
    }

    private void markUpdated() {
        this.setChanged();
        this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
}

package net.migueel26.faunaandorchestra.block.entity;

import net.migueel26.faunaandorchestra.networking.ModNetwork;
import net.migueel26.faunaandorchestra.networking.packets.SyncOwnableBEPacketS2C;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public abstract class OwnableBlockEntity extends BlockEntity {
    protected UUID owner;
    public OwnableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
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
    protected void saveAdditional(CompoundTag tag) {
        if (owner != null) tag.putUUID("Owner", owner);
        super.saveAdditional(tag);
    }

    public void setOwner(@Nullable UUID owner) {
        this.owner = owner;
        ModNetwork.sendToClients(new SyncOwnableBEPacketS2C(
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
        if (owner != null) compoundTag.putUUID("Owner", owner);
        return compoundTag;
    }

    protected void markUpdated() {
        this.setChanged();
        this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }

    public UUID getOwner() {
        return owner;
    }
}

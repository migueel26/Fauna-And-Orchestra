package net.migueel26.faunaandorchestra.networking.packets;

import net.migueel26.faunaandorchestra.networking.ClientPacketHandler;
import net.migueel26.faunaandorchestra.networking.ServerPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class SyncTipCaseOwnerS2CPacket {
    private final int x;
    private final int y;
    private final int z;
    private final UUID owner;

    public SyncTipCaseOwnerS2CPacket(int x, int y, int z, UUID owner) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.owner = owner;
    }

    public SyncTipCaseOwnerS2CPacket(FriendlyByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.owner = buf.readUUID();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
        buf.writeUUID(this.owner);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public UUID getOwner() {
        return owner;
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();

        context.enqueueWork(() -> {
            ClientPacketHandler.handleSyncTipCaseOnNetwork(
                    getOwner(),
                    getX(), getY(), getZ()
            );
        });

        return true;
    }
}

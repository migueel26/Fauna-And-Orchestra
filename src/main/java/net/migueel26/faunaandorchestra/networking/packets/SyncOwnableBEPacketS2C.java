package net.migueel26.faunaandorchestra.networking.packets;

import net.migueel26.faunaandorchestra.networking.ClientPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public record SyncOwnableBEPacketS2C(int x, int y, int z, UUID owner) {

    public SyncOwnableBEPacketS2C(FriendlyByteBuf buf) {
        this(buf.readInt(), buf.readInt(), buf.readInt(), buf.readUUID());
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
        buf.writeUUID(this.owner);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();

        context.enqueueWork(() -> {
            ClientPacketHandler.handleSyncOwnableBEOnNetwork(
                    owner,
                    x, y, z
            );
        });

        return true;
    }
}

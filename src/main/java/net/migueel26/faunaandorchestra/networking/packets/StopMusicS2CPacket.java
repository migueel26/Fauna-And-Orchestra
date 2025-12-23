package net.migueel26.faunaandorchestra.networking.packets;

import net.migueel26.faunaandorchestra.networking.ClientPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class StopMusicS2CPacket {
    private final UUID entityUUID;

    public StopMusicS2CPacket(UUID entityUUID) {
        this.entityUUID = entityUUID;
    }

    public StopMusicS2CPacket(FriendlyByteBuf buf) {
        this.entityUUID = buf.readUUID();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUUID(entityUUID);
    }

    public UUID getEntityUUID() {
        return entityUUID;
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();

        context.enqueueWork(() -> {
            ClientPacketHandler.handleStopMusicOnNetwork(
                    getEntityUUID()
            );
        });

        return true;
    }
}

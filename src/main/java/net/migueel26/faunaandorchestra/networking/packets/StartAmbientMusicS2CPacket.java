package net.migueel26.faunaandorchestra.networking.packets;

import net.migueel26.faunaandorchestra.networking.ClientPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public record StartAmbientMusicS2CPacket(UUID conductorUUID) {

    public StartAmbientMusicS2CPacket(FriendlyByteBuf buf) {
        this(buf.readUUID());
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUUID(conductorUUID);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();

        context.enqueueWork(() -> {
            ClientPacketHandler.handleStartAmbientMusicOnNetwork(conductorUUID());
        });

        return true;
    }
}

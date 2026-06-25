package net.migueel26.faunaandorchestra.networking.packets;

import net.migueel26.faunaandorchestra.networking.ServerPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record WriteEmailC2SPacket(String sender, String receiver, int x, int y, int z) {
    public WriteEmailC2SPacket(FriendlyByteBuf buf) {
        this(buf.readUtf(), buf.readUtf(), buf.readInt(), buf.readInt(), buf.readInt());
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(this.sender);
        buf.writeUtf(this.receiver);
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();

        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                ServerPacketHandler.handleWriteEmailOnNetwork(player, sender, receiver, x, y, z);
            }
        });

        return true;
    }
}

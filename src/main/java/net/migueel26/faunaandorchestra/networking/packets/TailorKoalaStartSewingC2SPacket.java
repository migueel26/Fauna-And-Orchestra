package net.migueel26.faunaandorchestra.networking.packets;

import net.migueel26.faunaandorchestra.networking.ClientPacketHandler;
import net.migueel26.faunaandorchestra.networking.ServerPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public record TailorKoalaStartSewingC2SPacket(UUID tailorUUID, boolean sewing, ItemStack catalogChoice) {
    public TailorKoalaStartSewingC2SPacket(FriendlyByteBuf buf) {
        this(buf.readUUID(), buf.readBoolean(), buf.readItem());
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUUID(tailorUUID);
        buf.writeBoolean(sewing);
        buf.writeItem(catalogChoice);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();

        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                ServerPacketHandler.handleTailorKoalaStartSewingOnNetwork(player, tailorUUID, sewing, catalogChoice);
            }
        });

        return true;
    }
}

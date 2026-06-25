package net.migueel26.faunaandorchestra.networking.packets;

import net.migueel26.faunaandorchestra.networking.ServerPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

public record EraseMailC2SPacket(ItemStack stack) {
    public EraseMailC2SPacket(FriendlyByteBuf buf) {
        this(buf.readItem());
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeItem(this.stack);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();

        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                ServerPacketHandler.handleEraseEmailOnNetwork(player, stack);
            }
        });

        return true;
    }
}

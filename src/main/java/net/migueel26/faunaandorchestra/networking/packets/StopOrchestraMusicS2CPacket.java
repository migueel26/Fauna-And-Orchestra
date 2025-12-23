package net.migueel26.faunaandorchestra.networking.packets;

import net.migueel26.faunaandorchestra.networking.ClientPacketHandler;
import net.migueel26.faunaandorchestra.util.BufferUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class StopOrchestraMusicS2CPacket {
    private final List<UUID> orchestra;
    public StopOrchestraMusicS2CPacket(List<UUID> orchestra) {
        this.orchestra = orchestra;
    }

    public StopOrchestraMusicS2CPacket(FriendlyByteBuf buf) {
        this.orchestra = BufferUtil.readUUIDList(buf);
    }

    public void toBytes(FriendlyByteBuf buf) {
        BufferUtil.writeUUIDList(buf, orchestra);
    }

    public List<UUID> getOrchestra() {
        return orchestra;
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();

        context.enqueueWork(() -> {
            ClientPacketHandler.handleStopOrchestraOnNetwork(
                    getOrchestra());
        });

        return true;
    }
}

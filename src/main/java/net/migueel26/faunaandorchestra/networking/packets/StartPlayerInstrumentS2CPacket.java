package net.migueel26.faunaandorchestra.networking.packets;

import net.migueel26.faunaandorchestra.networking.ClientPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class StartPlayerInstrumentS2CPacket {
    private final UUID playerID;
    private final ResourceLocation soundPath;

    public StartPlayerInstrumentS2CPacket(UUID playerID, ResourceLocation soundPath) {
        this.playerID = playerID;
        this.soundPath = soundPath;
    }

    public StartPlayerInstrumentS2CPacket(FriendlyByteBuf buf) {
        this.playerID = buf.readUUID();
        this.soundPath = buf.readResourceLocation();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUUID(this.playerID);
        buf.writeResourceLocation(this.soundPath);
    }

    public UUID getPlayerID() {
        return playerID;
    }

    public ResourceLocation getSoundPath() {
        return soundPath;
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();

        context.enqueueWork(() -> {
            ClientPacketHandler.handleStartPlayerInstrumentMusic(
                    getPlayerID(),
                    getSoundPath()
            );
        });

        return true;
    }
}

package net.migueel26.faunaandorchestra.networking.packets;

import net.migueel26.faunaandorchestra.networking.ClientPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class StartOrchestraMusicS2CPacket {
    private final UUID entityUUID;
    private final ResourceLocation soundPath;
    private final int ticksOffset;
    public StartOrchestraMusicS2CPacket(UUID entityID, ResourceLocation soundPath, int ticksOffset) {
        this.entityUUID = entityID;
        this.soundPath = soundPath;
        this.ticksOffset = ticksOffset;
    }

    public StartOrchestraMusicS2CPacket(FriendlyByteBuf buf) {
        this.entityUUID = buf.readUUID();
        this.soundPath = buf.readResourceLocation();
        this.ticksOffset = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUUID(entityUUID);
        buf.writeResourceLocation(soundPath);
        buf.writeInt(ticksOffset);
    }

    public UUID getEntityUUID() {
        return entityUUID;
    }

    public ResourceLocation getSoundPath() {
        return soundPath;
    }

    public int getTicksOffset() {
        return ticksOffset;
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();

        context.enqueueWork(() -> {
            ClientPacketHandler.handleStartOrchestraOnNetwork(
                    getEntityUUID(),
                    getSoundPath(),
                    getTicksOffset()
            );
        });

        return true;
    }
}

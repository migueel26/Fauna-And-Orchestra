package net.migueel26.faunaandorchestra.networking.packets;

import net.migueel26.faunaandorchestra.entity.custom.ConductorEntity;
import net.migueel26.faunaandorchestra.networking.ModNetwork;
import net.migueel26.faunaandorchestra.networking.ServerPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class RestartOrchestraMusicC2SPacket {
    private final UUID conductorUUID;
    private final float volume;

    public RestartOrchestraMusicC2SPacket(UUID conductorUUID, float volume) {
        this.conductorUUID = conductorUUID;
        this.volume = volume;
    }

    public RestartOrchestraMusicC2SPacket(FriendlyByteBuf buf) {
        this.conductorUUID = buf.readUUID();
        this.volume = buf.readFloat();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUUID(this.conductorUUID);
        buf.writeFloat(this.volume);
    }

    public UUID getConductorUUID() { return conductorUUID; }
    public float getVolume() { return volume; }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();

        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                ServerPacketHandler.handleRestartOrchestraOnNetwork(player, getConductorUUID(), getVolume());
            }
        });

        return true;
    }
}

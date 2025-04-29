package net.migueel26.faunaandorchestra.networking;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public record RestartOrchestraMusicC2SPayload(UUID conductorUUID, float volume) implements CustomPacketPayload {
    private RestartOrchestraMusicC2SPayload(FriendlyByteBuf buf) {
        this(buf.readUUID(), buf.readFloat());
    }

    public static final CustomPacketPayload.Type<RestartOrchestraMusicC2SPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "restart_orchestra_music_payloadc2s"));

    public static final StreamCodec<FriendlyByteBuf, RestartOrchestraMusicC2SPayload> STREAM_CODEC = CustomPacketPayload.codec(
            RestartOrchestraMusicC2SPayload::write, RestartOrchestraMusicC2SPayload::new
    );

    private void write(FriendlyByteBuf buf) {
        buf.writeUUID(conductorUUID);
        buf.writeFloat(volume);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

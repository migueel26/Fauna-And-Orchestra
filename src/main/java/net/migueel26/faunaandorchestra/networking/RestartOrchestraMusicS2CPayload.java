package net.migueel26.faunaandorchestra.networking;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.util.BufferUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.UUID;

public record RestartOrchestraMusicS2CPayload(UUID conductor, List<UUID> orchestra, int tickOffset, float volume) implements CustomPacketPayload {
    private RestartOrchestraMusicS2CPayload(FriendlyByteBuf buf) {
        this(buf.readUUID(), BufferUtil.readUUIDList(buf), buf.readInt(), buf.readFloat());
    }

    public static final CustomPacketPayload.Type<RestartOrchestraMusicS2CPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "restart_orchestra_music_payloads2c"));

    public static final StreamCodec<FriendlyByteBuf, RestartOrchestraMusicS2CPayload> STREAM_CODEC = CustomPacketPayload.codec(
            RestartOrchestraMusicS2CPayload::write, RestartOrchestraMusicS2CPayload::new
    );

    private void write(FriendlyByteBuf buf) {
        buf.writeUUID(conductor);
        BufferUtil.writeUUIDList(buf, orchestra);
        buf.writeInt(tickOffset);
        buf.writeFloat(volume);
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

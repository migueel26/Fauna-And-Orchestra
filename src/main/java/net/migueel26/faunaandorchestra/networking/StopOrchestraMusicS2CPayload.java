package net.migueel26.faunaandorchestra.networking;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.util.BufferUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.UUID;

public record StopOrchestraMusicS2CPayload(List<UUID> orchestra) implements CustomPacketPayload {
    private StopOrchestraMusicS2CPayload(FriendlyByteBuf buf) {
        this(BufferUtil.readUUIDList(buf));
    }

    public static final CustomPacketPayload.Type<StopOrchestraMusicS2CPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "stop_orchestra_music_payload"));

    public static final StreamCodec<FriendlyByteBuf, StopOrchestraMusicS2CPayload> STREAM_CODEC = CustomPacketPayload.codec(
            StopOrchestraMusicS2CPayload::write, StopOrchestraMusicS2CPayload::new
    );

    private void write(FriendlyByteBuf buf) {
        BufferUtil.writeUUIDList(buf, orchestra);
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}


package net.migueel26.faunaandorchestra.networking;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public record StopMusicS2CPayload(UUID entityUUID) implements CustomPacketPayload {
    private StopMusicS2CPayload(FriendlyByteBuf buf) {
        this(buf.readUUID());
    }

    public static final CustomPacketPayload.Type<StopMusicS2CPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "stop_music_payload"));

    public static final StreamCodec<FriendlyByteBuf, StopMusicS2CPayload> STREAM_CODEC = CustomPacketPayload.codec(
            StopMusicS2CPayload::write, StopMusicS2CPayload::new
    );

    private void write(FriendlyByteBuf buf) {
        buf.writeUUID(entityUUID);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

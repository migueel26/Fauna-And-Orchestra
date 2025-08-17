package net.migueel26.faunaandorchestra.networking;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public record StartAmbientMusicS2CPayload(UUID conductorUUID) implements CustomPacketPayload{
    private StartAmbientMusicS2CPayload(FriendlyByteBuf buf) {
        this(buf.readUUID());
    }

    public static final CustomPacketPayload.Type<StartAmbientMusicS2CPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "start_frog_choir_music_payload"));

    public static final StreamCodec<FriendlyByteBuf, StartAmbientMusicS2CPayload> STREAM_CODEC = CustomPacketPayload.codec(
            StartAmbientMusicS2CPayload::write, StartAmbientMusicS2CPayload::new
    );

    private void write(FriendlyByteBuf buf) {
        buf.writeUUID(conductorUUID);
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

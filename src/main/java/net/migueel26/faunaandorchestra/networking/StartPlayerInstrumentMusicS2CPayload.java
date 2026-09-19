package net.migueel26.faunaandorchestra.networking;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public record StartPlayerInstrumentMusicS2CPayload(UUID playerID, ResourceLocation soundPath) implements CustomPacketPayload {
    private StartPlayerInstrumentMusicS2CPayload(FriendlyByteBuf buf) {
        this(buf.readUUID(), buf.readResourceLocation());
    }

    public static final Type<StartPlayerInstrumentMusicS2CPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "start_player_instrument_music_payload"));

    public static final StreamCodec<FriendlyByteBuf, StartPlayerInstrumentMusicS2CPayload> STREAM_CODEC = CustomPacketPayload.codec(
            StartPlayerInstrumentMusicS2CPayload::write, StartPlayerInstrumentMusicS2CPayload::new
    );

    private void write(FriendlyByteBuf buf) {
        buf.writeUUID(playerID);
        buf.writeResourceLocation(soundPath);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

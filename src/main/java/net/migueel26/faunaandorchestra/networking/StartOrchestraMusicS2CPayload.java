package net.migueel26.faunaandorchestra.networking;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public record StartOrchestraMusicS2CPayload(UUID entityID, ResourceLocation soundPath, int tickOffset) implements CustomPacketPayload {
    private StartOrchestraMusicS2CPayload(FriendlyByteBuf buf) {
        this(buf.readUUID(), buf.readResourceLocation(), buf.readInt());
    }

    public static final CustomPacketPayload.Type<StartOrchestraMusicS2CPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "start_orchestra_music_payload"));

    public static final StreamCodec<FriendlyByteBuf, StartOrchestraMusicS2CPayload> STREAM_CODEC = CustomPacketPayload.codec(
            StartOrchestraMusicS2CPayload::write, StartOrchestraMusicS2CPayload::new
    );

    private void write(FriendlyByteBuf buf) {
        buf.writeUUID(entityID);
        buf.writeResourceLocation(soundPath);
        buf.writeInt(tickOffset);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

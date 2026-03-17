package net.migueel26.faunaandorchestra.networking;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public record TailorKoalaStartSewingC2SPayload(UUID tailorUUID, boolean sewing) implements CustomPacketPayload {
    private TailorKoalaStartSewingC2SPayload(FriendlyByteBuf buf) {
        this(buf.readUUID(), buf.readBoolean());
    }

    public static final CustomPacketPayload.Type<TailorKoalaStartSewingC2SPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "tailor_koala_start_sewing_payloadc2s"));

    public static final StreamCodec<FriendlyByteBuf, TailorKoalaStartSewingC2SPayload> STREAM_CODEC = CustomPacketPayload.codec(
            TailorKoalaStartSewingC2SPayload::write, TailorKoalaStartSewingC2SPayload::new
    );

    private void write(FriendlyByteBuf buf) {
        buf.writeUUID(tailorUUID);
        buf.writeBoolean(sewing);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

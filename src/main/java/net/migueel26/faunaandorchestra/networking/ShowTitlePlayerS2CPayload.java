package net.migueel26.faunaandorchestra.networking;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ShowTitlePlayerS2CPayload(String title, String subtitle) implements CustomPacketPayload {
    private ShowTitlePlayerS2CPayload(FriendlyByteBuf buf) {
        this(buf.readUtf(), buf.readUtf());
    }

    public static final CustomPacketPayload.Type<ShowTitlePlayerS2CPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "show_title_payload"));

    public static final StreamCodec<FriendlyByteBuf, ShowTitlePlayerS2CPayload> STREAM_CODEC = CustomPacketPayload.codec(
            ShowTitlePlayerS2CPayload::write, ShowTitlePlayerS2CPayload::new
    );

    private void write(FriendlyByteBuf buf) {
        buf.writeUtf(title);
        buf.writeUtf(subtitle);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

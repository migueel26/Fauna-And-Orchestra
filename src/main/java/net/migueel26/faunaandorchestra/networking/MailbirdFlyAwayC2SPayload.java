package net.migueel26.faunaandorchestra.networking;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record MailbirdFlyAwayC2SPayload(BlockPos pos) implements CustomPacketPayload {
    private MailbirdFlyAwayC2SPayload(RegistryFriendlyByteBuf buf) {
        this(BlockPos.STREAM_CODEC.decode(buf));
    }

    public static final Type<MailbirdFlyAwayC2SPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "mailbird_fly_away_payloadc2s"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MailbirdFlyAwayC2SPayload> STREAM_CODEC = CustomPacketPayload.codec(
            MailbirdFlyAwayC2SPayload::write, MailbirdFlyAwayC2SPayload::new
    );

    private void write(RegistryFriendlyByteBuf buf) {
        BlockPos.STREAM_CODEC.encode(buf, pos);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

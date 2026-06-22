package net.migueel26.faunaandorchestra.networking;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record EraseMailC2SPayload(ItemStack stack) implements CustomPacketPayload {

    private EraseMailC2SPayload(RegistryFriendlyByteBuf buf) {
        this(ItemStack.OPTIONAL_STREAM_CODEC.decode(buf));
    }

    public static final Type<EraseMailC2SPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "erase_mail_payloadc2s"));

    public static final StreamCodec<RegistryFriendlyByteBuf, EraseMailC2SPayload> STREAM_CODEC = CustomPacketPayload.codec(
            EraseMailC2SPayload::write, EraseMailC2SPayload::new
    );

    private void write(RegistryFriendlyByteBuf buf) {
        ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, stack);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
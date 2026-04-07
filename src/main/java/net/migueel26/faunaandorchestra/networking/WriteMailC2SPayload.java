package net.migueel26.faunaandorchestra.networking;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public record WriteMailC2SPayload(String sender, String receiver, int x, int y, int z) implements CustomPacketPayload {

    private WriteMailC2SPayload(RegistryFriendlyByteBuf buf) {
        this(buf.readUtf(), buf.readUtf(), buf.readInt(), buf.readInt(), buf.readInt());
    }

    public static final Type<WriteMailC2SPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "write_mail_payloadc2s"));

    public static final StreamCodec<RegistryFriendlyByteBuf, WriteMailC2SPayload> STREAM_CODEC = CustomPacketPayload.codec(
            WriteMailC2SPayload::write, WriteMailC2SPayload::new
    );

    private void write(RegistryFriendlyByteBuf buf) {
        buf.writeUtf(sender);
        buf.writeUtf(receiver);
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
package net.migueel26.faunaandorchestra.networking;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public record TailorKoalaStartSewingC2SPayload(UUID tailorUUID, boolean sewing, ItemStack catalogChoice) implements CustomPacketPayload {

    private TailorKoalaStartSewingC2SPayload(RegistryFriendlyByteBuf buf) {
        this(buf.readUUID(), buf.readBoolean(), ItemStack.OPTIONAL_STREAM_CODEC.decode(buf));
    }

    public static final CustomPacketPayload.Type<TailorKoalaStartSewingC2SPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "tailor_koala_start_sewing_payloadc2s"));

    public static final StreamCodec<RegistryFriendlyByteBuf, TailorKoalaStartSewingC2SPayload> STREAM_CODEC = CustomPacketPayload.codec(
            TailorKoalaStartSewingC2SPayload::write, TailorKoalaStartSewingC2SPayload::new
    );

    private void write(RegistryFriendlyByteBuf buf) {
        buf.writeUUID(tailorUUID);
        buf.writeBoolean(sewing);
        ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, catalogChoice);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
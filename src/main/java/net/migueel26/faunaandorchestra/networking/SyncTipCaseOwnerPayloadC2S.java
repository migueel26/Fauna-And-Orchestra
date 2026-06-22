package net.migueel26.faunaandorchestra.networking;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public record SyncTipCaseOwnerPayloadC2S(int x, int y, int z, UUID owner) implements CustomPacketPayload {
    private SyncTipCaseOwnerPayloadC2S(FriendlyByteBuf buf) {
        this(buf.readInt(), buf.readInt(), buf.readInt(), buf.readUUID());
    }

    public static final Type<SyncTipCaseOwnerPayloadC2S> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "sync_tip_case_owner_payloadc2s"));

    public static final StreamCodec<FriendlyByteBuf, SyncTipCaseOwnerPayloadC2S> STREAM_CODEC = CustomPacketPayload.codec(
            SyncTipCaseOwnerPayloadC2S::write, SyncTipCaseOwnerPayloadC2S::new
    );

    private void write(FriendlyByteBuf buf) {
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
        buf.writeUUID(this.owner);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

package net.migueel26.faunaandorchestra.networking;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public record SyncTipCaseOwnerPayload(int x, int y, int z, UUID owner) implements CustomPacketPayload {
    private SyncTipCaseOwnerPayload(FriendlyByteBuf buf) {
        this(buf.readInt(), buf.readInt(), buf.readInt(), buf.readUUID());
    }

    public static final CustomPacketPayload.Type<SyncTipCaseOwnerPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "sync_tip_case_owner_payload"));

    public static final StreamCodec<FriendlyByteBuf, SyncTipCaseOwnerPayload> STREAM_CODEC = CustomPacketPayload.codec(
            SyncTipCaseOwnerPayload::write, SyncTipCaseOwnerPayload::new
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

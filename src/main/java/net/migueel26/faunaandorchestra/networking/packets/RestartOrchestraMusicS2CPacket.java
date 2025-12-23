package net.migueel26.faunaandorchestra.networking.packets;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.custom.ConductorEntity;
import net.migueel26.faunaandorchestra.entity.custom.MusicalEntity;
import net.migueel26.faunaandorchestra.mixins.client.accessors.ClientLevelAccessor;
import net.migueel26.faunaandorchestra.mixins.interfaces.ISoundManagerMixin;
import net.migueel26.faunaandorchestra.networking.ClientPacketHandler;
import net.migueel26.faunaandorchestra.sound.custom.InstrumentSoundInstance;
import net.migueel26.faunaandorchestra.util.MusicUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class RestartOrchestraMusicS2CPacket {
    private final UUID conductor;
    private final List<UUID> orchestra;
    private final int tickOffset;
    private final float volume;
    private final String sheetName;

    public RestartOrchestraMusicS2CPacket(UUID conductor, List<UUID> orchestra, int tickOffset, float volume, String sheetName) {
        this.conductor = conductor;
        this.orchestra = orchestra;
        this.tickOffset = tickOffset;
        this.volume = volume;
        this.sheetName = sheetName;
    }

    public RestartOrchestraMusicS2CPacket(FriendlyByteBuf buf) {
        this.conductor = buf.readUUID();
        this.orchestra = buf.readList(FriendlyByteBuf::readUUID);
        this.tickOffset = buf.readInt();
        this.volume = buf.readFloat();
        this.sheetName = buf.readUtf();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUUID(this.conductor);
        buf.writeCollection(this.orchestra, FriendlyByteBuf::writeUUID);
        buf.writeInt(this.tickOffset);
        buf.writeFloat(this.volume);
        buf.writeUtf(this.sheetName);
    }

    public UUID getConductor() {
        return conductor;
    }

    public List<UUID> getOrchestra() {
        return orchestra;
    }

    public int getTickOffset() {
        return tickOffset;
    }

    public float getVolume() {
        return volume;
    }

    public String getSheetName() {
        return sheetName;
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();

        context.enqueueWork(() -> {
            ClientPacketHandler.handleRestartOrchestraOnNetwork(
                    getOrchestra(),
                    getConductor(),
                    getVolume(),
                    getTickOffset(),
                    getSheetName()
            );
        });

        return true;
    }
}

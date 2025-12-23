package net.migueel26.faunaandorchestra.networking.packets;

import net.migueel26.faunaandorchestra.networking.ClientPacketHandler;
import net.migueel26.faunaandorchestra.networking.ShowTitlePlayerS2CPayload;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ShowTitlePlayerS2CPacket {
    private final String title;
    private final String subtitle;

    public ShowTitlePlayerS2CPacket(String title, String subtitle) {
        this.title = title;
        this.subtitle = subtitle;
    }

    public ShowTitlePlayerS2CPacket(FriendlyByteBuf buf) {
        this.title = buf.readUtf();
        this.subtitle = buf.readUtf();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(title);
        buf.writeUtf(subtitle);
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();

        context.enqueueWork(() -> {
            ClientPacketHandler.handleShowTitleOnNetwork(
                    getTitle(),
                    getSubtitle()
            );
        });

        return true;
    }
}

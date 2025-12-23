package net.migueel26.faunaandorchestra.networking;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.networking.packets.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetwork {
    private static SimpleChannel INSTANCE;

    private static int packetId = 0;
    private static int id() {
        return packetId++;
    }

    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        net.messageBuilder(RestartOrchestraMusicC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(RestartOrchestraMusicC2SPacket::new)
                .encoder(RestartOrchestraMusicC2SPacket::toBytes)
                .consumerMainThread(RestartOrchestraMusicC2SPacket::handle)
                .add();

        net.messageBuilder(SyncTipCaseOwnerC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(SyncTipCaseOwnerC2SPacket::new)
                .encoder(SyncTipCaseOwnerC2SPacket::toBytes)
                .consumerMainThread(SyncTipCaseOwnerC2SPacket::handle)
                .add();

        net.messageBuilder(RestartOrchestraMusicS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(RestartOrchestraMusicS2CPacket::new)
                .encoder(RestartOrchestraMusicS2CPacket::toBytes)
                .consumerMainThread(RestartOrchestraMusicS2CPacket::handle)
                .add();

        net.messageBuilder(ShowTitlePlayerS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(ShowTitlePlayerS2CPacket::new)
                .encoder(ShowTitlePlayerS2CPacket::toBytes)
                .consumerMainThread(ShowTitlePlayerS2CPacket::handle)
                .add();

        net.messageBuilder(StartAmbientMusicS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(StartAmbientMusicS2CPacket::new)
                .encoder(StartAmbientMusicS2CPacket::toBytes)
                .consumerMainThread(StartAmbientMusicS2CPacket::handle)
                .add();

        net.messageBuilder(StartOrchestraMusicS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(StartOrchestraMusicS2CPacket::new)
                .encoder(StartOrchestraMusicS2CPacket::toBytes)
                .consumerMainThread(StartOrchestraMusicS2CPacket::handle)
                .add();

        net.messageBuilder(StopMusicS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(StopMusicS2CPacket::new)
                .encoder(StopMusicS2CPacket::toBytes)
                .consumerMainThread(StopMusicS2CPacket::handle)
                .add();

        net.messageBuilder(StopOrchestraMusicS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(StopOrchestraMusicS2CPacket::new)
                .encoder(StopOrchestraMusicS2CPacket::toBytes)
                .consumerMainThread(StopOrchestraMusicS2CPacket::handle)
                .add();

        net.messageBuilder(SyncTipCaseOwnerS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SyncTipCaseOwnerS2CPacket::new)
                .encoder(SyncTipCaseOwnerS2CPacket::toBytes)
                .consumerMainThread(SyncTipCaseOwnerS2CPacket::handle)
                .add();
    }
    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static <MSG> void sendToClients(MSG message) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), message);
    }

}

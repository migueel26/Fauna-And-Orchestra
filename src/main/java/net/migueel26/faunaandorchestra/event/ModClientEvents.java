package net.migueel26.faunaandorchestra.event;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.networking.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = FaunaAndOrchestra.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = net.neoforged.api.distmarker.Dist.CLIENT)
public class ModClientEvents {
    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        /*registrar.playToClient(
                StartOrchestraMusicS2CPayload.TYPE,
                StartOrchestraMusicS2CPayload.STREAM_CODEC,
                (payload, context) -> ClientPayloadHandler.handleStartOrchestraOnNetwork(payload, context)
        );
        registrar.playToClient(
                RestartOrchestraMusicS2CPayload.TYPE,
                RestartOrchestraMusicS2CPayload.STREAM_CODEC,
                (payload, context) -> ClientPayloadHandler.handleRestartOrchestraOnNetwork(payload, context)
        );
        registrar.playToClient(
                StopMusicS2CPayload.TYPE,
                StopMusicS2CPayload.STREAM_CODEC,
                (payload, context) -> ClientPayloadHandler.handleStopMusicOnNetwork(payload, context)
        );
        registrar.playToClient(
                StartAmbientMusicS2CPayload.TYPE,
                StartAmbientMusicS2CPayload.STREAM_CODEC,
                (payload, context) -> ClientPayloadHandler.handleStartAmbientMusicOnNetwork(payload, context)
        );
        registrar.playToClient(
                StopOrchestraMusicS2CPayload.TYPE,
                StopOrchestraMusicS2CPayload.STREAM_CODEC,
                (payload, context) -> ClientPayloadHandler.handleStopOrchestraOnNetwork(payload, context)
        );
        registrar.playToClient(
                ShowTitlePlayerS2CPayload.TYPE,
                ShowTitlePlayerS2CPayload.STREAM_CODEC,
                (payload, context) -> ClientPayloadHandler.handleShowTitleOnNetwork(payload, context)
        );
        registrar.playToClient(
                SyncTipCaseOwnerPayloadS2C.TYPE,
                SyncTipCaseOwnerPayloadS2C.STREAM_CODEC,
                (payload, context) -> ClientPayloadHandler.handleSyncTipCaseOnNetwork(payload, context)
        );

         */
    }
}

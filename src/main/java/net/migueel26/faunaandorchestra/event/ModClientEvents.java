package net.migueel26.faunaandorchestra.event;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.component.ModDataComponents;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.networking.*;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = FaunaAndOrchestra.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = net.neoforged.api.distmarker.Dist.CLIENT)
public class ModClientEvents {
    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> {
            if (tintIndex == 0) {
                long time = Util.getMillis();
                float speed = 6000.0f;
                float hue = (time % (long) speed) / speed;
                int rgbColor = Mth.hsvToRgb(hue, 1.0f, 1.0f);
                // alpha | rgb
                return 0xFF000000 | rgbColor;
            }
            return 0xFFFFFFFF;
        }, ModItems.EVERFRUIT.get()); // Reemplaza con tu ítem
    }

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

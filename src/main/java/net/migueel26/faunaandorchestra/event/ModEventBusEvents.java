package net.migueel26.faunaandorchestra.event;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.ModEntities;
import net.migueel26.faunaandorchestra.entity.custom.*;
import net.migueel26.faunaandorchestra.networking.ClientPayloadHandler;
import net.migueel26.faunaandorchestra.networking.ServerPayloadHandler;
import net.migueel26.faunaandorchestra.networking.StartOrchestraMusicPayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = FaunaAndOrchestra.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.MANTIS.get(), MantisEntity.createAttributes().build());
        event.put(ModEntities.TOAD.get(), ToadEntity.createAttributes().build());
        event.put(ModEntities.PENGUIN.get(), PenguinEntity.createAttributes().build());
        event.put(ModEntities.RED_PANDA.get(), RedPandaEntity.createAttributes().build());
        event.put(ModEntities.MACAW.get(), MacawEntity.createAttributes().build());
    }

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playBidirectional(
                StartOrchestraMusicPayload.TYPE,
                StartOrchestraMusicPayload.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        ClientPayloadHandler::handleStartOrchestraOnNetwork,
                        ServerPayloadHandler::handleDataOnNetwork
                )
        );
    }
}

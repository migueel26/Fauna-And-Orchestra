package net.migueel26.faunaandorchestra.event;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.ModEntities;
import net.migueel26.faunaandorchestra.entity.custom.*;
import net.migueel26.faunaandorchestra.networking.*;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = FaunaAndOrchestra.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.MANTIS.get(), MantisEntity.createAttributes().build());
        event.put(ModEntities.QUIRKY_FROG.get(), QuirkyFrogEntity.createAttributes().build());
        event.put(ModEntities.PENGUIN.get(), PenguinEntity.createAttributes().build());
        event.put(ModEntities.RED_PANDA.get(), RedPandaEntity.createAttributes().build());
        event.put(ModEntities.MACAW.get(), MacawEntity.createAttributes().build());
    }

    @SubscribeEvent
    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(ModEntities.MANTIS.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                MantisEntity::checkMantisSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntities.PENGUIN.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Animal::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntities.RED_PANDA.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Animal::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntities.MACAW.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING,
                MacawEntity::checkMacawSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntities.QUIRKY_FROG.get(), SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                QuirkyFrogEntity::checkFrogSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(
                StartOrchestraMusicS2CPayload.TYPE,
                StartOrchestraMusicS2CPayload.STREAM_CODEC,
                ClientPayloadHandler::handleStartOrchestraOnNetwork
        );
        registrar.playToServer(
                RestartOrchestraMusicC2SPayload.TYPE,
                RestartOrchestraMusicC2SPayload.STREAM_CODEC,
                ServerPayloadHandler::handleRestartOrchestraOnNetwork
        );
        registrar.playToClient(
                RestartOrchestraMusicS2CPayload.TYPE,
                RestartOrchestraMusicS2CPayload.STREAM_CODEC,
                ClientPayloadHandler::handleRestartOrchestraOnNetwork
        );
        registrar.playToClient(
                StopMusicS2CPayload.TYPE,
                StopMusicS2CPayload.STREAM_CODEC,
                ClientPayloadHandler::handleStopMusicOnNetwork
        );
        registrar.playToClient(
                StartFrogChoirMusicS2CPayload.TYPE,
                StartFrogChoirMusicS2CPayload.STREAM_CODEC,
                ClientPayloadHandler::handleStartFrogChoirOnNetwork
        );
        registrar.playToClient(
                StopOrchestraMusicS2CPayload.TYPE,
                StopOrchestraMusicS2CPayload.STREAM_CODEC,
                ClientPayloadHandler::handleStopOrchestraOnNetwork
        );
    }
}

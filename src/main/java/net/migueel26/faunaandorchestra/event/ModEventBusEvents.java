package net.migueel26.faunaandorchestra.event;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.ModEntities;
import net.migueel26.faunaandorchestra.entity.custom.*;
import net.migueel26.faunaandorchestra.entity.custom.boss.ComposerCanonEntity;
import net.migueel26.faunaandorchestra.entity.custom.boss.TheGreatComposer;
import net.migueel26.faunaandorchestra.item.ModCreativeModeTabs;
import net.migueel26.faunaandorchestra.networking.*;
import net.migueel26.faunaandorchestra.potion.ModPotions;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = FaunaAndOrchestra.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.MANTIS.get(), MantisEntity.createAttributes().build());
        event.put(ModEntities.QUIRKY_FROG.get(), QuirkyFrogEntity.createAttributes().build());
        event.put(ModEntities.PENGUIN.get(), PenguinEntity.createAttributes().build());
        event.put(ModEntities.EMPEROR_PENGUIN.get(), EmperorPenguinEntity.createAttributes().build());
        event.put(ModEntities.RED_PANDA.get(), RedPandaEntity.createAttributes().build());
        event.put(ModEntities.MACAW.get(), MacawEntity.createAttributes().build());
        event.put(ModEntities.BEAVER.get(), BeaverEntity.createAttributes().build());
        event.put(ModEntities.LEMUR.get(), LemurEntity.createAttributes().build());
        event.put(ModEntities.MADAME_BUTTERFLY.get(), MadameButterflyEntity.createAttributes().build());
        event.put(ModEntities.WANDERING_KOALA.get(), KoalaEntity.createAttributes().build());
        event.put(ModEntities.FAUST.get(), Faust.createAttributes().build());
        event.put(ModEntities.ORION.get(), Orion.createAttributes().build());
        event.put(ModEntities.ANYA_GHOST.get(), AnyaGhost.createAttributes().build());
        event.put(ModEntities.WISE_TREE.get(), WiseTree.createAttributes().build());
        event.put(ModEntities.SINGING_SPROUTLING.get(), SproutlingEntity.createAttributes().build());
        event.put(ModEntities.BUTTERFLY.get(), ButterflyEntity.createAttributes().build());
        event.put(ModEntities.WANDERING_NOTE.get(), ButterflyEntity.createAttributes().build());
        event.put(ModEntities.THE_GREAT_COMPOSER.get(), TheGreatComposer.createAttributes().build());
        event.put(ModEntities.THE_GREAT_COMPOSER_CANON.get(), ComposerCanonEntity.createAttributes().build());
        event.put(ModEntities.PLAYER_CANON.get(), PlayerCanonEntity.createAttributes().build());
    }

    @SubscribeEvent
    public static void addPotionsToCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab().equals(ModCreativeModeTabs.FAUNA_AND_ORCHESTRA.get())) {
            event.accept(PotionContents.createItemStack(Items.POTION, ModPotions.ABSOLUTE_HEARING_POTION), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
    }

    @SubscribeEvent
    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(ModEntities.MANTIS.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                MantisEntity::checkMantisSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntities.PENGUIN.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Goat::checkGoatSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntities.EMPEROR_PENGUIN.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Goat::checkGoatSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntities.RED_PANDA.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Animal::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntities.MACAW.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING,
                MacawEntity::checkMacawSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntities.QUIRKY_FROG.get(), SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                QuirkyFrogEntity::checkFrogSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntities.BEAVER.get(), SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                BeaverEntity::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntities.LEMUR.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                LemurEntity::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntities.BUTTERFLY.get(), SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                ButterflyEntity::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);

    }

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(
                RestartOrchestraMusicC2SPayload.TYPE,
                RestartOrchestraMusicC2SPayload.STREAM_CODEC,
                (payload, context) -> ServerPayloadHandler.handleRestartOrchestraOnNetwork(payload, context)
        );
        registrar.playToServer(
                SyncTipCaseOwnerPayloadC2S.TYPE,
                SyncTipCaseOwnerPayloadC2S.STREAM_CODEC,
                (payload, context) -> ServerPayloadHandler.handleSyncTipCaseOnNetwork(payload, context)
        );
        registrar.playToClient(
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
    }


}

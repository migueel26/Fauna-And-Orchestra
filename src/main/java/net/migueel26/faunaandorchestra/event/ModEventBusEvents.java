package net.migueel26.faunaandorchestra.event;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.ModEntities;
import net.migueel26.faunaandorchestra.entity.custom.*;
import net.migueel26.faunaandorchestra.entity.custom.boss.ComposerCanonEntity;
import net.migueel26.faunaandorchestra.entity.custom.boss.TheGreatComposer;
import net.migueel26.faunaandorchestra.item.ModCreativeModeTabs;
import net.migueel26.faunaandorchestra.networking.*;
import net.migueel26.faunaandorchestra.potion.ModPotions;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FaunaAndOrchestra.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.MANTIS.get(), MantisEntity.createAttributes().build());
        event.put(ModEntities.QUIRKY_FROG.get(), QuirkyFrogEntity.createAttributes().build());
        event.put(ModEntities.PENGUIN.get(), PenguinEntity.createAttributes().build());
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
        if (event.getTabKey().equals(ModCreativeModeTabs.FAUNA_AND_ORCHESTRA.getKey())) {
            event.accept(PotionUtils.setPotion(Items.POTION.getDefaultInstance(), ModPotions.ABSOLUTE_HEARING_POTION.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
    }

    @SubscribeEvent
    public static void registerSpawnPlacements(SpawnPlacementRegisterEvent event) {
        event.register(ModEntities.MANTIS.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                MantisEntity::checkMantisSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(ModEntities.PENGUIN.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Goat::checkGoatSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(ModEntities.RED_PANDA.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Animal::checkAnimalSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(ModEntities.MACAW.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING,
                MacawEntity::checkMacawSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(ModEntities.QUIRKY_FROG.get(), SpawnPlacements.Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                QuirkyFrogEntity::checkFrogSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(ModEntities.BEAVER.get(), SpawnPlacements.Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                BeaverEntity::checkAnimalSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(ModEntities.LEMUR.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                LemurEntity::checkAnimalSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(ModEntities.BUTTERFLY.get(), SpawnPlacements.Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                ButterflyEntity::checkAnimalSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);

    }
}

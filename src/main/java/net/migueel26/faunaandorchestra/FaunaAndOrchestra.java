package net.migueel26.faunaandorchestra;

import net.migueel26.faunaandorchestra.advancements.ModAdvancements;
import net.migueel26.faunaandorchestra.block.ModBlockEntities;
import net.migueel26.faunaandorchestra.block.ModBlocks;
import net.migueel26.faunaandorchestra.client.block.*;
import net.migueel26.faunaandorchestra.client.entity.*;
import net.migueel26.faunaandorchestra.client.entity.boss.ComposerCanonRenderer;
import net.migueel26.faunaandorchestra.client.entity.boss.TheGreatComposerRenderer;
import net.migueel26.faunaandorchestra.client.entity.projectile.MusicNoteProjectileRenderer;
import net.migueel26.faunaandorchestra.client.entity.projectile.PhantomNoteProjectileRenderer;
import net.migueel26.faunaandorchestra.client.entity.projectile.WanderingNoteRenderer;
import net.migueel26.faunaandorchestra.component.ModDataComponents;
import net.migueel26.faunaandorchestra.effect.ModEffects;
import net.migueel26.faunaandorchestra.entity.ModEntities;
import net.migueel26.faunaandorchestra.item.ModCreativeModeTabs;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.loot_tables.ModLootTables;
import net.migueel26.faunaandorchestra.particles.ModParticleTypes;
import net.migueel26.faunaandorchestra.particles.custom.*;
import net.migueel26.faunaandorchestra.potion.ModPotions;
import net.migueel26.faunaandorchestra.screen.ModMenuTypes;
import net.migueel26.faunaandorchestra.screen.custom.*;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.migueel26.faunaandorchestra.util.ModItemProperties;
import net.migueel26.faunaandorchestra.worldgen.structures.ModStructuredProcessors;
import net.minecraft.client.particle.NoteParticle;
import net.minecraft.client.particle.SculkChargePopParticle;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(FaunaAndOrchestra.MOD_ID)
public class FaunaAndOrchestra {
    public static final String MOD_ID = "faunaandorchestra";
    private static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public FaunaAndOrchestra(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (ExampleMod) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);
        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        ModStructuredProcessors.register(modEventBus);
        ModItems.register(modEventBus);
        ModCreativeModeTabs.register(modEventBus);
        ModEntities.register(modEventBus);
        ModSounds.register(modEventBus);
        ModDataComponents.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModParticleTypes.register(modEventBus);
        ModLootTables.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModAdvancements.register(modEventBus);
        ModEffects.register(modEventBus);
        ModPotions.register(modEventBus);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {

    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {

    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            ModItemProperties.addCustomItemProperties();

            EntityRenderers.register(ModEntities.MANTIS.get(), MantisRenderer::new);
            EntityRenderers.register(ModEntities.QUIRKY_FROG.get(), QuirkyFrogRenderer::new);
            EntityRenderers.register(ModEntities.PENGUIN.get(), PenguinRenderer::new);
            EntityRenderers.register(ModEntities.RED_PANDA.get(), RedPandaRenderer::new);
            EntityRenderers.register(ModEntities.MACAW.get(), MacawRenderer::new);
            EntityRenderers.register(ModEntities.BEAVER.get(), BeaverRenderer::new);
            EntityRenderers.register(ModEntities.LEMUR.get(), LemurRenderer::new);
            EntityRenderers.register(ModEntities.MADAME_BUTTERFLY.get(), MadameButterflyRenderer::new);
            EntityRenderers.register(ModEntities.WANDERING_KOALA.get(), KoalaRenderer::new);
            EntityRenderers.register(ModEntities.FAUST.get(), FaustRenderer::new);
            EntityRenderers.register(ModEntities.ORION.get(), OrionRenderer::new);
            EntityRenderers.register(ModEntities.ANYA_GHOST.get(), AnyaGhostRenderer::new);
            EntityRenderers.register(ModEntities.WISE_TREE.get(), WiseTreeRenderer::new);
            EntityRenderers.register(ModEntities.SINGING_SPROUTLING.get(), SproutlingRenderer::new);
            EntityRenderers.register(ModEntities.BUTTERFLY.get(), ButterflyRenderer::new);
            EntityRenderers.register(ModEntities.WANDERING_NOTE.get(), WanderingNoteRenderer::new);
            EntityRenderers.register(ModEntities.THE_GREAT_COMPOSER.get(), TheGreatComposerRenderer::new);
            EntityRenderers.register(ModEntities.MUSIC_NOTE_PROJECTILE.get(), MusicNoteProjectileRenderer::new);
            EntityRenderers.register(ModEntities.THE_GREAT_COMPOSER_CANON.get(), ComposerCanonRenderer::new);
            EntityRenderers.register(ModEntities.PHANTOM_NOTE_PROJECTILE.get(), PhantomNoteProjectileRenderer::new);
            EntityRenderers.register(ModEntities.THROWN_BOOGIE_BOMB.get(), ThrownItemRenderer::new);
            EntityRenderers.register(ModEntities.THROWN_DISCORD_BOMB.get(), ThrownItemRenderer::new);
        }

        @SubscribeEvent
        public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(ModBlockEntities.COMPOSER_GRAVESTONE_BE.get(), ComposerGravestoneBlockEntityRenderer::new);
            event.registerBlockEntityRenderer(ModBlockEntities.TIP_CASE_BE.get(), TipCaseBlockEntityRenderer::new);
            event.registerBlockEntityRenderer(ModBlockEntities.LISTENER_BE.get(), ListenerBlockEntityRenderer::new);
            event.registerBlockEntityRenderer(ModBlockEntities.LISTENER_CONTAINER_BE.get(), ListenerContainerBlockEntityRenderer::new);
            event.registerBlockEntityRenderer(ModBlockEntities.MELOMANCY_CAULDRON_BE.get(), MelomancyCauldronBlockEntityRenderer::new);
            event.registerBlockEntityRenderer(ModBlockEntities.THE_GREAT_HEAD_BE.get(), TheGreatHeadBlockEntityRenderer::new);
            event.registerBlockEntityRenderer(ModBlockEntities.SINGING_CROP_BE.get(), SingingCropBlockEntityRenderer::new);
            event.registerBlockEntityRenderer(ModBlockEntities.VOICE_CHAMBER_BE.get(), VoiceChamberBlockEntityRenderer::new);
            event.registerBlockEntityRenderer(ModBlockEntities.DISCORD_NUCLEI_BE.get(), DiscordNucleiBlockEntityRenderer::new);
        }

        @SubscribeEvent
        public static void registerScreens(RegisterMenuScreensEvent event) {
            event.register(ModMenuTypes.CONDUCTOR_MENU.get(), ConductorScreen::new);
        }

        @SubscribeEvent
        public static void registerOverlays(final RegisterGuiLayersEvent event) {
            event.registerBelow(VanillaGuiLayers.HOTBAR, ResourceLocation.fromNamespaceAndPath(MOD_ID, "dialogue"), DialogueScreen.OVERLAY);
            event.registerBelow(VanillaGuiLayers.HOTBAR, ResourceLocation.fromNamespaceAndPath(MOD_ID, "dialogue_composer"), TheGreatComposerScreen.OVERLAY);
            event.registerAbove(VanillaGuiLayers.CROSSHAIR, ResourceLocation.fromNamespaceAndPath(MOD_ID, "melomancy_hottip"), MelomancyCauldronScreen.OVERLAY);
            event.registerAbove(VanillaGuiLayers.CROSSHAIR, ResourceLocation.fromNamespaceAndPath(MOD_ID, "discord_nuclei_hottip"), DiscordNucleiScreen.OVERLAY);
            event.registerAbove(VanillaGuiLayers.CROSSHAIR, ResourceLocation.fromNamespaceAndPath(MOD_ID, "composer_gravestone_gui"), ComposerGravestoneScreen.OVERLAY);
        }

        @SubscribeEvent
        public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
            event.registerSpriteSet(ModParticleTypes.FAUNA_NOTES.get(), FaunaNoteParticle.NoteProvider::new);
            event.registerSpriteSet(ModParticleTypes.TREBLE_CLEF.get(), FaunaNoteParticle.TrebleProvider::new);
            event.registerSpriteSet(ModParticleTypes.DRIPPING_MUSIC.get(), DrippingMusicParticle.Provider::new);
            event.registerSpriteSet(ModParticleTypes.MAGICAL_NOTE.get(), MagicalNoteParticle.Provider::new);
            event.registerSpriteSet(ModParticleTypes.CAULDRON_POP.get(), SculkChargePopParticle.Provider::new);
            event.registerSpriteSet(ModParticleTypes.STAR.get(), StarParticle.Provider::new);
            event.registerSpriteSet(ModParticleTypes.VOICE_PARTICLE.get(), VoiceParticle.Provider::new);
            event.registerSpriteSet(ModParticleTypes.SLEEP.get(), SleepParticle.Provider::new);
            event.registerSpriteSet(ModParticleTypes.BASS_CLEF.get(), BassClefParticle.Provider::new);
        }
    }

}

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
import net.migueel26.faunaandorchestra.effect.ModEffects;
import net.migueel26.faunaandorchestra.entity.ModEntities;
import net.migueel26.faunaandorchestra.entity.custom.projectile.ThrownBoogieBomb;
import net.migueel26.faunaandorchestra.entity.custom.projectile.ThrownDiscordBomb;
import net.migueel26.faunaandorchestra.item.ModCreativeModeTabs;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.item.ModPaintings;
import net.migueel26.faunaandorchestra.networking.ModNetwork;
import net.migueel26.faunaandorchestra.particles.ModParticleTypes;
import net.migueel26.faunaandorchestra.particles.custom.*;
import net.migueel26.faunaandorchestra.potion.ModPotions;
import net.migueel26.faunaandorchestra.screen.ModMenuTypes;
import net.migueel26.faunaandorchestra.screen.custom.*;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.migueel26.faunaandorchestra.util.ModItemProperties;
import net.migueel26.faunaandorchestra.worldgen.structures.ModStructures;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.particle.SculkChargePopParticle;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(FaunaAndOrchestra.MOD_ID)
public class FaunaAndOrchestra {
    public static final String MOD_ID = "faunaandorchestra";
    private static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public FaunaAndOrchestra() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();


        modEventBus.addListener(this::commonSetup);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        MinecraftForge.EVENT_BUS.register(this);

        ModStructures.register(modEventBus);
        ModItems.register(modEventBus);
        ModCreativeModeTabs.register(modEventBus);
        ModEntities.register(modEventBus);
        ModSounds.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModParticleTypes.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModEffects.register(modEventBus);
        ModPotions.register(modEventBus);
        ModPaintings.register(modEventBus);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            // REGISTERS
            ModNetwork.register();
            ModAdvancements.register();
            // PROJECTILES
            DispenserBlock.registerBehavior(ModItems.BOOGIE_BOMB.get(), new AbstractProjectileDispenseBehavior() {
                @Override
                protected Projectile getProjectile(Level level, Position position, ItemStack stack) {
                    return new ThrownBoogieBomb(level, position.x(), position.y(), position.z());
                }
            });
            DispenserBlock.registerBehavior(ModItems.DISCORD_BOMB.get(), new AbstractProjectileDispenseBehavior() {
                @Override
                protected Projectile getProjectile(Level level, Position position, ItemStack stack) {
                    return new ThrownDiscordBomb(level, position.x(), position.y(), position.z());
                }
            });
        });
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {

    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            ModItemProperties.addCustomItemProperties();
            MenuScreens.register(ModMenuTypes.CONDUCTOR_MENU.get(), ConductorScreen::new);
            ItemProperties.register(ModItems.BRIEFCASE.get(), ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "opened"),
                    (stack, level, entity, seed) -> {
                        if (stack.hasTag() && stack.getTag().contains("Opened")) {
                            return stack.getTag().getBoolean("Opened") ? 1.0F : 0.0F;
                        }
                        return 0.0F;
            });
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

            event.registerEntityRenderer(ModEntities.MANTIS.get(), MantisRenderer::new);
            event.registerEntityRenderer(ModEntities.QUIRKY_FROG.get(), QuirkyFrogRenderer::new);
            event.registerEntityRenderer(ModEntities.PENGUIN.get(), PenguinRenderer::new);
            event.registerEntityRenderer(ModEntities.RED_PANDA.get(), RedPandaRenderer::new);
            event.registerEntityRenderer(ModEntities.MACAW.get(), MacawRenderer::new);
            event.registerEntityRenderer(ModEntities.BEAVER.get(), BeaverRenderer::new);
            event.registerEntityRenderer(ModEntities.LEMUR.get(), LemurRenderer::new);
            event.registerEntityRenderer(ModEntities.MADAME_BUTTERFLY.get(), MadameButterflyRenderer::new);
            event.registerEntityRenderer(ModEntities.WANDERING_KOALA.get(), KoalaRenderer::new);
            event.registerEntityRenderer(ModEntities.FAUST.get(), FaustRenderer::new);
            event.registerEntityRenderer(ModEntities.ORION.get(), OrionRenderer::new);
            event.registerEntityRenderer(ModEntities.ANYA_GHOST.get(), CanonEntityRenderer::new);
            event.registerEntityRenderer(ModEntities.WISE_TREE.get(), WiseTreeRenderer::new);
            event.registerEntityRenderer(ModEntities.SINGING_SPROUTLING.get(), SproutlingRenderer::new);
            event.registerEntityRenderer(ModEntities.BUTTERFLY.get(), ButterflyRenderer::new);
            event.registerEntityRenderer(ModEntities.WANDERING_NOTE.get(), WanderingNoteRenderer::new);
            event.registerEntityRenderer(ModEntities.THE_GREAT_COMPOSER.get(), TheGreatComposerRenderer::new);
            event.registerEntityRenderer(ModEntities.MUSIC_NOTE_PROJECTILE.get(), MusicNoteProjectileRenderer::new);
            event.registerEntityRenderer(ModEntities.THE_GREAT_COMPOSER_CANON.get(), ComposerCanonRenderer::new);
            event.registerEntityRenderer(ModEntities.PLAYER_CANON.get(), CanonEntityRenderer::new);
            event.registerEntityRenderer(ModEntities.PHANTOM_NOTE_PROJECTILE.get(), PhantomNoteProjectileRenderer::new);
            event.registerEntityRenderer(ModEntities.THROWN_BOOGIE_BOMB.get(), ThrownItemRenderer::new);
            event.registerEntityRenderer(ModEntities.THROWN_DISCORD_BOMB.get(), ThrownItemRenderer::new);
        }

        @SubscribeEvent
        public static void registerOverlays(final RegisterGuiOverlaysEvent event) {
            event.registerBelow(VanillaGuiOverlay.HOTBAR.id(), "dialogue", DialogueScreen.OVERLAY);
            event.registerBelow(VanillaGuiOverlay.HOTBAR.id(), "dialogue_composer", TheGreatComposerScreen.OVERLAY);
            event.registerBelow(VanillaGuiOverlay.HOTBAR.id(), "dialogue_anya", AnyaScreen.OVERLAY);
            event.registerAbove(VanillaGuiOverlay.CROSSHAIR.id(), "melomancy_hottip", MelomancyCauldronScreen.OVERLAY);
            event.registerAbove(VanillaGuiOverlay.CROSSHAIR.id(), "discord_nuclei_hottip", DiscordNucleiScreen.OVERLAY);
            event.registerAbove(VanillaGuiOverlay.CROSSHAIR.id(), "composer_gravestone_gui", ComposerGravestoneScreen.OVERLAY);
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

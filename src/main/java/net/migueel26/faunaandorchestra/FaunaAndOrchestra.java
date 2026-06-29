package net.migueel26.faunaandorchestra;

import net.migueel26.faunaandorchestra.advancements.ModAdvancements;
import net.migueel26.faunaandorchestra.block.ModBlockEntities;
import net.migueel26.faunaandorchestra.block.ModBlocks;
import net.migueel26.faunaandorchestra.client.block.*;
import net.migueel26.faunaandorchestra.client.entity.*;
import net.migueel26.faunaandorchestra.client.entity.boss.ComposerCanonRenderer;
import net.migueel26.faunaandorchestra.client.entity.boss.TheGreatComposerRenderer;
import net.migueel26.faunaandorchestra.client.entity.misc.FloatingBlossomRenderer;
import net.migueel26.faunaandorchestra.client.entity.projectile.MusicNoteProjectileRenderer;
import net.migueel26.faunaandorchestra.client.entity.projectile.PhantomNoteProjectileRenderer;
import net.migueel26.faunaandorchestra.client.entity.projectile.SensorNoteRenderer;
import net.migueel26.faunaandorchestra.client.entity.projectile.WanderingNoteRenderer;
import net.migueel26.faunaandorchestra.client.item.*;
import net.migueel26.faunaandorchestra.component.ModDataComponents;
import net.migueel26.faunaandorchestra.effect.ModEffects;
import net.migueel26.faunaandorchestra.entity.ModEntities;
import net.migueel26.faunaandorchestra.entity.custom.projectile.ThrownBoogieBomb;
import net.migueel26.faunaandorchestra.entity.custom.projectile.ThrownDiscordBomb;
import net.migueel26.faunaandorchestra.item.ModCreativeModeTabs;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.item.ModPaintings;
import net.migueel26.faunaandorchestra.item.custom.ModItemRenderers;
import net.migueel26.faunaandorchestra.loot_tables.ModLootTables;
import net.migueel26.faunaandorchestra.networking.ModNetwork;
import net.migueel26.faunaandorchestra.particles.ModParticleTypes;
import net.migueel26.faunaandorchestra.particles.custom.*;
import net.migueel26.faunaandorchestra.potion.ModPotions;
import net.migueel26.faunaandorchestra.recipe.ModRecipes;
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

import static net.migueel26.faunaandorchestra.item.custom.ModItemRenderers.*;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(FaunaAndOrchestra.MOD_ID)
public class FaunaAndOrchestra {
    public static final String MOD_ID = "faunaandorchestra";
    public static final Logger LOGGER = LogUtils.getLogger();

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
        ModLootTables.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModEffects.register(modEventBus);
        ModPotions.register(modEventBus);
        ModRecipes.register(modEventBus);
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
            MenuScreens.register(ModMenuTypes.MUSICIAN_MENU.get(), MusicianScreen::new);
            MenuScreens.register(ModMenuTypes.FARMER_MENU.get(), FarmerScreen::new);
            MenuScreens.register(ModMenuTypes.MELOMANCER_MENU.get(), MelomancerScreen::new);
            MenuScreens.register(ModMenuTypes.TAILOR_MENU.get(), TailorScreen::new);
            MenuScreens.register(ModMenuTypes.LETTER_AND_QUILL_MENU.get(), LetterAndQuillScreen::new);
            MenuScreens.register(ModMenuTypes.MAILBOX_MENU.get(), MailboxScreen::new);
            MenuScreens.register(ModMenuTypes.HANGING_JAR_MENU.get(), HangingJarScreen::new);

            ItemProperties.register(ModItems.BRIEFCASE.get(), ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "opened"),
                    (stack, level, entity, seed) -> {
                        if (stack.hasTag() && stack.getTag().contains(ModDataComponents.OPENED)) {
                            return stack.getTag().getBoolean(ModDataComponents.OPENED) ? 1.0F : 0.0F;
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
            event.registerBlockEntityRenderer(ModBlockEntities.MOTHER_STATUE_BE.get(), MotherStatueBlockEntityRenderer::new);
            event.registerBlockEntityRenderer(ModBlockEntities.HANGING_JAR_BE.get(), HangingJarBlockEntityRenderer::new);
            event.registerBlockEntityRenderer(ModBlockEntities.JAR_RACK_BE.get(), JarRackBlockEntityRenderer::new);
            event.registerBlockEntityRenderer(ModBlockEntities.BAMBOO_TRAP_BE.get(), BambooTrapBlockEntityRenderer::new);
            event.registerBlockEntityRenderer(ModBlockEntities.BEAVER_STATUE_BE.get(), BeaverStatueBlockEntityRenderer::new);
            event.registerBlockEntityRenderer(ModBlockEntities.SEWING_MACHINE_BE.get(), SewingMachineBlockEntityRenderer::new);
            event.registerBlockEntityRenderer(ModBlockEntities.MAILBOX_BE.get(), MailboxBlockEntityRenderer::new);
            event.registerBlockEntityRenderer(ModBlockEntities.FLORA_ENHANCER.get(), FloraEnhancerBlockEntityRenderer::new);

            EntityRenderers.register(ModEntities.MANTIS.get(), MantisRenderer::new);
            EntityRenderers.register(ModEntities.QUIRKY_FROG.get(), QuirkyFrogRenderer::new);
            EntityRenderers.register(ModEntities.PENGUIN.get(), PenguinRenderer::new);
            EntityRenderers.register(ModEntities.EMPEROR_PENGUIN.get(), EmperorPenguinRenderer::new);
            EntityRenderers.register(ModEntities.RED_PANDA.get(), RedPandaRenderer::new);
            EntityRenderers.register(ModEntities.MACAW.get(), MacawRenderer::new);
            EntityRenderers.register(ModEntities.BEAVER.get(), BeaverRenderer::new);
            EntityRenderers.register(ModEntities.LEMUR.get(), LemurRenderer::new);
            EntityRenderers.register(ModEntities.MADAME_BUTTERFLY.get(), MadameButterflyRenderer::new);
            EntityRenderers.register(ModEntities.SEA_LION.get(), SeaLionRenderer::new);

            EntityRenderers.register(ModEntities.WANDERING_KOALA.get(), WanderingKoalaRenderer::new);
            EntityRenderers.register(ModEntities.BUTLER_KOALA.get(), ButlerKoalaRenderer::new);
            EntityRenderers.register(ModEntities.WORKER_KOALA.get(), WorkerKoalaRenderer::new);
            EntityRenderers.register(ModEntities.TAILOR_KOALA.get(), TailorKoalaRenderer::new);
            EntityRenderers.register(ModEntities.MELOMANCER_KOALA.get(), MelomancerKoalaRenderer::new);
            EntityRenderers.register(ModEntities.FARMER_KOALA.get(), FarmerKoalaRenderer::new);

            EntityRenderers.register(ModEntities.FAUST.get(), FaustRenderer::new);
            EntityRenderers.register(ModEntities.ORION.get(), OrionRenderer::new);
            EntityRenderers.register(ModEntities.ANYA_GHOST.get(), CanonEntityRenderer::new);
            EntityRenderers.register(ModEntities.WISE_TREE.get(), WiseTreeRenderer::new);
            EntityRenderers.register(ModEntities.DAN_B.get(), DanBRenderer::new);
            EntityRenderers.register(ModEntities.DELROY.get(), DelroyRenderer::new);
            EntityRenderers.register(ModEntities.DENISE.get(), DeniseRenderer::new);
            EntityRenderers.register(ModEntities.DENZEL.get(), DenzelRenderer::new);

            EntityRenderers.register(ModEntities.SINGING_SPROUTLING.get(), SproutlingRenderer::new);
            EntityRenderers.register(ModEntities.LIVING_MUSIC.get(), LivingMusicRenderer::new);
            EntityRenderers.register(ModEntities.BUTTERFLY.get(), ButterflyRenderer::new);
            EntityRenderers.register(ModEntities.WANDERING_NOTE.get(), WanderingNoteRenderer::new);
            EntityRenderers.register(ModEntities.THE_GREAT_COMPOSER.get(), TheGreatComposerRenderer::new);
            EntityRenderers.register(ModEntities.MUSIC_NOTE_PROJECTILE.get(), MusicNoteProjectileRenderer::new);
            EntityRenderers.register(ModEntities.THE_GREAT_COMPOSER_CANON.get(), ComposerCanonRenderer::new);
            EntityRenderers.register(ModEntities.PLAYER_CANON.get(), CanonEntityRenderer::new);
            EntityRenderers.register(ModEntities.PHANTOM_NOTE_PROJECTILE.get(), PhantomNoteProjectileRenderer::new);
            EntityRenderers.register(ModEntities.THROWN_BOOGIE_BOMB.get(), ThrownItemRenderer::new);
            EntityRenderers.register(ModEntities.THROWN_DISCORD_BOMB.get(), ThrownItemRenderer::new);
            EntityRenderers.register(ModEntities.SENSOR_NOTE.get(), SensorNoteRenderer::new);
            EntityRenderers.register(ModEntities.FLOATING_BLOSSOM.get(), FloatingBlossomRenderer::new);

            // Headwear
            register(ModItems.PROPELLER_HAT.get(), PropellerHatItemRenderer::new);
            register(ModItems.TOP_HAT.get(), TopHatItemRenderer::new);
            register(ModItems.SANTA_HAT.get(), SantaHatItemRenderer::new);
            register(ModItems.BASEBALL_CAP.get(), BaseballCapItemRenderer::new);

            // Items
            register(ModItems.DRUM.get(), DrumItemRenderer::new);
            register(ModItems.FLOATING_BLOSSOM.get(), FloatingBlossomItemRenderer::new);

            // Blocks
            register(ModItems.MOTHER_STATUE_ITEM.get(), MotherStatueItemRenderer::new);
            register(ModItems.BAMBOO_TRAP_ITEM.get(), BambooTrapItemRenderer::new);
            register(ModItems.BEAVER_STATUE_ITEM.get(), BeaverStatueItemRenderer::new);
            register(ModItems.SEWING_MACHINE_ITEM.get(), SewingMachineItemRenderer::new);
            register(ModItems.MAILBOX_ITEM.get(), MailboxItemRenderer::new);
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
            event.registerSpriteSet(ModParticleTypes.REGULAR_NOTE.get(), MagicalNoteParticle.Provider::new);
            event.registerSpriteSet(ModParticleTypes.CAULDRON_POP.get(), SculkChargePopParticle.Provider::new);
            event.registerSpriteSet(ModParticleTypes.STAR.get(), StarParticle.Provider::new);
            event.registerSpriteSet(ModParticleTypes.VOICE_PARTICLE.get(), VoiceParticle.Provider::new);
            event.registerSpriteSet(ModParticleTypes.SLEEP.get(), SleepParticle.Provider::new);
            event.registerSpriteSet(ModParticleTypes.BASS_CLEF.get(), BassClefParticle.Provider::new);
            event.registerSpriteSet(ModParticleTypes.SPEECH_BUBBLE.get(), SpeechBubbleParticle.Provider::new);
        }
    }

}

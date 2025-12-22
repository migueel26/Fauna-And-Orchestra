package net.migueel26.faunaandorchestra.item;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.block.ModBlocks;
import net.migueel26.faunaandorchestra.util.VesselUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, FaunaAndOrchestra.MOD_ID);

    public static final RegistryObject<CreativeModeTab> FAUNA_AND_ORCHESTRA = CREATIVE_MODE_TAB.register("fauna_and_orchestra_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.ICON.get()))
                    .title(Component.literal("Fauna & Orchestra"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModItems.VIOLIN.get());
                        output.accept(ModItems.FLUTE.get());
                        output.accept(ModItems.KEYTAR.get());
                        output.accept(ModItems.DOUBLE_BASS.get());
                        output.accept(ModItems.SAXOPHONE.get());
                        output.accept(ModItems.OBOE.get());
                        output.accept(ModItems.CELLO.get());
                        output.accept(ModItems.PAN_FLUTE.get());
                        output.accept(ModItems.PAN_FLUTE_CREATIVE.get());
                        output.accept(ModItems.BATON.get());
                        output.accept(ModItems.LEGENDARY_BATON.get());
                        output.accept(ModItems.BRIEFCASE.get());
                        output.accept(ModItems.BUTTERFLY_NET.get());
                        output.accept(ModItems.BUTTERFLY_SPAWN_EGG.get());
                        output.accept(ModItems.MANTIS_SPAWN_EGG.get());
                        output.accept(ModItems.PENGUIN_SPAWN_EGG.get());
                        output.accept(ModItems.RED_PANDA_SPAWN_EGG.get());
                        output.accept(ModItems.MACAW_SPAWN_EGG.get());
                        output.accept(ModItems.BEAVER_SPAWN_EGG.get());
                        output.accept(ModItems.LEMUR_SPAWN_EGG.get());
                        output.accept(ModItems.MADAME_BUTTERFLY_SPAWN_EGG.get());
                        output.accept(ModItems.QUIRKY_FROG_SPAWN_EGG.get());
                        output.accept(ModItems.WANDERING_KOALA_SPAWN_EGG.get());
                        output.accept(ModItems.SPROUTLING_SPAWN_EGG.get());
                        output.accept(ModItems.RINGTAILS_SPAWN_EGG.get());
                        output.accept(ModItems.WISE_TREE_SPAWN_EGG.get());
                        output.accept(ModItems.THE_GREAT_COMPOSER_SPAWN_EGG.get());
                        output.accept(ModItems.BACH_AIR_SHEET_MUSIC.get());
                        output.accept(ModItems.GREENSLEEVES_SHEET_MUSIC.get());
                        output.accept(ModItems.BLUES_SHEET_MUSIC.get());
                        output.accept(ModItems.JAZZY_FUR_ELISE_SHEET_MUSIC.get());
                        output.accept(ModItems.DANCE_OF_THE_LITTLE_SWANS.get());
                        output.accept(ModItems.LA_BAMBA_SHEET_MUSIC.get());
                        output.accept(ModItems.RESURRECTION_SONG.get());
                        output.accept(ModItems.SHEET_FRAGMENTS.get());
                        output.accept(ModItems.WHISTLE.get());
                        output.accept(ModItems.MUSIC_BOTTLE.get());
                        output.accept(ModItems.MUSICAL_INK.get());
                        output.accept(ModItems.BOOGIE_BOMB.get());
                        output.accept(ModItems.STEELSONIC_INGOT.get());
                        output.accept(ModItems.AMPLIFIER_CRYSTAL.get());
                        output.accept(ModItems.VOICE_VESSEL.get());
                        output.accept(ModItems.GINKGO_BILOBA.get());
                        output.accept(ModItems.WANDERING_NOTE.get());
                        output.accept(ModItems.BOOGIE_FRUIT.get());
                        output.accept(ModItems.SINGING_SEED.get());
                        output.accept(ModItems.GLOVE.get());
                        output.accept(ModBlocks.TIP_CASE);
                        output.accept(ModBlocks.LISTENER);
                        output.accept(ModBlocks.LISTENER_CONTAINER);
                        output.accept(ModItems.MELOMANCY_CAULDRON_ITEM.get());
                        output.accept(ModBlocks.VOICE_CHAMBER);
                        output.accept(ModBlocks.ALTAR_OF_THE_PAN_FLUTE);
                        output.accept(ModBlocks.ALTAR_OF_THE_BUTTERFLIES);
                        output.accept(ModBlocks.ALTAR);
                        output.accept(ModBlocks.COMPOSER_GRAVESTONE);
                        output.accept(ModBlocks.GRAVESTONE);
                        output.accept(ModItems.THE_GREAT_HEAD_ITEM.get());
                        output.accept(ModItems.TRANSMUTED_VOICE.get());
                        output.accept(ModItems.DISCORD_ESSENCE.get());
                        output.accept(ModItems.ACTIVATOR_CLEF.get());
                        output.accept(ModItems.PETALS_OF_DEATH.get());
                        output.accept(ModBlocks.DISCORDED_FLOWER);
                        output.accept(ModItems.DISCORD_BOMB.get());
                        output.accept(ModItems.DISCORD_NUCLEI_ITEM.get());
                        output.accept(ModBlocks.CRAWLING_DISCORD);
                        output.accept(ModBlocks.DISCORD_BLOCK);
                        output.accept(ModBlocks.DAM_BLOCK);
                        output.accept(ModBlocks.GINGKO_BILOBA_LEAVES);
                        output.accept(ModBlocks.GINGKO_BILOBA_SAPLING);
                        output.accept(ModItems.FRUIT_OF_LIFE.get());
                        output.accept(ModItems.OFFERING.get());
                        output.acceptAll(VesselUtil.getAllVoiceItems());
                        output.accept(ModItems.UNLOCKER.get());
                    }).build());


    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}

package net.migueel26.faunaandorchestra.item;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, FaunaAndOrchestra.MOD_ID);

    public static final Supplier<CreativeModeTab> FAUNA_AND_ORCHESTRA = CREATIVE_MODE_TAB.register("fauna_and_orchestra",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.ICON.get()))
                    .title(Component.literal("Fauna and Orchestra"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModItems.VIOLIN);
                        output.accept(ModItems.FLUTE);
                        output.accept(ModItems.KEYTAR);
                        output.accept(ModItems.DOUBLE_BASS);
                        output.accept(ModItems.SAXOPHONE);
                        output.accept(ModItems.OBOE);
                        output.accept(ModItems.BATON);
                        output.accept(ModItems.BRIEFCASE);
                        output.accept(ModItems.MANTIS_SPAWN_EGG);
                        output.accept(ModItems.PENGUIN_SPAWN_EGG);
                        output.accept(ModItems.RED_PANDA_SPAWN_EGG);
                        output.accept(ModItems.MACAW_SPAWN_EGG);
                        output.accept(ModItems.BEAVER_SPAWN_EGG);
                        output.accept(ModItems.LEMUR_SPAWN_EGG);
                        output.accept(ModItems.QUIRKY_FROG_SPAWN_EGG);
                        output.accept(ModItems.WANDERING_KOALA_SPAWN_EGG);
                        output.accept(ModItems.BACH_AIR_SHEET_MUSIC);
                        output.accept(ModItems.GREENSLEEVES_SHEET_MUSIC);
                        output.accept(ModItems.BLUES_SHEET_MUSIC);
                        output.accept(ModItems.JAZZY_FUR_ELISE_SHEET_MUSIC);
                        output.accept(ModItems.DANCE_OF_THE_LITTLE_SWANS);
                        output.accept(ModItems.RESURRECTION_SONG);
                        output.accept(ModItems.MUSIC_BOTTLE);
                        output.accept(ModItems.SHEET_FRAGMENTS);
                        output.accept(ModBlocks.COMPOSER_GRAVESTONE);
                        output.accept(ModBlocks.GRAVESTONE);
                    }).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}

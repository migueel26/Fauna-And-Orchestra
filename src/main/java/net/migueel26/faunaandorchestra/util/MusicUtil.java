package net.migueel26.faunaandorchestra.util;

import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.item.custom.InstrumentItem;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.Map;

public class MusicUtil {
    private static final Map<Item, ResourceLocation> BACH_AIR = Map.of(
            ModItems.VIOLIN.get(), ModSounds.BACH_AIR_VIOLIN.get().getLocation(),
            ModItems.FLUTE.get(), ModSounds.BACH_AIR_FLUTE.get().getLocation()
    );

    public static String getLocation(Item sheet, Item instrument) {
        //if (sheet.getDefaultInstance().is(ModTags.Items.SHEET_MUSIC)) {
            if (instrument instanceof InstrumentItem) {

                return BACH_AIR.get(instrument).getPath();

            } else {
                throw new IllegalArgumentException("Tried to get the music sound of a non-instrument item!");
            }
        //} else {
        //    throw new IllegalArgumentException("Tried to get the music sound of a non-sheet item!");
        //}
    }

}

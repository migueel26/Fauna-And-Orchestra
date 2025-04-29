package net.migueel26.faunaandorchestra.util;

import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.item.custom.InstrumentItem;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MusicUtil {
    private static final Map<Item, ResourceLocation> BACH_AIR = Map.of(
            ModItems.VIOLIN.get(), ModSounds.BACH_AIR_VIOLIN.get().getLocation(),
            ModItems.FLUTE.get(), ModSounds.BACH_AIR_FLUTE.get().getLocation(),
            ModItems.KEYTAR.get(), ModSounds.BACH_AIR_KEYTAR.get().getLocation(),
            ModItems.DOUBLE_BASS.get(), ModSounds.BACH_AIR_DOUBLE_BASS.get().getLocation()
    );

    private static final Map<Item, Map<Item, ResourceLocation>> SONG = Map.of(
            ModItems.BACH_AIR_SHEET_MUSIC.get(), BACH_AIR
    );

    private static Map<UUID, Item> CURRENT_ORCHESTRAS = new HashMap<>();

    public static String getLocation(Item sheet, Item instrument) {
        if (sheet.getDefaultInstance().is(ModTags.Items.SHEET_MUSIC)) {
            if (instrument instanceof InstrumentItem) {

                return SONG.get(sheet).get(instrument).getPath();

            } else {
                throw new IllegalArgumentException("Tried to get the music sound of a non-instrument item!");
            }
        } else {
            throw new IllegalArgumentException("Tried to get the music sound of a non-sheet item!");
        }
    }

    public static void addNewOrchestra(UUID conductorUUID, Item sheetMusic) {
        CURRENT_ORCHESTRAS.put(conductorUUID, sheetMusic);
    }

    public static void deleteOrchestra(UUID conductorUUID) {
        CURRENT_ORCHESTRAS.remove(conductorUUID);
    }

    public static boolean updateNewSheet(UUID conductorUUID, Item sheetMusic) {
        Item currentSheet = CURRENT_ORCHESTRAS.get(conductorUUID);
        if (currentSheet == sheetMusic) {
            return false;
        } else {
            CURRENT_ORCHESTRAS.put(conductorUUID, sheetMusic);
            return true;
        }
    }

    public static Item getSheet(UUID conductorUUID) {
        return CURRENT_ORCHESTRAS.get(conductorUUID);
    }
}

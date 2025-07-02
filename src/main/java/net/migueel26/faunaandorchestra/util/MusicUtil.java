package net.migueel26.faunaandorchestra.util;

import net.migueel26.faunaandorchestra.entity.custom.ConductorEntity;
import net.migueel26.faunaandorchestra.entity.custom.MusicalEntity;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.item.custom.InstrumentItem;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.*;

public class MusicUtil {
    private static final Map<Item, ResourceLocation> BACH_AIR = Map.of(
            ModItems.VIOLIN.get(), ModSounds.BACH_AIR_VIOLIN.get().getLocation(),
            ModItems.FLUTE.get(), ModSounds.BACH_AIR_FLUTE.get().getLocation(),
            ModItems.KEYTAR.get(), ModSounds.BACH_AIR_KEYTAR.get().getLocation(),
            ModItems.DOUBLE_BASS.get(), ModSounds.BACH_AIR_DOUBLE_BASS.get().getLocation()
    );

    public static final Map<Item, ResourceLocation> GREENSLEEVES = Map.of(
            ModItems.VIOLIN.get(), ModSounds.GREENSLEEVES_VIOLIN.get().getLocation(),
            ModItems.FLUTE.get(), ModSounds.GREENSLEEVES_FLUTE.get().getLocation(),
            ModItems.KEYTAR.get(), ModSounds.GREENSLEEVES_KEYTAR.get().getLocation()
    );

    private static final Map<Item, Map<Item, ResourceLocation>> SONG = Map.of(
            ModItems.BACH_AIR_SHEET_MUSIC.get(), BACH_AIR,
            ModItems.GREENSLEEVES_SHEET_MUSIC.get(), GREENSLEEVES
    );

    private static final Map<String, Item> STRING_TO_SHEET = Map.of(
            "faunaandorchestra:bach_air_sheet_music", ModItems.BACH_AIR_SHEET_MUSIC.get(),
            "faunaandorchestra:greensleeves_sheet_music", ModItems.GREENSLEEVES_SHEET_MUSIC.get()
    );

    private static final Map<Item, Integer> DURATION = Map.of(
            ModItems.BACH_AIR_SHEET_MUSIC.get(), 2550,
            ModItems.GREENSLEEVES_SHEET_MUSIC.get(), 1315
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

    public static String musicalAnimalToString(Entity entity) {
        StringJoiner out = new StringJoiner(";");
        String animal = entity.getClass().getSimpleName();
        out.add(animal);
        if (animal.equals("QuirkyFrogEntity")) {
            out.add(((ConductorEntity) entity).isHoldingBaton() ? "t" : "f");
            out.add(((ConductorEntity) entity).getSheetMusic().toString());
        } else {
            out.add(((MusicalEntity) entity).isHoldingInstrument() ? "t" : "f");
            out.add("f");
        }
        out.add(entity.getCustomName() != null ? entity.getCustomName().getString() : "f");
        return out.toString();
    }

    public static int getMaxSize(Item sheet) {
        return SONG.getOrDefault(sheet, Map.of()).size();
    }

    public static Set<Item> getInstruments(Item sheet) {
        return SONG.get(sheet).keySet();
    }

    public static int getDuration(Item sheet) {
        return DURATION.getOrDefault(sheet, -1);
    }

    public static Item getSheet(UUID conductorUUID) {
        return CURRENT_ORCHESTRAS.get(conductorUUID);
    }

    public static Item getSheet(String name) {
        return STRING_TO_SHEET.getOrDefault(name, Items.AIR);
    }

}

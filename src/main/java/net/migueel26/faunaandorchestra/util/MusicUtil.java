package net.migueel26.faunaandorchestra.util;

import net.migueel26.faunaandorchestra.entity.custom.ConductorEntity;
import net.migueel26.faunaandorchestra.entity.custom.MusicalEntity;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.item.custom.InstrumentItem;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

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

    public static final Map<Item, ResourceLocation> BLUES_SONG = Map.of(
            ModItems.SAXOPHONE.get(), ModSounds.BLUES_SAXOPHONE.get().getLocation(),
            ModItems.DOUBLE_BASS.get(), ModSounds.BLUES_DOUBLE_BASS.get().getLocation(),
            ModItems.KEYTAR.get(), ModSounds.BLUES_KEYTAR.get().getLocation()
    );

    public static final Map<Item, ResourceLocation> JAZZY_FUR_ELISE = Map.of(
            ModItems.SAXOPHONE.get(), ModSounds.FUR_ELISE_SAXOPHONE.get().getLocation(),
            ModItems.FLUTE.get(), ModSounds.FUR_ELISE_FLUTE.get().getLocation(),
            ModItems.KEYTAR.get(), ModSounds.FUR_ELISE_KEYTAR.get().getLocation()
    );

    public static final Map<Item, ResourceLocation> SWANS = Map.of(
            ModItems.OBOE.get(), ModSounds.SWANS_OBOE.get().getLocation(),
            ModItems.FLUTE.get(), ModSounds.SWANS_FLUTE.get().getLocation(),
            ModItems.KEYTAR.get(), ModSounds.SWANS_KEYTAR.get().getLocation()
    );

    public static final Map<Item, ResourceLocation> RESURRECTION = Map.of(
            ModItems.OBOE.get(), ModSounds.RESURRECTION_OBOE.get().getLocation(),
            ModItems.FLUTE.get(), ModSounds.RESURRECTION_FLUTE.get().getLocation(),
            ModItems.CELLO.get(), ModSounds.RESURRECTION_CELLO.get().getLocation(),
            ModItems.KEYTAR.get(), ModSounds.RESURRECTION_KEYTAR.get().getLocation(),
            ModItems.VIOLIN.get(), ModSounds.RESURRECTION_VIOLIN.get().getLocation()
    );

    public static final Map<Item, ResourceLocation> LA_BAMBA = Map.of(
            ModItems.CELLO.get(), ModSounds.BAMBA_CELLO.get().getLocation(),
            ModItems.DOUBLE_BASS.get(), ModSounds.BAMBA_DOUBLE_BASS.get().getLocation(),
            ModItems.FLUTE.get(), ModSounds.BAMBA_FLUTE.get().getLocation(),
            ModItems.KEYTAR.get(), ModSounds.BAMBA_KEYTAR.get().getLocation(),
            ModItems.SAXOPHONE.get(), ModSounds.BAMBA_SAXOPHONE.get().getLocation(),
            ModItems.VIOLIN.get(), ModSounds.BAMBA_VIOLIN.get().getLocation()
    );

    private static final Map<Item, Map<Item, ResourceLocation>> SONG = Map.of(
            ModItems.BACH_AIR_SHEET_MUSIC.get(), BACH_AIR,
            ModItems.GREENSLEEVES_SHEET_MUSIC.get(), GREENSLEEVES,
            ModItems.BLUES_SHEET_MUSIC.get(), BLUES_SONG,
            ModItems.JAZZY_FUR_ELISE_SHEET_MUSIC.get(), JAZZY_FUR_ELISE,
            ModItems.DANCE_OF_THE_LITTLE_SWANS.get(), SWANS,
            ModItems.LA_BAMBA_SHEET_MUSIC.get(), LA_BAMBA,
            ModItems.RESURRECTION_SONG.get(), RESURRECTION
    );

    private static final Map<String, Item> STRING_TO_SHEET = Map.of(
            "bach_air_sheet_music", ModItems.BACH_AIR_SHEET_MUSIC.get(),
            "greensleeves_sheet_music", ModItems.GREENSLEEVES_SHEET_MUSIC.get(),
            "blues_sheet_music", ModItems.BLUES_SHEET_MUSIC.get(),
            "jazzy_fur_elise_sheet_music", ModItems.JAZZY_FUR_ELISE_SHEET_MUSIC.get(),
            "dance_of_the_little_swans_sheet_music", ModItems.DANCE_OF_THE_LITTLE_SWANS.get(),
            "la_bamba_sheet_music", ModItems.LA_BAMBA_SHEET_MUSIC.get(),
            "resurrection_song", ModItems.RESURRECTION_SONG.get()
    );

    private static final Map<Item, Integer> DURATION = Map.of(
            ModItems.BACH_AIR_SHEET_MUSIC.get(), 2550,
            ModItems.GREENSLEEVES_SHEET_MUSIC.get(), 1315,
            ModItems.BLUES_SHEET_MUSIC.get(), 1750,
            ModItems.JAZZY_FUR_ELISE_SHEET_MUSIC.get(), 1775,
            ModItems.DANCE_OF_THE_LITTLE_SWANS.get(), 1895,
            ModItems.LA_BAMBA_SHEET_MUSIC.get(), 1115,
            ModItems.RESURRECTION_SONG.get(), 4990
    );

    public static final List<Item> INSTRUMENTS = new ArrayList<>(List.of(
            ModItems.FLUTE.get(),
            ModItems.OBOE.get(),
            ModItems.SAXOPHONE.get(),
            ModItems.CELLO.get(),
            ModItems.DOUBLE_BASS.get(),
            ModItems.VIOLIN.get(),
            ModItems.KEYTAR.get())
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
            out.add(((ConductorEntity) entity).isHoldingLegendaryBaton() ? "t" : "f");
        } else {
            out.add(((MusicalEntity) entity).isHoldingInstrument() ? "t" : "f");
            out.add("f");
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
        return DURATION.getOrDefault(sheet, -2);
    }

    public static Item getSheet(UUID conductorUUID) {
        return CURRENT_ORCHESTRAS.get(conductorUUID);
    }

    public static Item getSheet(String name) {
        return STRING_TO_SHEET.getOrDefault(name, Items.AIR);
    }

    public static Item getRandomInstrument(Level level) {
        return INSTRUMENTS.get(level.random.nextInt(0, INSTRUMENTS.size()));
    }

}

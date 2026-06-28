package net.migueel26.faunaandorchestra.advancements;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ModAdvancements {
    public static List<CustomSimpleTrigger> TRIGGERS = new ArrayList<>();

    public static final CustomSimpleTrigger FULL_ORCHESTRA = register("full_orchestra");
    public static final CustomSimpleTrigger MEET_RINGTAILS = register("meet_ringtails");
    public static final CustomSimpleTrigger MEET_JAZZY_DAMMYS = register("meet_jazzy_dammys");
    public static final CustomSimpleTrigger BEFRIEND_ORION = register("befriend_orion");
    public static final CustomSimpleTrigger BEFRIEND_FAUST = register("befriend_faust");
    public static final CustomSimpleTrigger USE_MELOMANCY_CAULDRON = register("use_melomancy_cauldron");
    public static final CustomSimpleTrigger WISE_TREE = register("wise_tree");
    public static final CustomSimpleTrigger DISCORD_NUCLEI = register("discord_nuclei");
    public static final CustomSimpleTrigger USE_DISCORD_BOMB = register("use_discord_bomb");
    public static final CustomSimpleTrigger PAN_FLUTE = register("pan_flute");
    public static final CustomSimpleTrigger PAN_FLUTE_COMPLETE = register("pan_flute_complete");
    public static final CustomSimpleTrigger KILL_COMPOSER = register("kill_composer");
    public static final CustomSimpleTrigger PLAYER_CANON = register("player_canon");
    public static final CustomSimpleTrigger TAME_MUSICIAN = register("tame_musician");
    public static final CustomSimpleTrigger TAME_FROG = register("tame_frog");
    public static final CustomSimpleTrigger FIRST_RESOLVED_MYTH = register("diskinserted");
    public static final CustomSimpleTrigger LIVING_MUSIC = register("living_music");
    public static final CustomSimpleTrigger BRED_MUSICIANS = register("bred_musicians");

    // DAN MYTHS
    public static final CustomSimpleTrigger DAN_MYTH0 = register("dan_myth0");
    public static final CustomSimpleTrigger DAN_MYTH1 = register("dan_myth1");
    public static final CustomSimpleTrigger DAN_MYTH2 = register("dan_myth2");

    public static CustomSimpleTrigger register(String path) {
        CustomSimpleTrigger trigger = new CustomSimpleTrigger(ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, path));
        TRIGGERS.add(trigger);
        return trigger;
    }

    public static void register() {
        for (CustomSimpleTrigger trigger : TRIGGERS) {
            CriteriaTriggers.register(trigger);
        }
    }
}

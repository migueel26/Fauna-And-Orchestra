package net.migueel26.faunaandorchestra.advancements;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.resources.ResourceLocation;

public class ModAdvancements {
    public static final CustomSimpleTrigger FULL_ORCHESTRA = new CustomSimpleTrigger(ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "full_orchestra"));
    public static final CustomSimpleTrigger MEET_RINGTAILS = new CustomSimpleTrigger(ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "meet_ringtails"));
    public static final CustomSimpleTrigger BEFRIEND_ORION = new CustomSimpleTrigger(ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "befriend_orion"));
    public static final CustomSimpleTrigger BEFRIEND_FAUST = new CustomSimpleTrigger(ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "befriend_faust"));
    public static final CustomSimpleTrigger USE_MELOMANCY_CAULDRON = new CustomSimpleTrigger(ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "use_melomancy_cauldron"));
    public static final CustomSimpleTrigger WISE_TREE = new CustomSimpleTrigger(ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "wise_tree"));
    public static final CustomSimpleTrigger DISCORD_NUCLEI = new CustomSimpleTrigger(ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "discord_nuclei"));
    public static final CustomSimpleTrigger USE_DISCORD_BOMB = new CustomSimpleTrigger(ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "use_discord_bomb"));
    public static final CustomSimpleTrigger PAN_FLUTE = new CustomSimpleTrigger(ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "pan_flute"));
    public static final CustomSimpleTrigger PAN_FLUTE_COMPLETE = new CustomSimpleTrigger(ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "pan_flute_complete"));
    public static final CustomSimpleTrigger KILL_COMPOSER = new CustomSimpleTrigger(ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "kill_composer"));
    public static final CustomSimpleTrigger PLAYER_CANON = new CustomSimpleTrigger(ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "player_canon"));
    public static final CustomSimpleTrigger TAME_MUSICIAN = new CustomSimpleTrigger(ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "tame_musician"));
    public static final CustomSimpleTrigger TAME_FROG = new CustomSimpleTrigger(ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "tame_frog"));

    // Método para registrar todo manualmente
    public static void register() {
        CriteriaTriggers.register(FULL_ORCHESTRA);
        CriteriaTriggers.register(MEET_RINGTAILS);
        CriteriaTriggers.register(BEFRIEND_ORION);
        CriteriaTriggers.register(BEFRIEND_FAUST);
        CriteriaTriggers.register(USE_MELOMANCY_CAULDRON);
        CriteriaTriggers.register(WISE_TREE);
        CriteriaTriggers.register(DISCORD_NUCLEI);
        CriteriaTriggers.register(USE_DISCORD_BOMB);
        CriteriaTriggers.register(PAN_FLUTE);
        CriteriaTriggers.register(PAN_FLUTE_COMPLETE);
        CriteriaTriggers.register(KILL_COMPOSER);
        CriteriaTriggers.register(PLAYER_CANON);
        CriteriaTriggers.register(TAME_MUSICIAN);
        CriteriaTriggers.register(TAME_FROG);
    }
}

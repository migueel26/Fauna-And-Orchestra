package net.migueel26.faunaandorchestra.advancements;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModAdvancements {
    public static final DeferredRegister<CriterionTrigger<?>> TRIGGERS =
            DeferredRegister.create(BuiltInRegistries.TRIGGER_TYPES, FaunaAndOrchestra.MOD_ID);

    public static final DeferredHolder<CriterionTrigger<?>, CustomSimpleTrigger> FULL_ORCHESTRA = register("full_orchestra");
    public static final DeferredHolder<CriterionTrigger<?>, CustomSimpleTrigger> MEET_RINGTAILS = register("meet_ringtails");
    public static final DeferredHolder<CriterionTrigger<?>, CustomSimpleTrigger> MEET_JAZZY_DAMMYS = register("meet_jazzy_dammys");
    public static final DeferredHolder<CriterionTrigger<?>, CustomSimpleTrigger> BEFRIEND_ORION = register("befriend_orion");
    public static final DeferredHolder<CriterionTrigger<?>, CustomSimpleTrigger> BEFRIEND_FAUST = register("befriend_faust");
    public static final DeferredHolder<CriterionTrigger<?>, CustomSimpleTrigger> USE_MELOMANCY_CAULDRON = register("use_melomancy_cauldron");
    public static final DeferredHolder<CriterionTrigger<?>, CustomSimpleTrigger> WISE_TREE = register("wise_tree");
    public static final DeferredHolder<CriterionTrigger<?>, CustomSimpleTrigger> DISCORD_NUCLEI = register("discord_nuclei");
    public static final DeferredHolder<CriterionTrigger<?>, CustomSimpleTrigger> USE_DISCORD_BOMB = register("use_discord_bomb");
    public static final DeferredHolder<CriterionTrigger<?>, CustomSimpleTrigger> PAN_FLUTE = register("pan_flute");
    public static final DeferredHolder<CriterionTrigger<?>, CustomSimpleTrigger> PAN_FLUTE_COMPLETE = register("pan_flute_complete");
    public static final DeferredHolder<CriterionTrigger<?>, CustomSimpleTrigger> KILL_COMPOSER = register("kill_composer");
    public static final DeferredHolder<CriterionTrigger<?>, CustomSimpleTrigger> PLAYER_CANON = register("player_canon");
    public static final DeferredHolder<CriterionTrigger<?>, CustomSimpleTrigger> TAME_MUSICIAN = register("tame_musician");
    public static final DeferredHolder<CriterionTrigger<?>, CustomSimpleTrigger> TAME_FROG = register("tame_frog");
    public static final DeferredHolder<CriterionTrigger<?>, CustomSimpleTrigger> FIRST_RESOLVED_MYTH = register("diskinserted");

    // DAN MYTHS
    public static final DeferredHolder<CriterionTrigger<?>, CustomSimpleTrigger> DAN_MYTH0 = register("dan_myth0");
    public static final DeferredHolder<CriterionTrigger<?>, CustomSimpleTrigger> DAN_MYTH1 = register("dan_myth1");
    public static final DeferredHolder<CriterionTrigger<?>, CustomSimpleTrigger> DAN_MYTH2 = register("dan_myth2");

    public static <T extends CriterionTrigger<?>> DeferredHolder<CriterionTrigger<?>, CustomSimpleTrigger> register(String name) {
        return TRIGGERS.register(name, CustomSimpleTrigger::new);
    }

    public static void register(IEventBus eventBus) {
        TRIGGERS.register(eventBus);
    }
}

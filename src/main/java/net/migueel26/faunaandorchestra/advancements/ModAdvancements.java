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
    public static final DeferredHolder<CriterionTrigger<?>, CustomSimpleTrigger> BEFRIEND_ORION = register("befriend_orion");
    public static final DeferredHolder<CriterionTrigger<?>, CustomSimpleTrigger> BEFRIEND_FAUST = register("befriend_faust");

    public static <T extends CriterionTrigger<?>> DeferredHolder<CriterionTrigger<?>, CustomSimpleTrigger> register(String name) {
        return TRIGGERS.register(name, CustomSimpleTrigger::new);
    }

    public static void register(IEventBus eventBus) {
        TRIGGERS.register(eventBus);
    }
}

package net.migueel26.faunaandorchestra.effect;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(
            Registries.MOB_EFFECT, FaunaAndOrchestra.MOD_ID);

    public static final RegistryObject<MobEffect> BOOGIE = MOB_EFFECTS.register("boogie",
            () -> new BoogieEffect(MobEffectCategory.HARMFUL, 0xc8a2c8));

    public static final RegistryObject<MobEffect> ABSOLUTE_HEARING = MOB_EFFECTS.register("absolute_hearing",
            () -> new AbsoluteHearingEffect(MobEffectCategory.NEUTRAL, 0xffe79e));


    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}

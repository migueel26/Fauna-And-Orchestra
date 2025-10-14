package net.migueel26.faunaandorchestra.potion;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.effect.ModEffects;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModPotions {
    public static DeferredRegister<Potion> POTIONS =
            DeferredRegister.create(BuiltInRegistries.POTION, FaunaAndOrchestra.MOD_ID);

    public static Holder<Potion> ABSOLUTE_HEARING_POTION = POTIONS.register("absolute_hearing_potion",
            () -> new Potion(new MobEffectInstance(ModEffects.ABSOLUTE_HEARING, 600)));

    public static void register(IEventBus eventBus) {
        POTIONS.register(eventBus);
    }
}

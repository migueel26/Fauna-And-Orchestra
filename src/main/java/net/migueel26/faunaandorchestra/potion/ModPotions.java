package net.migueel26.faunaandorchestra.potion;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.effect.ModEffects;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModPotions {
    public static DeferredRegister<Potion> POTIONS =
            DeferredRegister.create(Registries.POTION, FaunaAndOrchestra.MOD_ID);

    public static RegistryObject<Potion> ABSOLUTE_HEARING_POTION = POTIONS.register("absolute_hearing_potion",
            () -> new Potion(new MobEffectInstance(ModEffects.ABSOLUTE_HEARING.get(), 600)));

    public static void register(IEventBus eventBus) {
        POTIONS.register(eventBus);
    }
}

package net.migueel26.faunaandorchestra.item;

import net.migueel26.faunaandorchestra.effect.ModEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class ModFoodProperties {
    public static final FoodProperties BOOGIE_FRUIT = new FoodProperties.Builder().nutrition(3).saturationModifier(0.15f)
            .effect(() -> new MobEffectInstance(ModEffects.BOOGIE_EFFECT, 100), 1.0f).build();

}

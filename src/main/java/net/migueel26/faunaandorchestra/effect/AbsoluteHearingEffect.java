package net.migueel26.faunaandorchestra.effect;

import net.migueel26.faunaandorchestra.entity.ModEntities;
import net.migueel26.faunaandorchestra.entity.custom.ConductorEntity;
import net.migueel26.faunaandorchestra.entity.custom.WanderingNoteEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import java.util.List;
import java.util.Optional;

public class AbsoluteHearingEffect extends MobEffect {
    protected AbsoluteHearingEffect(MobEffectCategory category, int color) {
        super(category, color);
    }
}

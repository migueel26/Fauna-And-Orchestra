package net.migueel26.faunaandorchestra.effect;

import net.migueel26.faunaandorchestra.entity.ModEntities;
import net.migueel26.faunaandorchestra.entity.custom.ConductorEntity;
import net.migueel26.faunaandorchestra.entity.custom.WanderingNoteEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import java.util.Optional;

public class AbsoluteHearingEffect extends MobEffect {
    protected int tick = 0;
    protected AbsoluteHearingEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        Optional<ConductorEntity> conductor = livingEntity.level().getNearbyEntities(ConductorEntity.class, TargetingConditions.DEFAULT, livingEntity, livingEntity.getBoundingBox().inflate(15.0f))
                .stream()
                .filter(ConductorEntity::isHoldingASheetMusic)
                .filter(ConductorEntity::isConducting)
                .findAny();

        if (conductor.isPresent() && tick % 40 == 0) {
            WanderingNoteEntity entity = new WanderingNoteEntity(ModEntities.WANDERING_NOTE.get(), livingEntity.level());
            int x = livingEntity.getRandom().nextInt(9) - 4;
            int z = livingEntity.getRandom().nextInt(3);
            int y = livingEntity.getRandom().nextInt(9) - 4;

            entity.moveTo(livingEntity.getX() + x, livingEntity.getY() + y, livingEntity.getZ() + z);
            livingEntity.level().addFreshEntity(entity);

            entity.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0f, 0.5f);

        }

        tick++;
        return super.applyEffectTick(livingEntity, amplifier);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}

package net.migueel26.faunaandorchestra.entity.custom.decorative;

import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class HealthFluteEntity extends Bat {
    protected int tick = 0;
    public HealthFluteEntity(EntityType<? extends Bat> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    @Override
    public void tick() {
        if (!level().isClientSide()) {
            ((ServerLevel) level()).sendParticles(ParticleTypes.HAPPY_VILLAGER, getX(), getY(), getZ(), 5, 0.2, 0.2, 0.2, 0.05);
        }

        if (tick == 70) {
            level().playSound(null, this.blockPosition(), ModSounds.MAGIC_GROWTH.get(), SoundSource.NEUTRAL, 0.6f, 1.0f);

            AABB aabb = this.getBoundingBox().inflate(8.0);
            List<Player> players = this.level().getEntitiesOfClass(Player.class, aabb);

            for (Player player : players) {
                if (!player.hasEffect(MobEffects.REGENERATION)) {
                    player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 1));
                }
            }

            if (!level().isClientSide()) {
                ((ServerLevel) level()).sendParticles(ParticleTypes.SCRAPE, this.getX(), this.getY(), this.getZ(), 40, 3.0f, 3.0f, 3.0f, 0.05);
            }

            this.discard();
        }

        tick++;

        super.tick();
    }

    @Override
    public boolean isSilent() {
        return true;
    }
}

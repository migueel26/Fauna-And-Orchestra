package net.migueel26.faunaandorchestra.entity.custom.projectile;

import net.migueel26.faunaandorchestra.effect.ModEffects;
import net.migueel26.faunaandorchestra.entity.ModEntities;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.particles.ModParticleTypes;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class ThrownBoogieBomb extends ThrowableItemProjectile implements ItemSupplier {
    public ThrownBoogieBomb(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public ThrownBoogieBomb(Level level, LivingEntity shooter) {
        super(ModEntities.THROWN_BOOGIE_BOMB.get(), shooter, level);
    }

    public ThrownBoogieBomb(Level level, double x, double y, double z) {
        super(ModEntities.THROWN_BOOGIE_BOMB.get(), x, y, z, level);
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return ModItems.BOOGIE_BOMB.get();
    }

    @Override
    protected float getGravity() {
        return 0.05f;
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            Vec3 vec3 = result.getLocation();
            level().playSound(null, vec3.x, vec3.y, vec3.z, ModSounds.BOOGIE_BOMB_DANCE.get(), SoundSource.NEUTRAL, 1.0f, 1.0f);
            level().playSound(null, vec3.x, vec3.y, vec3.z, SoundEvents.GENERIC_EXPLODE, SoundSource.NEUTRAL, 1.0f, 1.5f);
            ((ServerLevel) level()).sendParticles(ModParticleTypes.MAGICAL_NOTE.get(), vec3.x, vec3.y, vec3.z, 50, 1.5, 1.5, 1.5, 0.1);
            ((ServerLevel) level()).sendParticles(ModParticleTypes.MAGICAL_NOTE.get(), vec3.x, vec3.y, vec3.z, 30, 0.1, 0.1, 0.1, 0.05);
            this.applySplash(
                    new MobEffectInstance(ModEffects.BOOGIE.get(), 180), result.getType() == HitResult.Type.ENTITY ? ((EntityHitResult) result).getEntity() : null
            );

            this.discard();
        }
    }

    private void applySplash(MobEffectInstance effect, @Nullable Entity p_entity) {
        AABB aabb = this.getBoundingBox().inflate(5.0, 2.0, 5.0);
        List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, aabb);
        if (!list.isEmpty()) {
            Entity entity = this.getEffectSource();

            for (LivingEntity livingentity : list) {
                if (livingentity.isAffectedByPotions()) {
                    double d0 = this.distanceToSqr(livingentity);
                    if (d0 < 16.0) {
                        double d1;
                        if (livingentity == p_entity) {
                            d1 = 1.0;
                        } else {
                            d1 = 1.0 - Math.sqrt(d0) / 4.0;
                        }

                        MobEffect holder = effect.getEffect();
                        if (holder.isInstantenous()) {
                            holder.applyInstantenousEffect(this, this.getOwner(), livingentity, effect.getAmplifier(), d1);
                        } else {
                            int i = effect.mapDuration(p_267930_ -> (int) (d1 * (double) p_267930_ + 0.5));
                            MobEffectInstance mobeffectinstance1 = new MobEffectInstance(
                                    holder, i, effect.getAmplifier(), effect.isAmbient(), effect.isVisible()
                            );
                            if (!mobeffectinstance1.endsWithin(20)) {
                                livingentity.addEffect(mobeffectinstance1, entity);
                            }
                        }
                    }
                }
            }
        }
    }
}

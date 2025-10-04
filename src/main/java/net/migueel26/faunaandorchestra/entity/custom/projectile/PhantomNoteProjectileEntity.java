package net.migueel26.faunaandorchestra.entity.custom.projectile;

import net.migueel26.faunaandorchestra.entity.ModEntities;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class PhantomNoteProjectileEntity extends AbstractHurtingProjectile {
    public PhantomNoteProjectileEntity(EntityType<? extends AbstractHurtingProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public PhantomNoteProjectileEntity(LivingEntity owner, Vec3 movement, Level level) {
        super(ModEntities.PHANTOM_NOTE_PROJECTILE.get(), owner, movement, level);
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        this.discard();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (this.level() instanceof ServerLevel serverlevel) {
            Entity entity1 = result.getEntity();
            Entity owner = this.getOwner();
            DamageSource source = this.damageSources().magic();
            entity1.hurt(source, 6.0F);
            EnchantmentHelper.doPostAttackEffects(serverlevel, entity1, source);
        }
    }

    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.SOUL_FIRE_FLAME;
    }

    @Override
    protected boolean shouldBurn() {
        return false;
    }
}

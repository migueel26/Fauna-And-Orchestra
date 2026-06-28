package net.migueel26.faunaandorchestra.entity.custom.projectile;

import net.migueel26.faunaandorchestra.entity.ModEntities;
import net.migueel26.faunaandorchestra.entity.custom.ConductorEntity;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class PhantomNoteProjectileEntity extends AbstractHurtingProjectile {
    public static final int MAX_LIFE = 70;
    protected int lifetime = 0;
    private static final EntityDataAccessor<Integer> TEXTURE_INDEX = SynchedEntityData.defineId(PhantomNoteProjectileEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> GOOD = SynchedEntityData.defineId(PhantomNoteProjectileEntity.class, EntityDataSerializers.BOOLEAN);

    public PhantomNoteProjectileEntity(EntityType<? extends AbstractHurtingProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public PhantomNoteProjectileEntity(LivingEntity owner, Vec3 movement, Level level) {
        super(ModEntities.PHANTOM_NOTE_PROJECTILE.get(), owner, movement.x, movement.y, movement.z, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(TEXTURE_INDEX, this.random.nextInt(8));
        this.entityData.define(GOOD, false);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    @Override
    public void tick() {
        if (lifetime >= MAX_LIFE) {
            this.discard();
        } else {
            lifetime++;
        }

        super.tick();
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
            if (!this.isBad() && this.getOwner() instanceof ConductorEntity && result.getEntity() instanceof Monster) {
                Entity entity1 = result.getEntity();
                Entity owner = this.getOwner();
                DamageSource source = this.damageSources().magic();
                entity1.hurt(source, 6.0F);
                if (entity1 instanceof LivingEntity livingTarget) {
                    EnchantmentHelper.doPostHurtEffects(livingTarget, owner);
                }
            }
        }
    }

    protected ParticleOptions getTrailParticle() {
        return isBad() ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME;
    }

    @Override
    protected boolean shouldBurn() {
        return false;
    }

    public boolean isBad() {
        return !entityData.get(GOOD);
    }

    public int getTextureIndex() {
        return this.entityData.get(TEXTURE_INDEX);
    }

    public void setTextureIndex(int index) {
        this.entityData.set(TEXTURE_INDEX, index);
    }

    public void setGood(boolean good) {
        this.entityData.set(GOOD, good);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("TextureIndex")) {
            this.setTextureIndex(tag.getInt("TextureIndex"));
        }
        if (tag.contains("Good")) {
            this.entityData.set(GOOD, tag.getBoolean("Good"));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("TextureIndex", this.getTextureIndex());
        tag.putBoolean("Good", this.entityData.get(GOOD));
    }
}

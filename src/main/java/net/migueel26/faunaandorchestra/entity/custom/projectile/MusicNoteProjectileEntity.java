package net.migueel26.faunaandorchestra.entity.custom.projectile;

import net.migueel26.faunaandorchestra.entity.ModEntities;
import net.migueel26.faunaandorchestra.entity.custom.boss.TheGreatComposer;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

public class MusicNoteProjectileEntity extends AbstractHurtingProjectile {
    protected float inertia = 0.7f;
    public MusicNoteProjectileEntity(EntityType<? extends AbstractHurtingProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public MusicNoteProjectileEntity(LivingEntity owner, Vec3 movement, Level level) {
        super(ModEntities.MUSIC_NOTE_PROJECTILE.get(), owner, movement.x, movement.y, movement.z, level);
    }

    @Override
    protected void onHit(HitResult result) {
        boolean isComposer = result instanceof EntityHitResult hitResult && hitResult.getEntity() instanceof TheGreatComposer;
        if (isComposer) {
            ((TheGreatComposer) ((EntityHitResult) result).getEntity()).decreaseRepels();
        }

        // We decrease the repels before calling super to avoid inconsistency between onHit and onHitEntity
        super.onHit(result);

        if (!this.level().isClientSide) {
            if (!isComposer) {
                boolean flag = ForgeEventFactory.getMobGriefingEvent(this.level(), this.getOwner());
                this.level().explode(this, this.getX(), this.getY(), this.getZ(), (float)1, flag, Level.ExplosionInteraction.MOB);

                Iterator<BlockPos> iterator = BlockPos.betweenClosed(new BlockPos(this.getBlockX() + 2, this.getBlockY() + 1, this.getBlockZ() + 2),
                        new BlockPos(this.getBlockX() - 2, this.getBlockY() - 1, this.getBlockZ() - 2)).iterator();

                while (iterator.hasNext()) {
                    BlockPos blockPos = iterator.next();
                    if (level().getBlockState(blockPos).is(Blocks.FIRE)) {
                        level().setBlock(blockPos, Blocks.SOUL_FIRE.defaultBlockState(), 3);
                    }
                }

                this.discard();
            }
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        Entity entity = result.getEntity();
        if (entity instanceof TheGreatComposer greatComposer) {
            if (greatComposer.getRepels() > 0) {
                greatComposer.composerController.transitionLength(1);
                greatComposer.trigger("repel", true);
                level().playSound(null, blockPosition(), ModSounds.REPEL.get(), SoundSource.NEUTRAL);
                if (this.getOwner() != null) {
                    this.deflectToOwner(greatComposer, this.getOwner());
                    this.inertia += 0.04f;

                    this.level().playSound(null, this.blockPosition(), SoundEvents.VEX_HURT, SoundSource.NEUTRAL, 2.0f, 1.0f);
                    this.setOwner(greatComposer);
                }
            } else {
                this.discard();
            }


        } else {
            if (this.level() instanceof ServerLevel serverlevel) {
                Entity entity1 = result.getEntity();
                Entity owner = this.getOwner();
                DamageSource source = this.damageSources().magic();
                entity1.hurt(source, 8.0F);
                if (entity1 instanceof LivingEntity livingTarget) {
                    EnchantmentHelper.doPostHurtEffects(livingTarget, owner);
                }
            }
        }
    }

    private void deflectToOwner(Entity deflector, Entity targetOwner) {
        double dX = targetOwner.getX() - this.getX();
        double dY = targetOwner.getBoundingBox().getCenter().y - this.getY();
        double dZ = targetOwner.getZ() - this.getZ();

        double distance = Math.sqrt(dX * dX + dY * dY + dZ * dZ);

        if (distance != 0) {
            dX /= distance;
            dY /= distance;
            dZ /= distance;
        }

        double speed = 0.1D + (this.inertia * 0.05D);

        this.xPower = dX * speed;
        this.yPower = dY * speed;
        this.zPower = dZ * speed;

        this.setDeltaMovement(this.getDeltaMovement().reverse());

        this.hasImpulse = true;
    }

    @Override
    protected boolean shouldBurn() {
        return false;
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    protected @NotNull ParticleOptions getTrailParticle() {
        return ParticleTypes.SOUL_FIRE_FLAME;
    }

    @Override
    protected float getInertia() {
        return inertia;
    }

}

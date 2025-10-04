package net.migueel26.faunaandorchestra.entity.custom.projectile;

import net.migueel26.faunaandorchestra.entity.ModEntities;
import net.migueel26.faunaandorchestra.entity.custom.boss.TheGreatComposer;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
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
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

public class MusicNoteProjectileEntity extends AbstractHurtingProjectile {
    protected float inertia = 0.7f;
    public MusicNoteProjectileEntity(EntityType<? extends AbstractHurtingProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public MusicNoteProjectileEntity(LivingEntity owner, Vec3 movement, Level level) {
        super(ModEntities.MUSIC_NOTE_PROJECTILE.get(), owner, movement, level);
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
                boolean flag = net.neoforged.neoforge.event.EventHooks.canEntityGrief(this.level(), this.getOwner());
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
                if (this.getOwner() != null) {

                    /*
                    Vec3 vec3 = greatComposer.getViewVector(1.0F);
                    double d2 = getOwner().getX() - (greatComposer.getX() + vec3.x * 4.0);
                    double d3 = getOwner().getY(1) - (0.5 + greatComposer.getY(0.5));
                    double d4 = getOwner().getZ() - (greatComposer.getZ() + vec3.z * 4.0);
                    Vec3 vec31 = new Vec3(d2, d3, d4);

                    this.setPos(greatComposer.getX() + vec3.x * 1.25, greatComposer.getY(0.5), this.getZ() + vec3.z * 1.25);
                    this.setDeltaMovement(vec31.normalize());

                    greatComposer.getLookControl().setLookAt(getOwner().getX(), getOwner().getY(), getOwner().getZ());
                    */

                    this.deflect(ProjectileDeflection.REVERSE, this, greatComposer, false);
                    this.inertia += 0.06f;
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
                EnchantmentHelper.doPostAttackEffects(serverlevel, entity1, source);
            }
        }
    }

    @Override
    protected boolean shouldBurn() {
        return false;
    }

    @Nullable
    @Override
    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.SOUL_FIRE_FLAME;
    }

    @Override
    protected float getInertia() {
        return inertia;
    }

}

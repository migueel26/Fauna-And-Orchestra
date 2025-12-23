package net.migueel26.faunaandorchestra.entity.custom.boss;

import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ComposerCanonEntity extends Monster implements GeoEntity {
    int ticks = 0;
    public static final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    public static final RawAnimation WALK = RawAnimation.begin().thenPlay("walk_fast");
    public static final RawAnimation SPAWN = RawAnimation.begin().thenPlay("spawn");
    public static final RawAnimation ATTACK = RawAnimation.begin().thenPlay("attack_fast");
    public final AnimationController<ComposerCanonEntity> canonController = new AnimationController<>(this, "composer_canon_controller", 5, this::composerCanonState)
            .triggerableAnim("canon_attack", ATTACK)
            .triggerableAnim("canon_spawn", SPAWN);
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    public ComposerCanonEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    private <E extends GeoAnimatable> PlayState composerCanonState(AnimationState<E> state) {
        if (state.isMoving()) {
            state.setAnimation(WALK);
        } else {
            state.setAnimation(IDLE);
        }
        return PlayState.CONTINUE;
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new MeleeAttackGoal(this, 1.0f, true) {
            @Override
            protected void checkAndPerformAttack(LivingEntity target, double distToEnemySqr) {
                double reach = this.getAttackReachSqr(target);

                if (distToEnemySqr <= reach && this.isTimeToAttack()) {
                    ((ComposerCanonEntity) this.mob).triggerAnim("composer_canon_controller", "canon_attack");
                    this.resetAttackCooldown();
                    this.mob.doHurtTarget(target);
                    level().playSound(null, blockPosition(), ModSounds.CANON_ATTACK.get(), SoundSource.NEUTRAL);
                }
            }
        });
        goalSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, false, true) {
            @Override
            public boolean canUse() {
                return ((ComposerCanonEntity)mob).canAttack() && super.canUse();
            }
        });
        goalSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Animal.class, false, true) {
            @Override
            public boolean canUse() {
                return ((ComposerCanonEntity)mob).canAttack() && super.canUse();
            }
        });
    }

    @Override
    public void tick() {
        if (ticks == 1) {
            triggerAnim("composer_canon_controller", "canon_spawn");
            level().playSound(null, blockPosition(), ModSounds.CANON_SPAWN.get(), SoundSource.NEUTRAL);
        }
        if (ticks % 10 == 0) {
            if (!level().isClientSide()) {
                ((ServerLevel) level()).sendParticles(ParticleTypes.SMOKE, getX(), getY(), getZ(), 20, 0.1f, 0.2f, 0.1f, 0.05);
            }
        }
        ticks++;
        super.tick();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            boolean flag = source.getDirectEntity() instanceof ThrownPotion;
            if (!source.is(DamageTypeTags.IS_PROJECTILE) && !flag) {
                boolean flag2 = super.hurt(source, amount);
                if (!this.level().isClientSide() && (source.getEntity() instanceof LivingEntity)) {
                    this.teleport();
                }

                return flag2;
            } else {
                for (int i = 0; i < 64; i++) {
                    if (this.teleport()) {
                        return true;
                    }
                }

                return flag;
            }
        }
    }

    protected boolean teleport() {
        if (!this.level().isClientSide() && this.isAlive()) {
            double d0 = this.getX() + (this.random.nextDouble() - 0.5) * 32.0;
            double d1 = this.getY() + 1;
            double d2 = this.getZ() + (this.random.nextDouble() - 0.5) * 32.0;
            return this.teleport(d0, d1, d2);
        } else {
            return false;
        }
    }

    private boolean teleport(double x, double y, double z) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(x, y, z);

        while (blockpos$mutableblockpos.getY() > this.level().getMinBuildHeight() && !this.level().getBlockState(blockpos$mutableblockpos).blocksMotion()) {
            blockpos$mutableblockpos.move(Direction.DOWN);
        }

        BlockState blockstate = this.level().getBlockState(blockpos$mutableblockpos);
        boolean flag = blockstate.blocksMotion();
        boolean flag1 = blockstate.getFluidState().is(FluidTags.WATER);
        if (flag && !flag1) {
            EntityTeleportEvent.EnderEntity event = ForgeEventFactory.onEnderTeleport(this, x, y, z);
            if (event.isCanceled()) return false;
            Vec3 vec3 = this.position();
            boolean flag2 = this.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true);
            if (flag2) {
                this.level().gameEvent(GameEvent.TELEPORT, vec3, GameEvent.Context.of(this));
                if (!this.isSilent()) {
                    // TODO: CHANGE SOUND
                    this.level().playSound(null, this.xo, this.yo, this.zo, SoundEvents.ENDERMAN_TELEPORT, this.getSoundSource(), 1.0F, 1.0F);
                    this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
                }
            }

            return flag2;
        } else {
            return false;
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 35d)
                .add(Attributes.MOVEMENT_SPEED, 0.45D)
                .add(Attributes.FOLLOW_RANGE, 24D)
                .add(Attributes.ATTACK_DAMAGE, 12.0);
    }

    public boolean canAttack() {
        return ticks >= 40;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSounds.CANON_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.CANON_DEATH.get();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(canonController);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
}

package net.migueel26.faunaandorchestra.entity.custom;

import net.migueel26.faunaandorchestra.entity.goals.FaunaRandomLookAroundGoal;
import net.migueel26.faunaandorchestra.particles.ModParticleTypes;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class LivingMusicEntity extends AgeableMob implements GeoEntity {
    protected static final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    protected static final RawAnimation WALK = RawAnimation.begin().thenPlay("walk");
    protected static final EntityDataAccessor<Integer> TICKS_UNTIL_DEATH = SynchedEntityData.defineId(LivingMusicEntity.class, EntityDataSerializers.INT);
    /////
    public static final int BASE_TICKS_UNTIL_DEATH = 80;
    private final AnimationController<LivingMusicEntity> livingMusicController = new AnimationController<>(this, "living_music_controller", 5, this::livingMusicState);
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    public LivingMusicEntity(EntityType<? extends AgeableMob> entityType, Level level) {
        super(entityType, level);

        addOverridenGoals();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        entityData.define(TICKS_UNTIL_DEATH, BASE_TICKS_UNTIL_DEATH);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new RunAwayGoal(this, 1.4D));
        // LookAtPlayerGoal (2)
        goalSelector.addGoal(3, new FaunaRandomLookAroundGoal(this));
    }
    private void addOverridenGoals() {
        goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 8.0F) {
            @Override
            public boolean canUse() {
                return super.canUse() && ((LivingMusicEntity) mob).isTrapped();
            }
        });
    }

    private <E extends GeoAnimatable> PlayState livingMusicState(AnimationState<E> state) {
        if (state.isMoving()) {
            state.getController().setAnimation(WALK);
        } else {
            state.getController().setAnimation(IDLE);
        }
        return PlayState.CONTINUE;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 8.0d)
                .add(Attributes.MOVEMENT_SPEED, 0.5D)
                .add(Attributes.FOLLOW_RANGE, 24D);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(livingMusicController);
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.RABBIT_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.AXOLOTL_DEATH;
    }

    @Override
    public void tick() {
        if (!level().isClientSide() && !isTrapped()) {
            if (getTicksUntilDeath() == 0) {
                ((ServerLevel) level()).sendParticles(ParticleTypes.POOF, getX(), getY(), getZ(), 30, 0.1, 0.5, 0.1, 0.3);
                this.remove(Entity.RemovalReason.DISCARDED);
            } else {
                if (getTicksUntilDeath() % 20 == 0) {
                    level().playSound(null, blockPosition(), SoundEvents.NOTE_BLOCK_PLING.value(), SoundSource.NEUTRAL, 0.5F, 1.0F + (getRandom().nextFloat() - 0.5F));
                    ((ServerLevel) level()).sendParticles(ModParticleTypes.MAGICAL_NOTE.get(), getX(), getY() + 0.5, getZ(), 5, 0.1, 0.1, 0.1, 0.2);
                }

                setTicksUntilDeath(getTicksUntilDeath() - 1);
            }
        }
        super.tick();
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        if (compound.contains("TicksUntilDeath")) {
            setTicksUntilDeath(compound.getInt("TicksUntilDeath"));
        }
        super.readAdditionalSaveData(compound);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("TicksUntilDeath", getTicksUntilDeath());
        super.addAdditionalSaveData(compound);
    }

    @Override
    public boolean isPersistenceRequired() {
        return true;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    public int getTicksUntilDeath() {
        return entityData.get(TICKS_UNTIL_DEATH);
    }

    public void setTicksUntilDeath(int ticks) {
        entityData.set(TICKS_UNTIL_DEATH, ticks);
    }

    public boolean isTrapped() {
        return getTicksUntilDeath() == -1;
    }

    private static class RunAwayGoal extends Goal {
        private final LivingMusicEntity music;
        private final double speedModifier;
        private double posX;
        private double posY;
        private double posZ;

        public RunAwayGoal(LivingMusicEntity music, double speedModifier) {
            this.music = music;
            this.speedModifier = speedModifier;

            this.setFlags(java.util.EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (this.music.isTrapped()) {
                return false;
            }

            if (this.music.getNavigation().isInProgress()) {
                return true;
            }

            Vec3 targetPos = net.minecraft.world.entity.ai.util.DefaultRandomPos.getPos(this.music, 10, 4);
            if (targetPos == null) {
                return false;
            }

            this.posX = targetPos.x;
            this.posY = targetPos.y;
            this.posZ = targetPos.z;

            return true;
        }

        @Override
        public void start() {
            this.music.getNavigation().moveTo(this.posX, this.posY, this.posZ, this.speedModifier);
        }

        @Override
        public boolean canContinueToUse() {
            return !this.music.isTrapped() && !this.music.getNavigation().isDone();
        }
    }
}

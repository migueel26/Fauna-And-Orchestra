package net.migueel26.faunaandorchestra.entity.custom;

import net.migueel26.faunaandorchestra.entity.goals.ConductorEntityConductingOrchestraGoal;
import net.migueel26.faunaandorchestra.entity.goals.FaunaRandomLookAroundGoal;
import net.migueel26.faunaandorchestra.entity.goals.QuirkyFrogConductingChoirGoal;
import net.migueel26.faunaandorchestra.entity.goals.QuirkyFrogSingGoal;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.JumpControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.*;

public class QuirkyFrogEntity extends ConductorEntity implements GeoEntity {
    protected static final RawAnimation WALK = RawAnimation.begin().thenPlay("walk");
    protected static final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    protected static final RawAnimation CROAC = RawAnimation.begin().thenPlay("croac");
    protected static final RawAnimation CONDUCTING = RawAnimation.begin().thenPlay("conducting");
    protected static final RawAnimation HOLDING_BATON = RawAnimation.begin().thenPlay("holding_baton");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    // Server Variables
    private List<QuirkyFrogEntity> frogChoir = new ArrayList<>();
    private boolean isSinging;
    private QuirkyFrogEntity frogConductor;
    private int jumpTicks;
    private int jumpDuration;
    private boolean wasOnGround;
    private int jumpDelayTicks;
    private int animDelayTicks;
    private final AnimationController<QuirkyFrogEntity> quirkyFrogController = new AnimationController<>(this, "quirky_frog_controller", 5, this::quirkyFrogState)
            .triggerableAnim("jump", WALK);
    private final AnimationController<QuirkyFrogEntity> quirkyFrogCroacController = new AnimationController<>(this, "quirky_frog_croac_controller", 3, this::quirkyFrogCroacState)
            .triggerableAnim("croac", CROAC);

    public QuirkyFrogEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
        this.jumpControl = new QuirkyFrogJumpControl(this);
        this.moveControl = new QuirkyFrogMoveControl(this);
        this.isSinging = false;
        this.frogConductor = null;

        addOverridenGoals();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 15d)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FOLLOW_RANGE, 24D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new TamableAnimalPanicGoal(2.0D));
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new ConductorEntityConductingOrchestraGoal(this));
        this.goalSelector.addGoal(1, new QuirkyFrogSingGoal(this));
        this.goalSelector.addGoal(1, new QuirkyFrogConductingChoirGoal(this));
        // LookAtPlayerGoal(2);
        this.goalSelector.addGoal(3, new SitWhenOrderedToGoal(this));
        // RandomStrollGoal(4)
        this.goalSelector.addGoal(5, new FaunaRandomLookAroundGoal(this));
    }

    private void addOverridenGoals() {
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 6.0F) {
            private int lookTime;

            @Override
            public boolean canUse() {
                return super.canUse() && !((ConductorEntity) this.mob).isConducting()
                        && !((QuirkyFrogEntity) this.mob).isSinging();
            }

            @Override
            public boolean canContinueToUse() {
                if (!this.lookAt.isAlive() || ((ConductorEntity) this.mob).isConducting()) {
                    return false;
                } else {
                    return this.mob.distanceToSqr(this.lookAt) > (double)(this.lookDistance * this.lookDistance) ? false : this.lookTime > 0;
                }
            }

            @Override
            public void start() {
                this.lookTime = this.adjustedTickDelay(40 + this.mob.getRandom().nextInt(40));
            }

            @Override
            public void tick() {
                if (this.lookAt.isAlive()) {
                    double eyeOffset = ((ConductorEntity) mob).isHoldingBaton() ? 1F : 2F;
                    double d0 = this.lookAt.getEyeY() - eyeOffset;
                    this.mob.getLookControl().setLookAt(this.lookAt.getX(), d0, this.lookAt.getZ());
                    this.lookTime--;
                }
            }
        });

        this.goalSelector.addGoal(4, new RandomStrollGoal(this, 1.0) {
            private int countdown;
            @Override
            public boolean canUse() {
                return super.canUse() && !((QuirkyFrogEntity) this.mob).isReady() && !((QuirkyFrogEntity) this.mob).isConducting()
                        && ((QuirkyFrogEntity) this.mob).getFrogConductor() == null;
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && !((QuirkyFrogEntity) this.mob).isSinging() && !((QuirkyFrogEntity) this.mob).isConducting();
            }

            @Override
            public void start() {
                countdown = 0;
                super.start();
            }

            @Override
            public void tick() {
                countdown++;
                if (countdown == 140) {
                    Vec3 pos = this.mob.position();
                    double xdif = pos.x - this.mob.getBlockX();
                    double zdif = pos.z - this.mob.getBlockZ();
                    if (Math.abs(xdif) > Math.abs(zdif)) {
                        this.mob.moveTo(this.mob.getX() + xdif, this.mob.getY() + 1.0F, this.mob.getZ());
                    } else {
                        this.mob.moveTo(this.mob.getX(), this.mob.getY() + 1.0F, this.mob.getZ() + zdif);
                    }
                    countdown = 0;
                }
            }
        });
    }
    protected <E extends GeoAnimatable> PlayState quirkyFrogCroacState(AnimationState<E> state) {
        return PlayState.STOP;
    }
    protected <E extends GeoAnimatable> PlayState quirkyFrogState(AnimationState<E> state) {
        if (isConducting()) {
            state.getController().setAnimation(CONDUCTING);
        } else if (isHoldingBaton()) {
            state.getController().setAnimation(HOLDING_BATON);
        //} else if (state.isMoving()) {
            //state.getController().setAnimation(WALK);
        } else {
            state.getController().setAnimation(IDLE);
        }
        return PlayState.CONTINUE;
    }

    /////// JUMPING  (from Rabbit class)
    @Override
    protected float getJumpPower() {
        float f = 0.3F;
        if (this.horizontalCollision || this.moveControl.hasWanted() && this.moveControl.getWantedY() > this.getY() + 0.5) {
            f = 0.5F;
        }

        Path path = this.navigation.getPath();
        if (path != null && !path.isDone()) {
            Vec3 vec3 = path.getNextEntityPos(this);
            if (vec3.y > this.getY() + 0.5) {
                f = 0.5F;
            }
        }

        if (this.moveControl.getSpeedModifier() <= 0.6) {
            f = 0.2F;
        }

        return super.getJumpPower(f / 0.42F);
    }

    @Override
    public void jumpFromGround() {
        super.jumpFromGround();
        double d0 = this.moveControl.getSpeedModifier();
        if (d0 > 0.0) {
            double d1 = this.getDeltaMovement().horizontalDistanceSqr();
            if (d1 < 0.01) {
                this.moveRelative(0.1F, new Vec3(0.0, 0.0, 1.0));
            }
        }

        if (!this.level().isClientSide) {
            this.level().broadcastEntityEvent(this, (byte)1);
        }
    }

    public float getJumpCompletion(float partialTick) {
        return this.jumpDuration == 0 ? 0.0F : ((float)this.jumpTicks + partialTick) / (float)this.jumpDuration;
    }

    public void setSpeedModifier(double speedModifier) {
        this.getNavigation().setSpeedModifier(speedModifier);
        this.moveControl.setWantedPosition(this.moveControl.getWantedX(), this.moveControl.getWantedY(), this.moveControl.getWantedZ(), speedModifier);
    }

    @Override
    public void setJumping(boolean jumping) {
        super.setJumping(jumping);
        if (jumping) {
            this.playSound(this.getJumpSound(), this.getSoundVolume(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * 0.8F);
        }
    }

    public void startJumping() {
        this.setJumping(true);
        this.jumpDuration = 15;
        this.jumpTicks = 0;
        this.animDelayTicks = 10;
    }

    @Override
    public void customServerAiStep() {
        if (this.jumpDelayTicks > 0) {
            this.jumpDelayTicks--;
        }

        if (this.onGround()) {
            if (!this.wasOnGround) {
                this.setJumping(false);
                this.checkLandingDelay();
            }

            QuirkyFrogJumpControl quirkyfrog$quirkyfrogjumpcontrol = (QuirkyFrogJumpControl) this.jumpControl;
            if (!quirkyfrog$quirkyfrogjumpcontrol.wantJump()) {
                if (this.moveControl.hasWanted() && this.jumpDelayTicks == 0) {
                    triggerAnim("quirky_frog_controller", "jump");
                    if (animDelayTicks > 0) {
                        animDelayTicks--;
                    }
                    if (animDelayTicks == 0) {
                        Path path = this.navigation.getPath();
                        Vec3 vec3 = new Vec3(this.moveControl.getWantedX(), this.moveControl.getWantedY(), this.moveControl.getWantedZ());
                        if (path != null && !path.isDone()) {
                            vec3 = path.getNextEntityPos(this);
                        }

                        this.facePoint(vec3.x, vec3.z);
                        this.startJumping();
                    }
                }
            } else if (!quirkyfrog$quirkyfrogjumpcontrol.canJump()) {
                this.enableJumpControl();
            }
        }

        this.wasOnGround = this.onGround();
    }

    private void facePoint(double x, double z) {
        this.setYRot((float)(Mth.atan2(z - this.getZ(), x - this.getX()) * 180.0F / (float)Math.PI) - 90.0F);
    }

    private void enableJumpControl() {
        ((QuirkyFrogJumpControl)this.jumpControl).setCanJump(true);
    }

    private void disableJumpControl() {
        ((QuirkyFrogJumpControl)this.jumpControl).setCanJump(false);
    }

    private void setLandingDelay() {
        if (this.moveControl.getSpeedModifier() < 2.2) {
            this.jumpDelayTicks = 5;
        } else {
            this.jumpDelayTicks = 1;
        }
    }

    private void checkLandingDelay() {
        this.setLandingDelay();
        this.disableJumpControl();
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.jumpTicks != this.jumpDuration) {
            this.jumpTicks++;
        } else if (this.jumpDuration != 0) {
            this.jumpTicks = 0;
            this.jumpDuration = 0;
            this.setJumping(false);
        }
    }

    /////// JUMPING (from Rabbit Class)

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return isConducting() ? null : SoundEvents.FROG_AMBIENT;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.FROG_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.FROG_DEATH;
    }

    @Nullable
    protected SoundEvent getJumpSound() {
        return SoundEvents.FROG_LONG_JUMP;
    }

    public static boolean checkFrogSpawnRules(
            EntityType<? extends Animal> animal, LevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random
    ) {
        return level.getBlockState(pos.below()).is(BlockTags.FROGS_SPAWNABLE_ON) && isBrightEnoughToSpawn(level, pos);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (isMusical() && player.getMainHandItem().is(ModItems.BATON) && !isTame()) {
            this.tame(player);
            this.playSound(ModSounds.SUCCESSFUL_TAME.get());
            this.level().broadcastEntityEvent(this, (byte) 7);
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    public static class QuirkyFrogJumpControl extends JumpControl {
        private final QuirkyFrogEntity quirkyFrog;
        private boolean canJump;

        public QuirkyFrogJumpControl(QuirkyFrogEntity quirkyFrog) {
            super(quirkyFrog);
            this.quirkyFrog = quirkyFrog;
        }

        public boolean wantJump() {
            return this.jump;
        }

        public boolean canJump() {
            return this.canJump;
        }

        public void setCanJump(boolean canJump) {
            this.canJump = canJump;
        }

        @Override
        public void tick() {
            if (this.jump) {
                this.quirkyFrog.startJumping();
                this.jump = false;
            }
        }
    }

    static class QuirkyFrogMoveControl extends MoveControl {
        private final QuirkyFrogEntity quirkyFrog;
        private double nextJumpSpeed;

        public QuirkyFrogMoveControl(QuirkyFrogEntity quirkyFrog) {
            super(quirkyFrog);
            this.quirkyFrog = quirkyFrog;
        }

        @Override
        public void tick() {
            if (this.quirkyFrog.onGround() && !this.quirkyFrog.jumping && !((QuirkyFrogJumpControl)this.quirkyFrog.jumpControl).wantJump()) {
                this.quirkyFrog.setSpeedModifier(0.0);
            } else if (this.hasWanted()) {
                this.quirkyFrog.setSpeedModifier(this.nextJumpSpeed);
            }

            super.tick();
        }

        /**
         * Sets the speed and location to move to
         */
        @Override
        public void setWantedPosition(double x, double y, double z, double speed) {
            if (this.quirkyFrog.isInWater()) {
                speed = 1.5;
            }

            super.setWantedPosition(x, y, z, speed);
            if (speed > 0.0) {
                this.nextJumpSpeed = speed;
            }
        }
    }

    public List<QuirkyFrogEntity> getFrogChoir() {
        return frogChoir;
    }

    public void setFrogChoir(List<QuirkyFrogEntity> frogChoir) {
        this.frogChoir = frogChoir;
        setReady(true);
    }

    public boolean isFrogChoirEmpty() {
        return frogChoir.isEmpty();
    }

    public boolean isAptForChoir() {
        return !isTame() && isFrogChoirEmpty() && !isSinging();
    }

    public boolean isSinging() {
        return isSinging;
    }

    public void setSinging(boolean singing) {
        isSinging = singing;
    }

    public QuirkyFrogEntity getFrogConductor() {
        return frogConductor;
    }

    public void setFrogConductor(QuirkyFrogEntity frogConductor) {
        this.frogConductor = frogConductor;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(quirkyFrogController);
        controllers.add(quirkyFrogCroacController);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}

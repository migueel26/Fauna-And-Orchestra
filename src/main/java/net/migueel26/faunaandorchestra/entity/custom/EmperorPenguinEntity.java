package net.migueel26.faunaandorchestra.entity.custom;

import net.migueel26.faunaandorchestra.entity.ModEntities;
import net.migueel26.faunaandorchestra.entity.goals.FaunaRandomLookAroundGoal;
import net.migueel26.faunaandorchestra.entity.goals.MusicalEntityPlayingInstrumentGoal;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;

public class EmperorPenguinEntity extends MusicalEntity implements NeutralMob {
    protected static final RawAnimation RUN = RawAnimation.begin().thenPlay("run");
    protected static final RawAnimation WADDLE = RawAnimation.begin().thenPlay("waddle");
    protected static final RawAnimation WADDLE_FLUTE = RawAnimation.begin().thenPlay("waddle_flute");
    protected static final RawAnimation WAVE = RawAnimation.begin().thenPlay("wave");
    protected static final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    protected static final RawAnimation IDLE_FLUTE = RawAnimation.begin().thenPlay("holding_flute");
    protected static final RawAnimation PLAYING = RawAnimation.begin().thenPlay("playing");
    protected static final RawAnimation ATTACK = RawAnimation.begin().thenPlay("attack");
    private static final EntityDataAccessor<Integer> REMAINING_ANGER_TIME = SynchedEntityData.defineId(EmperorPenguinEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IS_RUNNING = SynchedEntityData.defineId(EmperorPenguinEntity.class, EntityDataSerializers.BOOLEAN);
    private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
    @Nullable
    private UUID persistentAngerTarget;
    private boolean isRunning = false;
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private final AnimationController<EmperorPenguinEntity> penguinController = new AnimationController<>(this, "emperor_penguin_controller", 5, this::penguinState)
            .triggerableAnim("wave", WAVE)
            .triggerableAnim("attack", ATTACK);
    public EmperorPenguinEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);

        addOverriddenGoals();
    }

    @Override
    public DeferredItem<Item> getInstrument() {
        return ModItems.FLUTE;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(IS_RUNNING, false);
        builder.define(REMAINING_ANGER_TIME, 0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        // TamableAnimalPanicGoal(0)
        this.goalSelector.addGoal(1, new MusicalEntityPlayingInstrumentGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new HurtByTargetGoal(this).setAlertOthers());
        // NearestAttackableTargetGoal (4)
        // LookAtPlayerGoal (4)
        // MeleeAttackGoal (5)
        // RandomStrollGoal(5)
        this.goalSelector.addGoal(6, new FaunaRandomLookAroundGoal(this));
        this.goalSelector.addGoal(8, new ResetUniversalAngerTargetGoal<>(this, true));
    }

    private void addOverriddenGoals() {
        this.goalSelector.addGoal(0, new TamableAnimalPanicGoal(2.0D, DamageTypeTags.PANIC_CAUSES) {
            final EmperorPenguinEntity penguin = (EmperorPenguinEntity) super.mob;

            @Override
            public boolean canUse() {
                return super.canUse() && !penguin.isAngry();
            }

            @Override
            public void start() {
                penguin.setRunning(true);
                super.start();
            }

            @Override
            public void stop() {
                penguin.setRunning(false);
                super.stop();
            }
        });

        this.goalSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, false, true, this::isAngryAt) {
            final EmperorPenguinEntity penguin = (EmperorPenguinEntity) super.mob;
            @Override
            public void start() {
                penguin.setRunning(true);
                super.start();
            }

            @Override
            public void stop() {
                penguin.setRunning(false);
                super.stop();
            }
        });

        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 6.0F) {
            final EmperorPenguinEntity penguin = (EmperorPenguinEntity) super.mob;
            private boolean hasWaved = false;
            @Override
            public void start() {
                penguin.getNavigation().moveTo(penguin.getX(), penguin.getY(), penguin.getZ(), 1.0D);
                this.mob.getLookControl().setLookAt(this.lookAt.getX(), this.lookAt.getEyeY(), this.lookAt.getZ());
                if (!hasWaved) {
                    penguin.wave();
                    this.hasWaved = true;
                }
                super.start();
            }

            @Override
            public boolean canUse() {
                return super.canUse() && penguin.getNavigation().isDone();
            }

            @Override
            public void tick() {
                super.tick();
            }
        });

        this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.25D, false) {
            @Override
            protected void checkAndPerformAttack(LivingEntity target) {
                if (this.canPerformAttack(target)) {
                    ((EmperorPenguinEntity) this.mob).attack();
                    mob.playSound(SoundEvents.PARROT_EAT, 1.5F, 1.25F);

                    this.resetAttackCooldown();
                    this.mob.doHurtTarget(target);

                }
            }
        });

        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 1.0) {
            final EmperorPenguinEntity penguin = (EmperorPenguinEntity) super.mob;
            @Override
            public boolean canUse() {
                return super.canUse() && !penguin.isWaving();
            }
        });
    }

    private <E extends GeoAnimatable> PlayState penguinState(AnimationState<E> state) {
        if (isPlayingInstrument()) {
            state.getController().setAnimation(PLAYING);
        } else if (state.isMoving() && isRunning()) {
            state.getController().setAnimation(RUN);
        } else if (isHoldingInstrument()) {
            state.getController().setAnimation(IDLE_FLUTE);
        } else if (state.isMoving()) {
            state.getController().setAnimation(isHoldingInstrument() ? WADDLE_FLUTE : WADDLE);
        } else {
            state.getController().setAnimation(IDLE);
        }
        return PlayState.CONTINUE;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 25d)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FOLLOW_RANGE, 24D)
                .add(Attributes.ATTACK_DAMAGE, 4.0);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (key.equals(IS_RUNNING)) {
            this.isRunning = entityData.get(IS_RUNNING);
        }
        super.onSyncedDataUpdated(key);
    }

    @Override
    public void tryToTame(Player player) {

    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return isPlayingInstrument() ? null : ModSounds.BABY_PENGUIN_AMBIENT.get();
    }

    @Override
    public void playAmbientSound() {
        if (this.getAmbientSound() != null) {
            this.playSound(getAmbientSound(), 0.3f, 0.5f);
        }
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PARROT_DEATH;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.PANDA_HURT;
    }


    @Override
    protected void playStepSound(BlockPos pos, BlockState block) {
        this.playSound(SoundEvents.POLAR_BEAR_STEP, 0.15F, 1.0F);
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return isTame() && itemStack.is(Tags.Items.FOODS_RAW_FISH) || itemStack.is(Tags.Items.FOODS_COOKED_FISH);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return ModEntities.PENGUIN.get().create(level());
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (!this.level().isClientSide()) {
            this.updatePersistentAnger((ServerLevel)this.level(), true);
        }
    }

    //////////////////// NEUTRAL MOB METHODS -> ANGER ////////////////////////////////////

    @Override
    public int getRemainingPersistentAngerTime() {
        return this.entityData.get(REMAINING_ANGER_TIME);
    }

    @Override
    public void setRemainingPersistentAngerTime(int time) {
        this.entityData.set(REMAINING_ANGER_TIME, time);
    }

    @Nullable
    @Override
    public UUID getPersistentAngerTarget() {
        return this.persistentAngerTarget;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable UUID target) {
        this.persistentAngerTarget = target;
    }

    @Override
    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(this.random));
    }

    ///////////////////////////////////////////////////////////////

    public void attack() {
        triggerAnim("emperor_penguin_controller", "attack");
    }

    public void wave() {
        triggerAnim("emperor_penguin_controller", "wave");
    }

    public boolean isWaving() {
        return penguinController.isPlayingTriggeredAnimation();
    }

    public void setRunning(boolean flag) {
        entityData.set(IS_RUNNING, flag);
        this.isRunning = flag;
    }

    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(penguinController);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}

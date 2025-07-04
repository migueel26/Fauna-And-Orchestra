package net.migueel26.faunaandorchestra.entity.custom;

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
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
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
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;

public class MantisEntity extends MusicalEntity implements GeoEntity, NeutralMob {
    protected static final RawAnimation WALK = RawAnimation.begin().thenPlay("walk");
    protected static final RawAnimation WALK_VIOLIN = RawAnimation.begin().thenPlay("walk_violin");
    protected static final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    protected static final RawAnimation ATTACK = RawAnimation.begin().thenPlay("attack");
    protected static final RawAnimation PLAYING = RawAnimation.begin().thenPlay("playing");
    protected static final RawAnimation IDLE_VIOLIN = RawAnimation.begin().thenPlay("idle_violin");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private static final EntityDataAccessor<Integer> REMAINING_ANGER_TIME = SynchedEntityData.defineId(MantisEntity.class, EntityDataSerializers.INT);
    private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
    private final AnimationController MANTIS_CONTROLLER =
            new AnimationController<>(this, "mantis_controller", 5, this::mantisState)
                    .triggerableAnim("attack", ATTACK);
    @Nullable
    private UUID persistentAngerTarget;

    public MantisEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);

        addOverridenGoals();
    }

    @Override
    public DeferredItem<Item> getInstrument() {
        return ModItems.VIOLIN;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(0, new TamableAnimalPanicGoal(1.0, DamageTypeTags.PANIC_ENVIRONMENTAL_CAUSES));
        this.goalSelector.addGoal(1, new MusicalEntityPlayingInstrumentGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new HurtByTargetGoal(this).setAlertOthers());
        // NearestAttackableTargetGoal (4)
        // MeleeAttackGoal (5)
        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 1.0));
        // LookAtPlayerGoal (6)
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(8, new ResetUniversalAngerTargetGoal<>(this, true));
    }

    private void addOverridenGoals() {
        this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.25D, false) {
            @Override
            protected void checkAndPerformAttack(LivingEntity target) {
                if (this.canPerformAttack(target)) {
                    ((MantisEntity) this.mob).attack();
                    this.resetAttackCooldown();
                    this.mob.doHurtTarget(target);

                }
            }
        });

        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F) {
            @Override
            public boolean canUse() {
                return super.canUse() && !((MantisEntity) mob).isPlayingInstrument();
            }
        });

        this.goalSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Player.class, 10, false, true, this::isAngryAt) {
            @Override
            public void start() {
                super.start();
                this.mob.makeSound(ModSounds.MANTIS_ANGRY.get());
            }
        });
    }

    protected <E extends GeoAnimatable> PlayState mantisState(AnimationState<E> state) {
        if (state.isMoving()) {
            state.getController().transitionLength(5);
            state.getController().setAnimation(isHoldingInstrument() ? WALK_VIOLIN : WALK);
        } else if (isPlayingInstrument()) {
            state.getController().transitionLength(5);
            state.getController().setAnimation(PLAYING);
            state.getController().transitionLength(0);
        } else if (isHoldingInstrument()) {
            state.getController().transitionLength(0);
            state.getController().setAnimation(IDLE_VIOLIN);
        } else {
            state.getController().setAnimation(IDLE);
        }
        return PlayState.CONTINUE;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(REMAINING_ANGER_TIME, 0);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 15d)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FOLLOW_RANGE, 24D)
                .add(Attributes.ATTACK_DAMAGE, 4.0);
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (!this.level().isClientSide()) {
            this.updatePersistentAnger((ServerLevel)this.level(), true);
        }
    }

    @Override
    public boolean isAngryAt(LivingEntity target) {
        if (!this.canAttack(target)) {
            return false;
        } else if (this.level().getPathfindingCostFromLightLevels(this.blockPosition()) < 0.5F) {
            return true;
        } else {
            return NeutralMob.super.isAngryAt(target);
        }
    }

    public void attack() {
        triggerAnim("mantis_controller", "attack");
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

    @Override
    protected void playStepSound(BlockPos pos, BlockState block) {
        this.playSound(SoundEvents.SPIDER_STEP, 0.15F, 1.0F);
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.CREEPER_DEATH;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return isPlayingInstrument() ? null : ModSounds.MANTIS_AMBIENT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.PHANTOM_HURT;
    }

    public static boolean checkMantisSpawnRules(
            EntityType<? extends Animal> animal, LevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random
    ) {
        return level.getBlockState(pos.below()).is(BlockTags.FROGS_SPAWNABLE_ON);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(MANTIS_CONTROLLER);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}
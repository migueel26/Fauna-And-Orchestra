package net.migueel26.faunaandorchestra.entity.custom;

import net.migueel26.faunaandorchestra.entity.ModEntities;
import net.migueel26.faunaandorchestra.entity.goals.AnimalEatGoal;
import net.migueel26.faunaandorchestra.entity.goals.MusicalEntityPlayingInstrumentGoal;
import net.migueel26.faunaandorchestra.entity.goals.RedPandaRandomChangeStanceGoal;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class RedPandaEntity extends MusicalEntity {
    // I've to change the names to match de other entities
    protected static final RawAnimation WALK = RawAnimation.begin().thenPlay("walking");
    protected static final RawAnimation WALK_STANDING = RawAnimation.begin().thenPlay("walking_standing");
    protected static final RawAnimation WALK_KEYTAR = RawAnimation.begin().thenPlay("walking_keytar");
    protected static final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    protected static final RawAnimation IDLE_STANDING = RawAnimation.begin().thenPlay("idle_standing");
    protected static final RawAnimation STAND_UP = RawAnimation.begin().thenPlay("stand_up");
    protected static final RawAnimation SIT_DOWN = RawAnimation.begin().thenPlay("sit_down");
    protected static final RawAnimation PLAYING = RawAnimation.begin().thenPlay("playing");
    protected static final RawAnimation PLAYING_IMAGINAL_DISK = RawAnimation.begin().thenPlay("playing_imaginal_disk");
    protected static final RawAnimation INSERT_DISK = RawAnimation.begin().thenPlay("insert_disk");
    protected static final RawAnimation EAT = RawAnimation.begin().thenPlay("eat");
    protected static final RawAnimation IDLE_KEYTAR = RawAnimation.begin().thenPlay("holding_keytar");
    private static final EntityDataAccessor<Boolean> IS_STANDING = SynchedEntityData.defineId(RedPandaEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_EATING = SynchedEntityData.defineId(RedPandaEntity.class, EntityDataSerializers.BOOLEAN);
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private final AnimationController<RedPandaEntity> redPandaController = new AnimationController<>(this, "red_panda_controller", 5, this::redPandaState)
            .triggerableAnim("stand_up_animation", STAND_UP)
            .triggerableAnim("sit_down_animation", SIT_DOWN)
            .triggerableAnim("insert_disk", INSERT_DISK)
            .triggerableAnim("eat", EAT);
    public RedPandaEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);

        addOverriddenGoals();
    }

    @Override
    public DeferredItem<Item> getInstrument() {
        return ModItems.KEYTAR;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        // TamableAnimalPanicGoal(0)
        this.goalSelector.addGoal(1, new MusicalEntityPlayingInstrumentGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new RedPandaEatingBambooGoal(this));
        // BreedGoal(3)
        this.goalSelector.addGoal(4, new RedPandaLookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(4, new RedPandaLookAtPlayerGoal(this, TravellingMusician.class, 6.0F));
        // RandomStrollGoal(5)
        this.goalSelector.addGoal(4, new AnimalEatGoal(this, ModItems.PERFUMED_BAMBOO.get(), this::onEat));
        this.goalSelector.addGoal(4, new AnimalEatGoal(this, Items.BAMBOO, this::onEat));
        // RandomLookAroundGoal(6)
        this.goalSelector.addGoal(6, new RedPandaRandomChangeStanceGoal(this, 0.05F));
    }

    private <E extends GeoAnimatable> PlayState redPandaState(AnimationState<E> state) {
        if (state.isMoving()) {
            if (isHoldingInstrument())  {
                state.getController().setAnimation(WALK_KEYTAR);
            } else {
                state.getController().setAnimation(isStanding() ? WALK_STANDING : WALK);
            };
        } else if (isPlayingInstrument()) {

            if (getHat() == ModItems.IMAGINAL_DISK.get()) {
                state.getController().setAnimation(PLAYING_IMAGINAL_DISK);
            } else {
                state.getController().setAnimation(PLAYING);
            }

        } else if (isHoldingInstrument()) {
            state.getController().setAnimation(IDLE_KEYTAR);
        } else if (isEating()) {
            state.getController().setAnimation(EAT);
        } else {
            state.getController().setAnimation(
                    isStanding() ? IDLE_STANDING : IDLE);
        }
        return PlayState.CONTINUE;
    }

    private void addOverriddenGoals() {
        this.goalSelector.addGoal(0, new TamableAnimalPanicGoal(2.0D, DamageTypeTags.PANIC_CAUSES) {
            final RedPandaEntity redPanda = (RedPandaEntity) this.mob;

            @Override
            public void start() {
                redPanda.standUp(false);
                redPanda.setEating(false);
                super.start();
            }
        });

        this.goalSelector.addGoal(3, new BreedGoal(this, 1.0f) {
            @Override
            public boolean canUse() {
                return super.canUse() && !((RedPandaEntity) animal).isEating() && !((RedPandaEntity) animal).isHoldingInstrument();
            }
        });

        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 1.0) {
            final RedPandaEntity redPanda = (RedPandaEntity) this.mob;
            @Override
            public boolean canUse() {
                return super.canUse() && redPanda.isCurrentlyNotChangingStances();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && redPanda.isCurrentlyNotChangingStances();
            }
        });

        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this) {
            @Override
            public boolean canUse() {
                return super.canUse() && isCurrentlyNotChangingStances();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && isCurrentlyNotChangingStances();
            }
        });
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(IS_STANDING, false);
        builder.define(IS_EATING, false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 15d)
                .add(Attributes.MOVEMENT_SPEED, 0.2D)
                .add(Attributes.FOLLOW_RANGE, 24D);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Eating")) {
            this.setEating(compound.getBoolean("Eating"));
        }
        this.standUp(isHoldingInstrument());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Eating", isEating());
    }

    public void onEat(ItemEntity targetEntity) {
        ItemStack stack = targetEntity.getItem();

        this.setEating(true);

        if (stack.is(ModItems.PERFUMED_BAMBOO)) {
            Player owner = targetEntity.getOwner() instanceof Player player ? player : null;
            this.setInLove(owner);
        }

        this.level().playSound(null, this.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.NEUTRAL);

        stack.shrink(1);
        if (stack.isEmpty()) {
            targetEntity.discard();
        }
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(Items.BAMBOO) || stack.is(ModItems.PERFUMED_BAMBOO);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return createBaby(ModEntities.RED_PANDA.get(), (RedPandaEntity) otherParent);
    }

    @Override
    public float getAgeScale() {
        return this.isBaby() ? 0.7F : 1.0F;
    }


    public EntityDimensions getDefaultDimensions(Pose pose) {
        return this.isBaby() ? ModEntities.RED_PANDA.get().getDimensions().scale(0.7f) : super.getDefaultDimensions(pose);
    }


    public void standUp(boolean flag) {
        if (!isStanding() && flag) triggerAnim("red_panda_controller", "stand_up_animation");
        this.entityData.set(IS_STANDING, flag);
    }

    public void sitDownAnimated() {
        if (isStanding()) triggerAnim("red_panda_controller", "sit_down_animation");
        this.entityData.set(IS_STANDING, false);
    }

    public boolean isStanding() {
        return this.entityData.get(IS_STANDING);
    }

    public boolean isCurrentlyNotChangingStances() {
        return !redPandaController.isPlayingTriggeredAnimation();
    }

    @Override
    public void setHoldingInstrument(boolean holdingInstrument) {
        standUp(true);
        super.setHoldingInstrument(holdingInstrument);
    }

    @Override
    public void playSpecialClothingAnimation(ItemStack stack) {
        if (stack.is(ModItems.IMAGINAL_DISK)) {
            triggerAnim("red_panda_controller", "insert_disk");
            playSound(ModSounds.INSERT_DISK.get());
        }
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.PANDA_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PANDA_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.PANDA_STEP, 0.15F, 1.25F);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return isPlayingInstrument() ? null : SoundEvents.PANDA_AMBIENT;
    }

    public void setEating(boolean eating) {
        this.entityData.set(IS_EATING, eating);
    }

    public boolean isEating() {
        return entityData.get(IS_EATING);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(redPandaController);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    private class RedPandaLookAtPlayerGoal extends LookAtPlayerGoal {
        final RedPandaEntity redPanda = (RedPandaEntity) this.mob;
        private int lookTime;

        public RedPandaLookAtPlayerGoal(Mob mob, Class<? extends LivingEntity> lookAtType, float lookDistance) {
            super(mob, lookAtType, lookDistance);
        }

        @Override
        public boolean canUse() {
            return super.canUse() && redPanda.isCurrentlyNotChangingStances() && !redPanda.isPlayingInstrument();
        }

        @Override
        public boolean canContinueToUse() {
            if (!this.lookAt.isAlive()) {
                return false;
            } else {
                return this.redPanda.distanceToSqr(this.lookAt) > (double)(this.lookDistance * this.lookDistance) ? false : this.lookTime > 0
                        && redPanda.isCurrentlyNotChangingStances();
            }
        }

        @Override
        public void start() {
            this.lookTime = this.adjustedTickDelay(40 + this.mob.getRandom().nextInt(40));
        }

        @Override
        public void tick() {
            if (this.lookAt.isAlive()) {
                double d0 = this.redPanda.isStanding() ? this.lookAt.getEyeY() - 0.425 : this.lookAt.getEyeY();
                this.redPanda.getLookControl().setLookAt(this.lookAt.getX(), d0, this.lookAt.getZ());
                this.lookTime--;
            }
        }
    }

    private class RedPandaEatingBambooGoal extends Goal {
        private final int MAX_TICKS = 400;
        private final RedPandaEntity redPanda;
        private int ticksEating;

        public RedPandaEatingBambooGoal(RedPandaEntity redPanda) {
            this.redPanda = redPanda;
        }

        @Override
        public boolean canUse() {
            return redPanda.isEating() && !redPanda.isDeadOrDying();
        }

        @Override
        public boolean canContinueToUse() {
            return redPanda.isEating() && !redPanda.isDeadOrDying() && ticksEating < MAX_TICKS;
        }

        @Override
        public void start() {
            this.ticksEating = 0;
            super.start();
        }

        @Override
        public void stop() {
            redPanda.setEating(false);
            redPanda.spawnAtLocation(ModItems.SHARP_BAMBOO, 1);
            redPanda.level().playSound(null, redPanda.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.NEUTRAL, 1.0F, 1.0F);

            if (redPanda.isInLove()) {
                redPanda.setInLoveTime(600);
            }

            super.stop();
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            ticksEating++;
            redPanda.getNavigation().stop();

            int cycle = ticksEating % 40;

            if (cycle < 20 && cycle % 4 == 0) {
                redPanda.playSound(SoundEvents.PANDA_EAT, 1.0F, 0.9F + redPanda.getRandom().nextFloat() * 0.2F);

                if (redPanda.level() instanceof ServerLevel serverLevel) {
                    Vec3 look = redPanda.getLookAngle();
                    double x = redPanda.getX() + look.x * 0.5;
                    double y = redPanda.getEyeY() - 0.15;
                    double z = redPanda.getZ() + look.z * 0.5;

                    serverLevel.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.BAMBOO)),
                            x, y, z, 3, 0.1, 0.1, 0.1, 0.05
                    );
                }
            }
        }
    }
}

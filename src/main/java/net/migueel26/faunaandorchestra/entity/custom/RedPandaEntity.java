package net.migueel26.faunaandorchestra.entity.custom;

import net.migueel26.faunaandorchestra.entity.goals.MusicalEntityPlayingInstrumentGoal;
import net.migueel26.faunaandorchestra.entity.goals.RedPandaRandomChangeStanceGoal;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class RedPandaEntity extends MusicalEntity implements GeoEntity {
    // Gotta change the names to match de other entities
    protected static final RawAnimation WALK = RawAnimation.begin().thenPlay("walking");
    protected static final RawAnimation WALK_STANDING = RawAnimation.begin().thenPlay("walking_standing");
    protected static final RawAnimation WALK_KEYTAR = RawAnimation.begin().thenPlay("walking_keytar");
    protected static final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    protected static final RawAnimation IDLE_STANDING = RawAnimation.begin().thenPlay("idle_standing");
    protected static final RawAnimation STAND_UP = RawAnimation.begin().thenPlay("stand_up");
    protected static final RawAnimation SIT_DOWN = RawAnimation.begin().thenPlay("sit_down");
    protected static final RawAnimation PLAYING = RawAnimation.begin().thenPlay("playing");
    protected static final RawAnimation IDLE_KEYTAR = RawAnimation.begin().thenPlay("holding_keytar");
    private static final EntityDataAccessor<Boolean> IS_STANDING = SynchedEntityData.defineId(RedPandaEntity.class, EntityDataSerializers.BOOLEAN);
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private final AnimationController<RedPandaEntity> redPandaController = new AnimationController<>(this, "red_panda_controller", 5, this::redPandaState)
            .triggerableAnim("stand_up_animation", STAND_UP)
            .triggerableAnim("sit_down_animation", SIT_DOWN);
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
        //TODO: ROAR
        this.goalSelector.addGoal(0, new FloatGoal(this));
        // TamableAnimalPanicGoal(0)
        this.goalSelector.addGoal(1, new MusicalEntityPlayingInstrumentGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(4, new RedPandaLookAtPlayerGoal(this, Player.class, 6.0F));
        // RandomStrollGoal(5)
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
            state.getController().setAnimation(PLAYING);
        } else if (isHoldingInstrument()) {
            state.getController().setAnimation(IDLE_KEYTAR);
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
                super.start();
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
        standUp(isHoldingInstrument());
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(Items.BAMBOO);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null;
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
        return SoundEvents.PANDA_AMBIENT;
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
            return super.canUse() && redPanda.isCurrentlyNotChangingStances();
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

}

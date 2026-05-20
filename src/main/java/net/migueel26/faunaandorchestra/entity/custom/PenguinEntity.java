package net.migueel26.faunaandorchestra.entity.custom;

import net.migueel26.faunaandorchestra.entity.ModEntities;
import net.migueel26.faunaandorchestra.entity.goals.AlertWhenAttackedGoal;
import net.migueel26.faunaandorchestra.entity.goals.MusicalEntityPlayingInstrumentGoal;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class PenguinEntity extends MusicalEntity {
    protected static final RawAnimation RUN = RawAnimation.begin().thenPlay("run");
    protected static final RawAnimation WADDLE = RawAnimation.begin().thenPlay("waddle");
    protected static final RawAnimation WADDLE_FLUTE = RawAnimation.begin().thenPlay("waddle_flute");
    protected static final RawAnimation WAVE = RawAnimation.begin().thenPlay("wave");
    protected static final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    protected static final RawAnimation IDLE_FLUTE = RawAnimation.begin().thenPlay("holding_flute");
    protected static final RawAnimation PLAYING = RawAnimation.begin().thenPlay("playing");
    protected static final RawAnimation PLAYING_PROPELLER_HAT = RawAnimation.begin().thenPlay("playing_propeller_hat");
    protected static final RawAnimation PROPEL = RawAnimation.begin().thenPlay("propel");
    protected static final RawAnimation DECLINE = RawAnimation.begin().thenPlay("decline");
    private static final EntityDataAccessor<Boolean> IS_RUNNING = SynchedEntityData.defineId(PenguinEntity.class, EntityDataSerializers.BOOLEAN);
    public static final int DEFAULT_AGE = -72000;
    private boolean isRunning = false;

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private final AnimationController<PenguinEntity> penguinController = new AnimationController<>(this, "penguin_controller", 5, this::penguinState)
            .triggerableAnim("wave", WAVE)
            .triggerableAnim("propel", PROPEL);
    private final AnimationController<PenguinEntity> declineController = new AnimationController<>(this, "penguin_decline_controller", 2, this::emptyState)
            .triggerableAnim("decline", DECLINE);
    public PenguinEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);

        addOverriddenGoals();
    }

    @Override
    public DeferredItem<Item> getInstrument() {
        return ModItems.FLUTE;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        // TamableAnimalPanicGoal(0)
        this.goalSelector.addGoal(1, new MusicalEntityPlayingInstrumentGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new AlertWhenAttackedGoal(this, EmperorPenguinEntity.class));
        // LookAtPlayerGoal(4)
        // LookAtPlayerGoal(4, TravellingMusician)
        // RandomStrollGoal(5)
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }

    private <E extends GeoAnimatable> PlayState penguinState(AnimationState<E> state) {
        if (isPlayingInstrument()) {

            if (getHat() == ModItems.PROPELLER_HAT.get()) {
                state.getController().setAnimation(PLAYING_PROPELLER_HAT);
            } else {
                state.getController().setAnimation(PLAYING);
            }

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

    private <E extends GeoAnimatable> PlayState emptyState(AnimationState<E> state) {
        return PlayState.CONTINUE;
    }

    private void addOverriddenGoals() {
        this.goalSelector.addGoal(0, new TamableAnimalPanicGoal(2.0D, DamageTypeTags.PANIC_CAUSES) {
            final PenguinEntity penguin = (PenguinEntity) super.mob;
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
            final PenguinEntity penguin = (PenguinEntity) super.mob;
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

        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, TravellingMusician.class, 6.0F) {
            final PenguinEntity penguin = (PenguinEntity) super.mob;

            @Override
            public boolean canUse() {
                return super.canUse() && !penguin.isPlayingInstrument();
            }
        });


        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 1.0) {
            final PenguinEntity penguin = (PenguinEntity) super.mob;
            @Override
            public boolean canUse() {
                return super.canUse() && !penguin.isBusy();
            }
        });
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 15d)
                .add(Attributes.MOVEMENT_SPEED, 0.2D)
                .add(Attributes.FOLLOW_RANGE, 24D);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!isTame() && stack.is(ItemTags.FISHES) && !player.getCooldowns().isOnCooldown(stack.getItem())) {
            player.getCooldowns().addCooldown(stack.getItem(), 10);

            if (!level().isClientSide()) {
                if (random.nextFloat() <= (stack.is(ModItems.TAIYAKI) ? 0.9f : 0.1f)) {
                    this.accept(player);
                } else {
                    this.decline(player);
                }
            }

            stack.shrink(1);

            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        this.setAge(DEFAULT_AGE);
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(IS_RUNNING, false);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (key.equals(IS_RUNNING)) {
            this.isRunning = entityData.get(IS_RUNNING);
        }
        super.onSyncedDataUpdated(key);
    }

    @Override
    public float getAgeScale() {
        return 1.0f;
    }

    @Override
    protected void ageBoundaryReached() {
        if (!this.level().isClientSide() && getAge() >= 0) {
            EmperorPenguinEntity adult = this.convertTo(ModEntities.EMPEROR_PENGUIN.get(), true);

            if (adult != null) {
                this.level().levelEvent(1505, this.blockPosition(), 0);

                if (adult.getHat().equals(ModItems.PROPELLER_HAT.get())) {
                    adult.inventory.setStackInSlot(0, ItemStack.EMPTY);
                    this.spawnAtLocation(ModItems.PROPELLER_HAT, 1);
                }
            }
        }
    }

    @Override
    public float getVoicePitch() {
        return (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return isPlayingInstrument() ? null : ModSounds.BABY_PENGUIN_AMBIENT.get();
    }

    @Override
    public void playAmbientSound() {
        if (this.getAmbientSound() != null) {
            this.playSound(getAmbientSound(), 0.3f, getVoicePitch());
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
    public boolean isFood(ItemStack stack) {
        return false;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null;
    }

    public void wave() {
        triggerAnim("penguin_controller", "wave");
    }

    public void decline(Player player) {
        this.lookAt(EntityAnchorArgument.Anchor.EYES, player.position());
        triggerAnim("penguin_decline_controller", "decline");
        if (!level().isClientSide() && level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.ANGRY_VILLAGER, position().x, position().y + getBbHeight(), position().z, 3, 0.2, 0.2, 0.2, 0.01);
        }
        this.makeSound(SoundEvents.VILLAGER_NO);
    }

    public void accept(Player player) {
        this.lookAt(EntityAnchorArgument.Anchor.EYES, player.position());
        wave();
        if (!level().isClientSide() && level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, position().x, position().y + getBbHeight(), position().z, 10, 0.3, 0.3, 0.3, 0.01);
        }
        this.makeSound(SoundEvents.VILLAGER_CELEBRATE);

        EmperorPenguinEntity adult = level().getNearestEntity(EmperorPenguinEntity.class, TargetingConditions.DEFAULT, this,
                getX(), getY(), getZ(), getBoundingBox().inflate(6.0f));

        if (adult != null) {
            adult.lookAt(EntityAnchorArgument.Anchor.EYES, player.position());
            adult.accept();
            adult.spawnAtLocation(  new ItemStack(ModItems.PENGUIN_FEATHER.get()));
            adult.makeSound(SoundEvents.ITEM_PICKUP);

            if (!level().isClientSide() && level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, adult.getX(), adult.getY() + getBbHeight(), adult.getZ(), 10, 0.3, 0.3, 0.3, 0.01);
            }
        }
    }

    public boolean isBusy() {
        return penguinController.isPlayingTriggeredAnimation() || declineController.isPlayingTriggeredAnimation();
    }

    public void setRunning(boolean flag) {
        entityData.set(IS_RUNNING, flag);
        this.isRunning = flag;
    }

    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public void playSpecialClothingAnimation(ItemStack stack) {
        if (stack.is(ModItems.PROPELLER_HAT)) {
            triggerAnim("penguin_controller", "propel");
            playSound(ModSounds.PROPEL.get());
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(penguinController);
        controllers.add(declineController);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}

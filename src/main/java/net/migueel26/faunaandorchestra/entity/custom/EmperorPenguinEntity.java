package net.migueel26.faunaandorchestra.entity.custom;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.advancements.ModAdvancements;
import net.migueel26.faunaandorchestra.effect.ModEffects;
import net.migueel26.faunaandorchestra.entity.ModEntities;
import net.migueel26.faunaandorchestra.entity.goals.AnimalEatGoal;
import net.migueel26.faunaandorchestra.entity.goals.FaunaRandomLookAroundGoal;
import net.migueel26.faunaandorchestra.entity.goals.MusicalEntityPlayingInstrumentGoal;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.migueel26.faunaandorchestra.util.AdvancementUtil;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
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
    protected static final RawAnimation PLAYING_PHANTOM = RawAnimation.begin().thenPlay("playing_phantom");
    protected static final RawAnimation ATTACK = RawAnimation.begin().thenPlay("attack");
    protected static final RawAnimation ACCEPT = RawAnimation.begin().thenPlay("accept");
    protected static final RawAnimation PHANTOM_OPERA = RawAnimation.begin().thenPlay("phantom_opera");
    private static final EntityDataAccessor<Integer> REMAINING_ANGER_TIME = SynchedEntityData.defineId(EmperorPenguinEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IS_RUNNING = SynchedEntityData.defineId(EmperorPenguinEntity.class, EntityDataSerializers.BOOLEAN);
    private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
    @Nullable
    private UUID persistentAngerTarget;
    private boolean isRunning = false;
    private int emergingTicks = -1;
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private final AnimationController<EmperorPenguinEntity> penguinController = new AnimationController<>(this, "emperor_penguin_controller", 5, this::penguinState)
            .triggerableAnim("wave", WAVE)
            .triggerableAnim("attack", ATTACK)
            .triggerableAnim("phantom_opera", PHANTOM_OPERA);
    private final AnimationController<EmperorPenguinEntity> acceptController = new AnimationController<>(this, "emperor_penguin_accept_controller", 2, this::emptyState)
            .triggerableAnim("accept", ACCEPT);
    public EmperorPenguinEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);

        addOverriddenGoals();
    }

    @Override
    public RegistryObject<Item> getInstrument() {
        return ModItems.FLUTE;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(IS_RUNNING, false);
        entityData.define(REMAINING_ANGER_TIME, 0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        // TamableAnimalPanicGoal(0)
        this.goalSelector.addGoal(1, new MusicalEntityPlayingInstrumentGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new HurtByTargetGoal(this).setAlertOthers());
        // BreedGoal (3)
        // NearestAttackableTargetGoal (4)
        this.goalSelector.addGoal(5, new AnimalEatGoal(this, Items.STONE, this::onEat));
        // LookAtPlayerGoal (4)
        // LookAtPlayerGoal (3, TravellingMusician)
        // MeleeAttackGoal (5)
        // RandomStrollGoal(5)
        this.goalSelector.addGoal(6, new FaunaRandomLookAroundGoal(this));
        this.goalSelector.addGoal(8, new ResetUniversalAngerTargetGoal<>(this, true));
    }

    private void addOverriddenGoals() {
        this.goalSelector.addGoal(0, new PanicGoal(this, 2.0D) {
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

        this.goalSelector.addGoal(3, new BreedGoal(this, 1.0f) {
            @Override
            public boolean canUse() {
                return super.canUse() && !((EmperorPenguinEntity) animal).isAngry() && !((EmperorPenguinEntity) animal).isHoldingInstrument();
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

        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, TravellingMusician.class, 6.0F) {
            final EmperorPenguinEntity penguin = (EmperorPenguinEntity) super.mob;

            @Override
            public boolean canUse() {
                return super.canUse() && !penguin.isPlayingInstrument();
            }
        });

        this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.25D, false) {
            @Override
            protected void checkAndPerformAttack(LivingEntity target, double distance) {
                double d0 = this.getAttackReachSqr(target);
                if (distance <= d0 && isTimeToAttack()) {
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
                return super.canUse() && !penguin.isBusy();
            }
        });
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData, CompoundTag tag) {
        if (!(spawnType.equals(MobSpawnType.SPAWN_EGG) || spawnType.equals(MobSpawnType.MOB_SUMMONED))
                && random.nextFloat() <= 0.5f) {
            int times = random.nextFloat() <= 0.5f ? 2 : 1;
            for (int i = 0; i < times; i++) {
                PenguinEntity penguin = ModEntities.PENGUIN.get().create(level.getLevel());
                penguin.moveTo(this.position());

                level.addFreshEntity(penguin);
            }
        }
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData, tag);
    }

    @Override
    public void tick() {
        if (!level().isClientSide() && emergingTicks >= 0 && emergingTicks <= 100) {
            if (emergingTicks % 5 == 0) {
                Vec3 pos = this.position();
                ((ServerLevel) level()).sendParticles(ParticleTypes.SMOKE, pos.x, pos.y, pos.z, 40, 0.4, 0.1, 0.4, 0);
            }
            emergingTicks++;
        }
        super.tick();
    }

    @Override
    public float getScale() {
        return 1.0f;
    }

    private <E extends GeoAnimatable> PlayState penguinState(AnimationState<E> state) {
        if (isPlayingInstrument()) {
            if (getHat() == ModItems.PHANTOM_MASK.get()) {
                state.getController().setAnimation(PLAYING_PHANTOM);
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
        return isTame() && itemStack.is(ItemTags.FISHES);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return createBaby(ModEntities.PENGUIN.get(), (EmperorPenguinEntity) ageableMob);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        Entity entity = source.getEntity();
        boolean secondChance = this.hasSecondLife();
        // We apply the second life
        boolean wasHurt = super.hurt(source, amount);
        // If before we had a second life, and now we don't, we used it
        boolean wastedSecondChance = secondChance && !hasSecondLife();
        if (wastedSecondChance && !level().isClientSide() && level() instanceof ServerLevel serverLevel) {
            // Check if conditions are met
            if (isEntityInWoodlandMansion(this) && this.getOwner() instanceof Player player) {
                if (!AdvancementUtil.hasAdvancement(player, FaunaAndOrchestra.MOD_ID, "myths/dan_myth3")) {
                    player.displayClientMessage(Component.translatable("text.faunaandorchestra.myth_locked"), true);
                } else {
                    this.inventory.setStackInSlot(HAT_SLOT, ModItems.PHANTOM_MASK.get().getDefaultInstance());
                    ModAdvancements.FIRST_RESOLVED_MYTH.trigger((ServerPlayer) player);

                    this.getNavigation().stop();
                    this.addEffect(new MobEffectInstance(ModEffects.OVERWHELMING_SLOWNESS.get(), 100, 255, false, false, false));
                    player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 40, 5, false, false, false));
                    player.lookAt(EntityAnchorArgument.Anchor.EYES, this.getEyePosition());
                    this.lookAt(EntityAnchorArgument.Anchor.FEET, player.getEyePosition());

                    this.emergingTicks = 0;
                }
                return true;
            }
        }

        return wasHurt;
    }

    public static boolean isEntityInWoodlandMansion(Entity entity) {
        if (entity.level() instanceof ServerLevel serverLevel) {
            BlockPos pos = entity.blockPosition();
            Structure structure = serverLevel.structureManager().registryAccess().registryOrThrow(Registries.STRUCTURE).get(BuiltinStructures.WOODLAND_MANSION);

            return structure != null && serverLevel.structureManager().getStructureAt(pos, structure).isValid();
        }
        return false;
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (!this.level().isClientSide()) {
            this.updatePersistentAnger((ServerLevel)this.level(), true);
        }
    }

    public void onEat(ItemEntity targetEntity) {
        Player owner = targetEntity.getOwner() instanceof Player player ? player : null;
        ItemStack stack = targetEntity.getItem();

        this.setInLove(owner);

        this.level().playSound(null, this.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.NEUTRAL);
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, stack),
                    targetEntity.getX(), targetEntity.getY(), targetEntity.getZ(),
                    20, 0.05, 0.05, 0.05, 0.1);
        }

        stack.shrink(1);
        if (stack.isEmpty()) {
            targetEntity.discard();
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

    public void accept() {
        triggerAnim("emperor_penguin_accept_controller", "accept");
    }

    public boolean isBusy() {
        return penguinController.isPlayingTriggeredAnimation() || acceptController.isPlayingTriggeredAnimation();
    }

    public void setRunning(boolean flag) {
        entityData.set(IS_RUNNING, flag);
        this.isRunning = flag;
    }

    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public @Nullable <T extends Mob> T convertTo(EntityType<T> entityType, boolean transferInventory) {
        // Baby penguins can't wear the phantom of the opera mask
        if (this.getHat().equals(ModItems.PHANTOM_MASK.get())) {
            this.inventory.setStackInSlot(0, ItemStack.EMPTY);
            this.spawnAtLocation(ModItems.PHANTOM_MASK.get(), 1);
        }
        return super.convertTo(entityType, transferInventory);
    }

    @Override
    public void playSpecialClothingAnimation(ItemStack stack) {
        if (stack.is(ModItems.PHANTOM_MASK.get())) {
            triggerAnim("emperor_penguin_controller", "phantom_opera");
            playSound(ModSounds.PHANTOM_MASK.get());
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(penguinController);
        controllers.add(acceptController);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}

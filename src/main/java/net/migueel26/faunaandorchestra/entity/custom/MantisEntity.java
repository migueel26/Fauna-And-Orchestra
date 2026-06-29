package net.migueel26.faunaandorchestra.entity.custom;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.advancements.ModAdvancements;
import net.migueel26.faunaandorchestra.effect.ModEffects;
import net.migueel26.faunaandorchestra.entity.ModEntities;
import net.migueel26.faunaandorchestra.entity.custom.variants.MantisVariant;
import net.migueel26.faunaandorchestra.entity.goals.AnimalEatGoal;
import net.migueel26.faunaandorchestra.entity.goals.MantisBreedGoal;
import net.migueel26.faunaandorchestra.entity.goals.MantisLayEggGoal;
import net.migueel26.faunaandorchestra.entity.goals.MusicalEntityPlayingInstrumentGoal;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.migueel26.faunaandorchestra.util.AdvancementUtil;
import net.minecraft.Util;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.RegistryObject;
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

import java.util.Iterator;
import java.util.UUID;

public class MantisEntity extends MusicalEntity implements NeutralMob, VariantEntity<MantisVariant> {
    protected static final RawAnimation WALK = RawAnimation.begin().thenPlay("walk");
    protected static final RawAnimation WALK_VIOLIN = RawAnimation.begin().thenPlay("walk_violin");
    protected static final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    protected static final RawAnimation ATTACK = RawAnimation.begin().thenPlay("attack");
    protected static final RawAnimation PLAYING = RawAnimation.begin().thenPlay("playing");
    protected static final RawAnimation IDLE_VIOLIN = RawAnimation.begin().thenPlay("idle_violin");
    protected static final RawAnimation PLAYING_ENLIGHTEN = RawAnimation.begin().thenPlay("playing_enlighten");
    protected static final RawAnimation ENLIGHTEN = RawAnimation.begin().thenPlay("enlighten");
    public static final int MIN_ALTAR_PILLAR = 170;
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private static final EntityDataAccessor<Integer> REMAINING_ANGER_TIME = SynchedEntityData.defineId(MantisEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(MantisEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> HAS_EGG = SynchedEntityData.defineId(MantisEntity.class, EntityDataSerializers.BOOLEAN);
    private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
    private final AnimationController<MantisEntity> MANTIS_CONTROLLER =
            new AnimationController<>(this, "mantis_controller", 5, this::mantisState)
                    .triggerableAnim("attack", ATTACK)
                    .triggerableAnim("enlighten", ENLIGHTEN);
    @Nullable
    private UUID persistentAngerTarget;
    public int layEggCounter;

    public MantisEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);

        addOverridenGoals();
    }

    @Override
    public RegistryObject<Item> getInstrument() {
        return ModItems.VIOLIN;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(0, new PanicGoal(this, 1.0) {
            @Override
            public boolean canUse() {
                return ((TamableAnimal) mob).isTame() && super.canUse();
            }
        });
        this.goalSelector.addGoal(1, new MusicalEntityPlayingInstrumentGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(4, new MantisBreedGoal(this, 1.0f));
        this.goalSelector.addGoal(4, new MantisLayEggGoal(this, 1.0f));
        // NearestAttackableTargetGoal (4)
        // MeleeAttackGoal (5)
        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 1.0));
        // LookAtPlayerGoal (6)
        // LookAtPlayerGoal (6, TravellingMusician)
        this.goalSelector.addGoal(7, new AnimalEatGoal(this, ModItems.MANTIS_FOOD.get(), this::onEat));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(8, new ResetUniversalAngerTargetGoal<>(this, true));
    }

    private void addOverridenGoals() {
        this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.25D, false) {
            @Override
            public boolean canUse() {
                return super.canUse() && !mob.isBaby();
            }
            @Override
            protected void checkAndPerformAttack(LivingEntity target, double distToEnemySqr) {
                double reach = this.getAttackReachSqr(target);

                if (distToEnemySqr <= reach && this.isTimeToAttack()) {
                    ((MantisEntity) this.mob).attack();
                    this.resetAttackCooldown();
                    this.mob.doHurtTarget(target);
                }
            }
        });

        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F) {
            @Override
            public boolean canUse() {
                return super.canUse() && !((MusicalEntity) mob).isPlayingInstrument();
            }
        });

        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, TravellingMusician.class, 6.0F) {
            @Override
            public boolean canUse() {
                return super.canUse() && !((MusicalEntity) mob).isPlayingInstrument();
            }
        });

        this.goalSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Player.class, 10, false, true, this::isAngryAt) {
            @Override
            public boolean canUse() {
                return super.canUse() && !mob.isBaby();
            }

            @Override
            public void start() {
                super.start();
                this.mob.playSound(ModSounds.MANTIS_ANGRY.get());
            }
        });
    }

    protected <E extends GeoAnimatable> PlayState mantisState(AnimationState<E> state) {
        if (state.isMoving()) {
            state.getController().transitionLength(5);
            state.getController().setAnimation(isHoldingInstrument() ? WALK_VIOLIN : WALK);
        } else if (isPlayingInstrument()) {

            state.getController().transitionLength(5);
            if (getHat() == ModItems.MASK_OF_THE_ENLIGHTENED.get()) {
                state.getController().setAnimation(PLAYING_ENLIGHTEN);
            } else {
                state.getController().setAnimation(PLAYING);
            }
            state.getController().transitionLength(0);

        } else if (isHoldingInstrument()) {
            if (getHat() == ModItems.MASK_OF_THE_ENLIGHTENED.get()) {
                state.getController().transitionLength(5);
            } else {
                state.getController().transitionLength(0);
            }
            state.getController().setAnimation(IDLE_VIOLIN);
        } else {
            state.getController().setAnimation(IDLE);
        }
        return PlayState.CONTINUE;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(REMAINING_ANGER_TIME, 0);
        this.entityData.define(VARIANT, 0);
        this.entityData.define(HAS_EGG, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Variant", this.getVariantId());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.entityData.set(VARIANT, compound.getInt("Variant"));
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData, CompoundTag tag) {
        MantisVariant variant;
        if (spawnType.equals(MobSpawnType.SPAWN_EGG)) {
            // If spawn egg, random
            variant = Util.getRandom(MantisVariant.values(), this.random);
        } else {
            // If not spawn egg, depending on the biome
            Holder<Biome> biome = level.getBiome(blockPosition());
            if (biome.is(Biomes.CHERRY_GROVE)) {
                variant = MantisVariant.ORCHID;
            } else {
                variant = MantisVariant.NORMAL;
            }
        }

        this.setVariant(variant);
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData, tag);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (itemStack.is(ModItems.UNLIT_MASK.get()) && this.isHoldingInstrument() && !this.isBaby()) {
            if (!this.level().isClientSide()) return tryToLitMask(player, hand);
            else return InteractionResult.sidedSuccess(this.level().isClientSide());
        }
        return super.mobInteract(player, hand);
    }

    private InteractionResult tryToLitMask(Player player, InteractionHand hand) {
        if (!AdvancementUtil.hasAdvancement(player, FaunaAndOrchestra.MOD_ID, "myths/dan_myth1")) {
            player.displayClientMessage(Component.translatable("text.faunaandorchestra.myth_locked"), true);
            return InteractionResult.FAIL;
        } else {
            if (isDawn() && this.getBlockY() >= MIN_ALTAR_PILLAR && checkAltar()) {
                ServerLevel serverLevel = (ServerLevel) level();

                // Summon Lighting Bolt
                EntityType.LIGHTNING_BOLT.spawn(serverLevel, blockPosition().offset(5, 1, 0), MobSpawnType.MOB_SUMMONED);

                // Replace Gold with Coal
                level().setBlock(blockPosition().offset(3, 0, 0), Blocks.COAL_BLOCK.defaultBlockState(), 3);
                level().setBlock(blockPosition().offset(0, 0, -3), Blocks.COAL_BLOCK.defaultBlockState(), 3);
                level().setBlock(blockPosition().offset(0, 0, 3), Blocks.COAL_BLOCK.defaultBlockState(), 3);
                level().setBlock(blockPosition().offset(2, 1, -3), Blocks.COAL_BLOCK.defaultBlockState(), 3);
                level().setBlock(blockPosition().offset(2, 1, 3), Blocks.COAL_BLOCK.defaultBlockState(), 3);

                // Give Darkness, Slowness and Look at Mantis Eyes
                player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 100, 1, false, false, true));
                player.addEffect(new MobEffectInstance(ModEffects.OVERWHELMING_SLOWNESS.get(), 100, 255, false, false, true));
                player.moveTo(Vec3.atBottomCenterOf(blockPosition().offset(-2, 0, 0)));
                player.lookAt(EntityAnchorArgument.Anchor.EYES, this.getEyePosition());

                // Give the mask to the mantis and look at player
                this.inventory.setStackInSlot(HAT_SLOT, new ItemStack(ModItems.MASK_OF_THE_ENLIGHTENED.get()));
                this.lookAt(EntityAnchorArgument.Anchor.FEET, player.getEyePosition());

                // Clear the mask and give advancement
                player.setItemInHand(hand, ItemStack.EMPTY);
                ModAdvancements.FIRST_RESOLVED_MYTH.trigger((ServerPlayer) player);

                return InteractionResult.sidedSuccess(!this.level().isClientSide());
            }
        }
        return InteractionResult.FAIL;
    }

    private boolean isDawn() {
        return level().getDayTime() >= 23200 || level().getDayTime() <= 800;
    }

    private boolean checkAltar() {
        boolean result = true;

        // Check the blocks under the mantis
        Iterator<BlockPos> baseIt = BlockPos.betweenClosed(blockPosition().offset(-1, -1, -1), blockPosition().offset(1, -1, 1)).iterator();
        while (baseIt.hasNext() && result) {
            if (!level().getBlockState(baseIt.next()).is(Blocks.QUARTZ_BLOCK)) {
                result = false;
            }
        }

        // Check the stairs
        Iterator<BlockPos> stairsIt = BlockPos.betweenClosed(blockPosition().offset(-2, -1, -1), blockPosition().offset(-2, -1, 1)).iterator();
        while (stairsIt.hasNext() && result) {
            if (!level().getBlockState(stairsIt.next()).is(Blocks.QUARTZ_STAIRS)) {
                result = false;
            }
        }

        // Check the left, right and front pillars
        for (int i = 0; i < 2 && result; i++) {
            Block block = i == 0 ? Blocks.QUARTZ_PILLAR : Blocks.GOLD_BLOCK;
            if (!level().getBlockState(blockPosition().offset(0, -1 + i, -3)).is(block)
                    || !level().getBlockState(blockPosition().offset(0, -1 + i, 3)).is(block)
                    || !level().getBlockState(blockPosition().offset(3, -1 + i, 0)).is(block)) {
                result = false;
            }
        }

        // Check the front left and right pilars
        for (int i = 0; i < 3; i++) {
            Block block = i < 2 ? Blocks.QUARTZ_PILLAR : Blocks.GOLD_BLOCK;
            if (!level().getBlockState(blockPosition().offset(2, -1 + i, -3)).is(block)
                    || !level().getBlockState(blockPosition().offset(2, -1 + i, 3)).is(block)) {
                result = false;
            }
        }

        return result;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 15d)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FOLLOW_RANGE, 24D)
                .add(Attributes.ATTACK_DAMAGE, 4.0)
                .add(Attributes.ATTACK_KNOCKBACK, Attributes.ATTACK_KNOCKBACK.getDefaultValue());

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
        if (!this.canAttack(target) || this.isBaby()) {
            return false;
        } else if (this.level().getPathfindingCostFromLightLevels(this.blockPosition()) < 0.5F && !isTame()) {
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

    @Override
    public void playAmbientSound() {
        if (this.getAmbientSound() != null) {
            this.playSound(getAmbientSound(), 0.3f, getVoicePitch());
        }
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

    @Override
    public void playSpecialClothingAnimation(ItemStack stack) {
        if (stack.is(ModItems.MASK_OF_THE_ENLIGHTENED.get())) {
            triggerAnim("mantis_controller", "enlighten");
            playSound(ModSounds.ENLIGHTEN.get());
        }
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return ModEntities.MANTIS.get().create(level);
    }

    @Override
    public float getScale() {
        return this.isBaby() ? 0.3F : 1.0F;
    }

    @Override
    protected void ageBoundaryReached() {
        super.ageBoundaryReached();
        if (!this.isBaby() && this.level().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            this.spawnAtLocation(ModItems.MANTIS_CLAW.get(), 1);
        }
    }

    public EntityDimensions getDimensions(Pose pose) {
        return this.isBaby() ? ModEntities.MANTIS.get().getDimensions().scale(0.3f) : super.getDimensions(pose);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    @Override
    public int getVariantId() {
        return entityData.get(VARIANT);
    }

    @Override
    public MantisVariant getVariant() {
        return MantisVariant.byId(getVariantId() & 255);
    }

    @Override
    public void setVariant(MantisVariant variant) {
        this.entityData.set(VARIANT, variant.getId());
    }

    public boolean hasEgg() {
        return this.entityData.get(HAS_EGG);
    }

    public void setHasEgg(boolean hasEgg) {
        this.entityData.set(HAS_EGG, hasEgg);
    }

    public boolean isLayingEgg() {
        return layEggCounter > 0;
    }

    public void setLayingEgg(boolean isLayingEgg) {
        this.layEggCounter = isLayingEgg ? 1 : 0;
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
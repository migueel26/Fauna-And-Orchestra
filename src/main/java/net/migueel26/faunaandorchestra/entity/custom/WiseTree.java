package net.migueel26.faunaandorchestra.entity.custom;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.particles.ModParticleTypes;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.migueel26.faunaandorchestra.util.ModSavedData;
import net.migueel26.faunaandorchestra.util.MusicUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import oshi.util.tuples.Pair;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class WiseTree extends TamableAnimal implements GeoEntity, TalkableEntity, Npc {
    protected int tick = 0;
    // Fruit animation
    protected int fruitTick = -1;
    protected Item fancyInstrument = null;
    public static final int DEFAULT_WET_TIME = 600;
    protected static final int DEFAULT_DROP_ANIMATION_DURATION = 80;
    private static final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    public static final RawAnimation DROP = RawAnimation.begin().thenPlay("drop");
    protected static final EntityDataAccessor<Integer> DIALOGUE_TIMER = SynchedEntityData.defineId(WiseTree.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Boolean> GOOD_MORNING = SynchedEntityData.defineId(WiseTree.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Integer> LIFE_STAGE = SynchedEntityData.defineId(WiseTree.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> LIFE_TIME = SynchedEntityData.defineId(WiseTree.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> TIME_WET = SynchedEntityData.defineId(WiseTree.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Boolean> FRUIT = SynchedEntityData.defineId(WiseTree.class, EntityDataSerializers.BOOLEAN);
    public static final ResourceLocation ICON = new ResourceLocation(FaunaAndOrchestra.MOD_ID, "textures/gui/entity/wise_tree_icon.png");
    public String currentDialogue;
    public static final String RESOURCE = "dialogue.faunaandorchestra.wise_tree";
    private final AnimationController<WiseTree> wiseTreeController = new AnimationController<>(this, "wise_tree_controller", 5, this::wiseTreeState)
            .triggerableAnim("drop", DROP);
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    public WiseTree(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DIALOGUE_TIMER, 0);
        this.entityData.define(GOOD_MORNING, true);
        this.entityData.define(LIFE_STAGE,1);
        this.entityData.define(LIFE_TIME, 0);
        this.entityData.define(TIME_WET, 0);
        this.entityData.define(FRUIT, false);

        super.defineSynchedData();
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        if (getLifeStage() == 1) {
            return EntityDimensions.scalable(0.6f, 1.0f);
        } else if (getLifeStage() == 2) {
            return EntityDimensions.scalable(1.5f, 2.25f);
        } else {
            return EntityDimensions.scalable(3.0f, 5.0f);
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (getLifeStage() == 3 && getDialogueTimer() == 0) {
            if (level().isClientSide()) {
                increaseDialogueTimer();
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag tag) {
        if (mobSpawnType.equals(MobSpawnType.SPAWN_EGG)) this.setOwnerUUID(level.getNearestPlayer(this, 10.D).getUUID());
        return super.finalizeSpawn(level, difficulty, mobSpawnType, spawnGroupData, tag);
    }

    @Override
    public void tick() {
        // Fruit animation
        if (fruitTick > 0) {
            if (fruitTick == 20) {
                Vec3 look = this.getLookAngle().normalize();
                Vec3 itemPos = this.position().add(look.scale(4.25).add(0, 3, 0));
                level().addFreshEntity(new ItemEntity(level(), itemPos.x, itemPos.y, itemPos.z,
                        new ItemStack(ModItems.FRUIT_OF_LIFE.get())));
                if (!level().isClientSide()) {
                    level().playSound(null, blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.NEUTRAL);
                }
            }
            fruitTick--;
        } else if (fruitTick == 0) {
            fruitTick = -1;
            this.entityData.set(FRUIT, false);
        }

        // Wet Logic
        if (tick == 10) {
            fancyInstrument = MusicUtil.getRandomInstrument(level());
            refreshDimensions();
        }

        if (getTimeWet() == 1) {
            fancyInstrument = MusicUtil.getRandomInstrument(level());
        }

        if (isWet() && tick % 20 == 0) {
            decreaseWetTime();
            increaseLifeTime();

            if (!level().isClientSide()) {
                float offset = switch (getLifeStage()) {
                   case 1 -> 0.3f;
                   case 2 -> 0.6f;
                   default -> 1.5f;
                };
                ((ServerLevel) level()).sendParticles(ParticleTypes.DRIPPING_WATER, getX(), getY(), getZ(), 5, offset, offset, offset, 0);
            }
        }

        if (getLifeTime() >= DEFAULT_WET_TIME * 3) {
            // Sprout -> Young
            if (!level().isClientSide()) {
                level().playSound(null, blockPosition(), ModSounds.SUCCESSFUL_TAME.get(), SoundSource.NEUTRAL);
                ((ServerLevel) level()).sendParticles(ParticleTypes.HAPPY_VILLAGER, getX(), getY(), getZ(), 50, 3, 3, 3, 0.05);
            }

            if (getLifeStage() == 3) this.entityData.set(FRUIT, true);
            if (getLifeStage() < 3) this.entityData.set(LIFE_STAGE, getLifeStage()+1);

            this.entityData.set(LIFE_TIME, 0);
            this.entityData.set(TIME_WET, 0);
            this.tick = 0;

            refreshDimensions();
            wiseTreeController.forceAnimationReset();
        }

        tick++;
        super.tick();
    }

    public void tryToWater(Item item) {
        if (!isWet() && !entityData.get(FRUIT)) {
            float offset = getLifeStage() == 3 ? 1.75f : 0.2f;
            int quantity = getLifeStage() == 3 ? 10 : 3;
            if (item == fancyInstrument) {
                if (!level().isClientSide()) {
                    level().playSound(null, blockPosition(), SoundEvents.VILLAGER_YES, SoundSource.NEUTRAL);
                    ((ServerLevel) level()).sendParticles(ParticleTypes.HEART, position().x, getEyeY() + 1, position().z, quantity, offset, offset, offset, 0.05);
                }

                this.setWet();

            } else if (!level().isClientSide()) {
                ((ServerLevel) level()).sendParticles(ParticleTypes.ANGRY_VILLAGER, position().x, getEyeY() + 1, position().z, quantity, offset, offset, offset, 0.05);
                level().playSound(null, blockPosition(), SoundEvents.VILLAGER_NO, SoundSource.NEUTRAL);
            }
        }
    }

    private <E extends GeoAnimatable> PlayState wiseTreeState(AnimationState<E> state) {
        state.getController().setAnimation(IDLE);
        return PlayState.CONTINUE;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("LifeStage", getLifeStage());
        compound.putInt("TimeWet", getTimeWet());
        compound.putInt("LifeTime", getLifeTime());
        compound.putBoolean("Fruit", entityData.get(FRUIT));

        super.addAdditionalSaveData(compound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        if (compound.contains("LifeStage")) {
            setLifeStage(compound.getInt("LifeStage"));
        }

        if (compound.contains("TimeWet")) {
            this.entityData.set(TIME_WET, compound.getInt("TimeWet"));
        }

        if (compound.contains("LifeTime")) {
            this.entityData.set(LIFE_TIME, compound.getInt("LifeTime"));
        }

        if (compound.contains("Fruit")) {
            this.entityData.set(FRUIT, compound.getBoolean("Fruit"));
        }

        super.readAdditionalSaveData(compound);
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getEntity() instanceof Player player && player.getMainHandItem().getItem() instanceof AxeItem) {
            if (getLifeStage() == 3) {
                double speed = 0.25D;
                double motionX = (level().random.nextDouble() - 0.5D) * 2.0D * speed;
                double motionY = level().random.nextDouble() * speed + 0.1D;
                double motionZ = (level().random.nextDouble() - 0.5D) * 2.0D * speed;

                ItemEntity item = new ItemEntity(level(), getX(), getY() + 1, getZ(), new ItemStack(Items.OAK_LOG, random.nextInt(1, 4)),
                        motionX, motionY, motionZ);
                item.setPos(getX(), getY() + 1, getZ());
                level().addFreshEntity(item);
            }

            return super.hurt(source, amount);
        } else if (entityData.get(FRUIT) && getLifeStage() == 3 && fruitTick == -1) {
            triggerAnim("wise_tree_controller", "drop");
            this.fruitTick = DEFAULT_DROP_ANIMATION_DURATION;
            level().playSound(source.getEntity() instanceof Player player ? player : null, blockPosition(), ModSounds.WISE_TREE_DROP.get(), SoundSource.NEUTRAL);
            return false;
        } else {
            if (!level().isClientSide()) {
                ((ServerLevel) level()).sendParticles(ParticleTypes.CRIT, getX(), getY() + 1.25, getZ(),
                        7, 0.3, 0.15, 0.3, 0.05);
            }
            return false;
        }

    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return getLifeStage() == 3 ? ModSounds.WISE_TREE_AMBIENT.get() : super.getAmbientSound();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.WOOD_HIT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.WOOD_BREAK;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 80d)
                .add(Attributes.MOVEMENT_SPEED, 0D)
                .add(Attributes.FOLLOW_RANGE, 24D);
    }

    @Override
    public void checkDespawn() {

    }

    @Override
    public void knockback(double strength, double x, double z) {

    }

    @Override
    public boolean isPushable() {
        return false;
    }

    public int getLifeStage() {
        return entityData.get(LIFE_STAGE);
    }

    public void setLifeStage(int stage) {
        entityData.set(LIFE_STAGE, stage);
    }

    @Override
    public int getTextBoxOffset() {
        return 8;
    }

    @Override
    public ResourceLocation getIcon() {
        return ICON;
    }

    @Override
    public String getRandomDialogue(Player player) {
        // NOTE: % is the player's name
        // # is laugh and it's animated
        String dialogue = currentDialogue;
        boolean goodMorning = entityData.get(GOOD_MORNING);
        if (getDialogueTimer() <= 5) {
            if (goodMorning) {
                dialogue = Component.translatable(RESOURCE + "0").getString();
            } else {
                int randomDialogue = random.nextInt(1, 21);
                dialogue = Component.translatable(RESOURCE + randomDialogue).getString();
            }
            if (getOwner() == null || !getOwner().is(player)) dialogue = "§k" + dialogue;
            currentDialogue = dialogue;
        }
        return dialogue;
    }

    public void setWet() {
        this.entityData.set(TIME_WET, DEFAULT_WET_TIME);
    }

    public boolean isWet() {
        return this.entityData.get(TIME_WET) > 0;
    }

    public int getTimeWet() {
        return this.entityData.get(TIME_WET);
    }

    public int getLifeTime() {
        return this.entityData.get(LIFE_TIME);
    }

    public void increaseLifeTime() {
        this.entityData.set(LIFE_TIME, getLifeTime() + 1);
    }

    public void decreaseWetTime() {
        this.entityData.set(TIME_WET, getTimeWet() - 1);
    }

    @Override
    public Pair<Integer, Integer> getIconSize() {
        return new Pair<>(111, 102);
    }

    @Override
    public Pair<Integer, Integer> getIconLocation() {
        return new Pair<>(71, 94);
    }

    @Override
    public int getDialogueTimer() {
        return entityData.get(DIALOGUE_TIMER);
    }

    @Override
    public void increaseDialogueTimer() {
        entityData.set(DIALOGUE_TIMER, getDialogueTimer() + 1);
    }

    @Override
    public void resetDialogueTimer() {
        entityData.set(DIALOGUE_TIMER, 0);
    }

    @Override
    public void setGoodMorning(boolean goodMorning) {
        entityData.set(GOOD_MORNING, goodMorning);
    }

    @Override
    public boolean getGoodMorning() {
        return entityData.get(GOOD_MORNING);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(wiseTreeController);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
}

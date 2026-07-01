package net.migueel26.faunaandorchestra.entity.custom.koala_workers;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.ModEntities;
import net.migueel26.faunaandorchestra.entity.custom.TalkableEntity;
import net.migueel26.faunaandorchestra.entity.goals.RandomWalkToPlayerGoal;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.particles.ModParticleTypes;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.migueel26.faunaandorchestra.util.ModTags;
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
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
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

public class WorkerKoalaEntity extends AgeableMob implements Npc, GeoEntity, TalkableEntity {
    private static final RawAnimation WALK = RawAnimation.begin().thenPlay("walk");
    private static final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    public static final int DESPAWN_DELAY = 47999;
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    AnimationController<WorkerKoalaEntity> controller = new AnimationController<>(this, "worker_koala_controller", 5, this::koalaState);
    // SERVER SIDE
    private int despawnDelay;
    // TALKABLE ENTITY
    protected static final EntityDataAccessor<Integer> DIALOGUE_TIMER = SynchedEntityData.defineId(WorkerKoalaEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Boolean> GOOD_MORNING = SynchedEntityData.defineId(WorkerKoalaEntity.class, EntityDataSerializers.BOOLEAN);
    public static final ResourceLocation ICON = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/gui/entity/koala_icon.png");
    public static final String RESOURCE = "dialogue.faunaandorchestra.worker_koala";
    public String currentDialogue;
    public WorkerKoalaEntity(EntityType<? extends AgeableMob> entityType, Level level) {
        super(entityType, level);

        ((GroundPathNavigation) this.getNavigation()).setCanOpenDoors(true);
        this.setCustomName(getRandomName());

        addOverridenGoals();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DIALOGUE_TIMER, 0);
        entityData.define(GOOD_MORNING, true);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        // PanicGoal(1);
        this.goalSelector.addGoal(3, new RandomWalkToPlayerGoal(this, 1.0f, 120, 32));
        this.goalSelector.addGoal(4, new RandomStrollGoal(this, 1.0f));
        this.goalSelector.addGoal(9, new InteractGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Mob.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }

    private void addOverridenGoals() {
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.5F) {
            @Override
            public void stop() {
                super.stop();
                if (!level().isClientSide() && level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.POOF, mob.getX(), mob.getY(), mob.getZ(), 30, 0.1, 0.5, 0.1, 0.3);
                }
                mob.discard();
            }
        });
    }

    private <E extends GeoAnimatable> PlayState koalaState(AnimationState<E> state) {
        if (state.isMoving()) {
            state.getController().setAnimation(WALK);
        } else {
            state.getController().setAnimation(IDLE);
        }
        return PlayState.CONTINUE;
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        if (compound.contains("DespawnDelay")) {
            this.despawnDelay = compound.getInt("DespawnDelay");
        }
        super.readAdditionalSaveData(compound);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("DespawnDelay", despawnDelay);
        super.addAdditionalSaveData(compound);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData, CompoundTag tag) {
        this.despawnDelay = DESPAWN_DELAY;
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData, tag);
    }

    public void aiStep() {
        if (!this.level().isClientSide) {
            this.maybeDespawn();
        }
        super.aiStep();
    }

    private void maybeDespawn() {
        if (this.despawnDelay > 0 && --this.despawnDelay == 0) {
            if (!level().isClientSide() && level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.POOF, getX(), getY(), getZ(), 30, 0.1, 0.5, 0.1, 0.3);
            }
            this.discard();
        }

    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 15d)
                .add(Attributes.MOVEMENT_SPEED, 0.2D)
                .add(Attributes.FOLLOW_RANGE, 24D);
    }

    @Override
    public boolean isPersistenceRequired() {
        return true;
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return null;
    }

    private Component getRandomName() {
        return Component.translatable(ModEntities.WORKER_KOALA.get().getDescriptionId() + "_name" + random.nextIntBetweenInclusive(0, 15));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(controller);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    @Override
    public ResourceLocation getIcon() {
        return ICON;
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(ModTags.Items.KITS) && !level().isClientSide()) {
            // Some VFX and SFX
            ((ServerLevel) level()).sendParticles(ModParticleTypes.STAR.get(), getX(), getY() + 0.5, getZ(), 20, 0.5, 0.5, 0.5, 0.1);
            level().playSound(null, blockPosition(), ModSounds.TWINKLE.get(), SoundSource.NEUTRAL, 1.0f, 1.0f);

            // We transform the koala
            this.convertTo(getKoala(stack), true);
            
            return InteractionResult.SUCCESS;
        } else if (getDialogueTimer() == 0) {
            if (level().isClientSide()) {
                increaseDialogueTimer();
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    @Override
    public String getRandomDialogue(Player player) {
        // NOTE: % is the koala's name
        String dialogue = currentDialogue;
        boolean goodMorning = entityData.get(GOOD_MORNING);
        if (getDialogueTimer() <= 5) {
            if (goodMorning) {
                dialogue = Component.translatable(RESOURCE + "0").getString();
                String[] arr = dialogue.split("%");
                dialogue = arr[0] + this.getCustomName().getString() + arr[1];
            } else {
                dialogue = Component.translatable(RESOURCE + "1").getString();
            }
            currentDialogue = dialogue;
        }
        return dialogue;
    }

    public EntityType<? extends AbstractKoalaWorker> getKoala(ItemStack stack) {
        if (stack.is(ModItems.SEWING_KIT.get())) {
            return ModEntities.TAILOR_KOALA.get();
        } else if (stack.is(ModItems.MELOMANCY_KIT.get())) {
            return ModEntities.MELOMANCER_KOALA.get();
        } else if (stack.is(ModItems.FARMING_KIT.get())) {
            return ModEntities.FARMER_KOALA.get();
        }
        return null;
    }

    @Override
    public @Nullable <T extends Mob> T convertTo(EntityType<T> entityType, boolean transferInventory) {
        T entity = super.convertTo(entityType, transferInventory);
        if (entity instanceof AbstractKoalaWorker koalaWorker) {
            koalaWorker.setYBodyRot(this.getYRot());
            koalaWorker.setYHeadRot(this.getYHeadRot());
            koalaWorker.setXRot(this.getXRot());

            koalaWorker.lookForConductor((ServerLevelAccessor) level());
        }
        return entity;
    }

    @Override
    public Pair<Integer, Integer> getIconSize() {
        return new Pair<>(49, 60);
    }

    @Override
    public Pair<Integer, Integer> getIconLocation() {
        return new Pair<>(107, 136);
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

    public void setDialogueTimer(int timer) {
        entityData.set(DIALOGUE_TIMER, timer);
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
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.PANDA_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PANDA_DEATH;
    }
}

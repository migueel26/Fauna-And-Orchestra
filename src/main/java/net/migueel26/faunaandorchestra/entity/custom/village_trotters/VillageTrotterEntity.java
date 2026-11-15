package net.migueel26.faunaandorchestra.entity.custom.village_trotters;

import net.migueel26.faunaandorchestra.entity.ModEntities;
import net.migueel26.faunaandorchestra.entity.custom.variants.ButterflyVariant;
import net.migueel26.faunaandorchestra.mixins.client.accessors.ClientLevelAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;

public class VillageTrotterEntity extends AgeableMob implements GeoEntity {
    // ENTITY DATA
    protected static final EntityDataAccessor<Optional<UUID>> CORNELIUS_UUID = SynchedEntityData.defineId(VillageTrotterEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    protected static final EntityDataAccessor<Boolean> IS_QUEST_FINISHED = SynchedEntityData.defineId(VillageTrotterEntity.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Integer> VILLAGE_TROTTER_TYPE = SynchedEntityData.defineId(VillageTrotterEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Boolean> MOVABLE = SynchedEntityData.defineId(VillageTrotterEntity.class, EntityDataSerializers.BOOLEAN);

    // ANIMATION
    protected static final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle_instrument");
    protected static final RawAnimation PLAYING = RawAnimation.begin().thenPlay("playing");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    protected final AnimationController<VillageTrotterEntity> controller = new AnimationController<>(this, "village_trotter_controller", 5, this::musicianState);

    public VillageTrotterEntity(EntityType<? extends AgeableMob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(CORNELIUS_UUID, Optional.empty());
        builder.define(IS_QUEST_FINISHED, false);
        builder.define(VILLAGE_TROTTER_TYPE, 0);
        builder.define(MOVABLE, false);
    }

    private <E extends GeoAnimatable> PlayState musicianState(AnimationState<E> state) {
        if (this.isQuestFinished()) {
            state.getController().setAnimation(PLAYING);
        } else {
            state.getController().setAnimation(IDLE);
        }
        return PlayState.CONTINUE;
    }

    @Override
    protected EntityDimensions getDefaultDimensions(Pose pose) {
        return switch (getVillageTrotterType()) {
            case MANTIS -> EntityDimensions.scalable(1.0F, 2.25F);
            case PENGUIN -> EntityDimensions.scalable(0.75F, 1.0F);
            case RED_PANDA -> EntityDimensions.scalable(0.75F, 1.0F);
            case MACAW -> EntityDimensions.scalable(1.5F, 2.75F);
        };
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);

        if (VILLAGE_TROTTER_TYPE.equals(key)) {
            // Runs on both sides, so the client also gets the new hitbox
            refreshDimensions();

            // The controller keeps the animation baked from the previous model until it is reset
            if (level().isClientSide()) {
                controller.forceAnimationReset();
            }
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);

        this.setQuestFinished(compound.getBoolean("IsQuestFinished"));
        this.setVillageTrotterType(compound.getInt("VillageTrotterType"));
        this.entityData.set(MOVABLE, compound.getBoolean("Movable"));
        if (compound.hasUUID("CorneliusUUID")) {
            this.setCornelius(compound.getUUID("CorneliusUUID"));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);

        if (this.getCorneliusUUID() != null) {
            compound.putUUID("CorneliusUUID", this.getCorneliusUUID());
        }
        compound.putBoolean("IsQuestFinished", this.isQuestFinished());
        compound.putInt("VillageTrotterType", this.getVillageTrotterType().getId());
        compound.putBoolean("Movable", this.entityData.get(MOVABLE));
    }

    public static AttributeSupplier.Builder createMusicianAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 1000d)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FOLLOW_RANGE, 24D);
    }

    @Override
    public void checkDespawn() {

    }

    @Override
    public void knockback(double strength, double x, double z) {
        if (isMovable()) {
            super.knockback(strength, x, z);
        }
    }

    @Override
    public boolean isPushable() {
        return isMovable() && super.isPushable();
    }

    @Override
    public boolean canBeLeashed() {
        return false;
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return null;
    }

    public void setCornelius(Cornelius cornelius) {
        setCornelius(cornelius.getUUID());
    }

    public void setCornelius(@Nullable UUID uuid) {
        this.entityData.set(CORNELIUS_UUID, Optional.ofNullable(uuid));
    }

    public Cornelius getCornelius() {
        if (!level().isClientSide() && level() instanceof ServerLevel serverLevel) {
            return (Cornelius) serverLevel.getEntity(getCorneliusUUID());
        } else {
            return (Cornelius) ((ClientLevelAccessor) level()).callGetEntities().get(getCorneliusUUID());
        }
    }

    public UUID getCorneliusUUID() {
        return this.entityData.get(CORNELIUS_UUID).orElse(null);
    }

    public void setQuestFinished(boolean questFinished) {
        this.entityData.set(IS_QUEST_FINISHED, questFinished);
    }

    public boolean isQuestFinished() {
        return this.entityData.get(IS_QUEST_FINISHED);
    }

    public VillageTrotter getVillageTrotterType() {
        return VillageTrotter.byId(this.entityData.get(VILLAGE_TROTTER_TYPE));
    }

    public void setVillageTrotterType(VillageTrotter villageTrotterType) {
        setVillageTrotterType(villageTrotterType.getId());
    }

    public void setVillageTrotterType(int id) {
        this.entityData.set(VILLAGE_TROTTER_TYPE, id);
    }

    public void setMovable(boolean movable) {
        this.entityData.set(MOVABLE, movable);
    }

    public boolean isMovable() {
        return this.entityData.get(MOVABLE);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(controller);
    }

    public enum VillageTrotter {
        MANTIS(0),
        RED_PANDA(1),
        MACAW(2),
        PENGUIN(3);

        private static final VillageTrotter[] BY_ID = Arrays.stream(values()).sorted(
                Comparator.comparingInt(VillageTrotter::getId)).toArray(VillageTrotter[]::new);
        private final int id;

        VillageTrotter(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static VillageTrotter byId(int id) {
            return BY_ID[id % BY_ID.length];
        }
    }
}

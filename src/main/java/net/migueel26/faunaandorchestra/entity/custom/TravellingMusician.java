package net.migueel26.faunaandorchestra.entity.custom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import oshi.util.tuples.Pair;

public abstract class TravellingMusician extends AgeableMob implements TalkableEntity {
    protected static final EntityDataAccessor<Boolean> IS_PLAYING = SynchedEntityData.defineId(TravellingMusician.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Integer> DIALOGUE_TIMER = SynchedEntityData.defineId(TravellingMusician.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Boolean> MOVABLE = SynchedEntityData.defineId(TravellingMusician.class, EntityDataSerializers.BOOLEAN);

    protected TravellingMusician(EntityType<? extends AgeableMob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(IS_PLAYING, false);
        builder.define(DIALOGUE_TIMER, 0);
        builder.define(MOVABLE, false);
    }

    public static AttributeSupplier.Builder createMusicianAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 1000d)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FOLLOW_RANGE, 24D);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        this.entityData.set(MOVABLE, compound.getBoolean("Movable"));
        super.readAdditionalSaveData(compound);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        compound.putBoolean("Movable", this.entityData.get(MOVABLE));
        super.addAdditionalSaveData(compound);
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

    public void setPlaying(boolean playing) {
        this.entityData.set(IS_PLAYING, playing);
    }

    public boolean isPlaying() {
        return this.entityData.get(IS_PLAYING);
    }

    public void setMovable(boolean movable) {
        this.entityData.set(MOVABLE, movable);
    }

    public boolean isMovable() {
        return this.entityData.get(MOVABLE);
    }

    public abstract void setConfidence(int confidence);
    public abstract int getConfidence();
}

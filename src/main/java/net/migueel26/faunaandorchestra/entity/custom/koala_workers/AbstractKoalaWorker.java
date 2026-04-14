package net.migueel26.faunaandorchestra.entity.custom.koala_workers;

import net.migueel26.faunaandorchestra.block.ModBlocks;
import net.migueel26.faunaandorchestra.entity.custom.ConductorEntity;
import net.migueel26.faunaandorchestra.entity.custom.ListeningEntity;
import net.migueel26.faunaandorchestra.entity.custom.TalkableEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import oshi.util.tuples.Pair;
import software.bernie.geckolib.animatable.GeoEntity;

import java.util.Optional;

public abstract class AbstractKoalaWorker extends AgeableMob implements Npc, TalkableEntity, ListeningEntity, GeoEntity {
    // WORKING
    protected static final EntityDataAccessor<Integer> WORK_TIME = SynchedEntityData.defineId(AbstractKoalaWorker.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<BlockPos> WORKING_STATION = SynchedEntityData.defineId(AbstractKoalaWorker.class, EntityDataSerializers.BLOCK_POS);
    protected static final EntityDataAccessor<Boolean> SLEEPING = SynchedEntityData.defineId(AbstractKoalaWorker.class, EntityDataSerializers.BOOLEAN);
    protected BlockPos workingStation;
    protected ConductorEntity conductor;
    protected int workTime;
    // TALKABLE ENTITY
    protected static final EntityDataAccessor<Integer> DIALOGUE_TIMER = SynchedEntityData.defineId(AbstractKoalaWorker.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Boolean> GOOD_MORNING = SynchedEntityData.defineId(AbstractKoalaWorker.class, EntityDataSerializers.BOOLEAN);
    public String currentDialogue;
    protected AbstractKoalaWorker(EntityType<? extends AgeableMob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SLEEPING, true);
        builder.define(WORKING_STATION, BlockPos.ZERO);
        builder.define(WORK_TIME, 0);
        builder.define(DIALOGUE_TIMER, 0);
        builder.define(GOOD_MORNING, true);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        Optional<ConductorEntity> conductor = level.getEntitiesOfClass(ConductorEntity.class,
                this.getBoundingBox().inflate(50.0, 50.0, 50.0), ConductorEntity::isConducting).stream().findAny();
        if (conductor.isPresent()) {
            this.onStartListening(conductor.get());
        }
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (key.equals(WORKING_STATION)) {
            BlockPos pos = this.entityData.get(WORKING_STATION);
            this.workingStation = pos.equals(BlockPos.ZERO) || !isWorkingStation(level().getBlockState(pos)) ? null : pos;
        }
        if (key.equals(WORK_TIME)) {
            this.workTime = this.entityData.get(WORK_TIME);
        }
        super.onSyncedDataUpdated(key);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        NbtUtils.readBlockPos(compound, "WorkingStation").ifPresent(pos -> this.entityData.set(WORKING_STATION, pos));
        this.entityData.set(WORK_TIME, compound.getInt("WorkTime"));
        super.readAdditionalSaveData(compound);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        if (workingStation != null && isWorkingStation(level().getBlockState(workingStation))) {
            compound.put("WorkingStation", NbtUtils.writeBlockPos(this.workingStation));
        }
        compound.putInt("WorkTime", getWorkTime());
        super.addAdditionalSaveData(compound);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 15d)
                .add(Attributes.MOVEMENT_SPEED, 0.2D)
                .add(Attributes.FOLLOW_RANGE, 24D);
    }

    @Override
    public boolean isPushable() {
        return !hasWorkingStation() || isInLunchBreak();
    }

    @Override
    public void knockback(double strength, double x, double z) {
        if (!hasWorkingStation() || isInLunchBreak()) {
            super.knockback(strength, x, z);
        }
    }

    @Override
    public boolean isPersistenceRequired() {
        return true;
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

    @Override
    public void setGoodMorning(boolean goodMorning) {
        entityData.set(GOOD_MORNING, goodMorning);
    }

    @Override
    public boolean getGoodMorning() {
        return entityData.get(GOOD_MORNING);
    }

    public void setWorkingStation(BlockPos workingStation) {
        this.entityData.set(WORKING_STATION, workingStation);
        this.workingStation = workingStation == BlockPos.ZERO ? null : workingStation;
    }

    public boolean hasWorkingStation() {
        return workingStation != null;
    }

    public BlockPos getWorkingStation() {
        return workingStation;
    }

    public boolean isKoalaSleeping() {
        return entityData.get(SLEEPING);
    }

    public void setKoalaSleeping(boolean isSleeping) {
        this.entityData.set(SLEEPING, isSleeping);
    }

    public int getWorkTime() {
        return workTime;
    }

    public void increaseWorkTime() {
        this.workTime++;
        entityData.set(WORK_TIME, workTime);
    }

    public void resetWorkTime() {
        this.workTime = 0;
        entityData.set(WORK_TIME, workTime);
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
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return null;
    }

    public abstract boolean isInLunchBreak();

    public abstract boolean isWorking();

    public abstract boolean isWorkingStation(BlockState state);
}

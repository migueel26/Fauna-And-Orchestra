package net.migueel26.faunaandorchestra.entity.custom;

import net.migueel26.faunaandorchestra.block.entity.ListenerContainerBlockEntity;
import net.migueel26.faunaandorchestra.block.entity.TermiteChestBlockEntity;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.migueel26.faunaandorchestra.util.BlocksUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public class TermiteEntity extends AgeableMob implements GeoEntity {
    // WORKING
    protected static final EntityDataAccessor<Boolean> HAS_LIQUID = SynchedEntityData.defineId(TermiteEntity.class, EntityDataSerializers.BOOLEAN);
    public static final float SPEED_MODIFIER = 0.85f;
    protected BlockPos originPos = null;
    protected BlockPos destinyPos = null;
    protected boolean isGoingToListener = true;
    private int pathUpdateCountdown = 0;

    // GECKO
    protected final static RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    protected final static RawAnimation WALK = RawAnimation.begin().thenPlay("walk");
    private final AnimationController<TermiteEntity> controller = new AnimationController<>(this, "termite_controller", 0, this::termiteState);
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public TermiteEntity(EntityType<? extends AgeableMob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(HAS_LIQUID, false);
        super.defineSynchedData(builder);
    }

    private <E extends GeoAnimatable> PlayState termiteState(AnimationState<E> state) {
        if (state.isMoving()) {
            state.getController().setAnimation(WALK);
        } else {
            state.getController().setAnimation(IDLE);
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void tick() {

        if (!level().isClientSide()) {
            if (this.isGoingToListener) {
                this.goToListener();
            } else {
                this.goToChest();
            }
        }

        super.tick();
    }

    private void goToListener() {
        double dist = this.distanceToSqr(destinyPos.getCenter());

        if (dist < 1.5D) {
            // Arriving at the listener
            this.getNavigation().stop();

            this.getLookControl().setLookAt(this.destinyPos.getX() + 0.5D, this.destinyPos.getY() + 0.5D, this.destinyPos.getZ() + 0.5D, 30.0F, 30.0F);

            // Check if the listener container is full
            if (level().getBlockEntity(destinyPos) instanceof ListenerContainerBlockEntity listenerBE
                && listenerBE.isFull()) {

                listenerBE.resetDroplets();
                this.setHasLiquid(true);

                level().playSound(null, blockPosition(), ModSounds.CAULDRON_ITEM.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
                BlocksUtil.magicSoundParticles((ServerLevel) level(), destinyPos, 0.1f);
            }

            this.setGoingToListener(false);

        } else {
            this.getLookControl().setLookAt(this.destinyPos.getX() + 0.5D, this.destinyPos.getY() + 0.5D, this.destinyPos.getZ() + 0.5D, 10.0F, (float)this.getMaxHeadXRot());

            if (--this.pathUpdateCountdown <= 0) {
                this.pathUpdateCountdown = 10;

                this.getNavigation().moveTo(this.destinyPos.getCenter().x(), this.destinyPos.getY(), this.destinyPos.getCenter().z(), SPEED_MODIFIER);
            }
        }
    }

    private void goToChest() {
        double dist = this.distanceToSqr(originPos.getCenter());

        if (dist < 1.5D) {
            // Arriving at the chest
            this.getNavigation().stop();

            if (level().getBlockEntity(originPos) instanceof TermiteChestBlockEntity termiteChestBE) {
                termiteChestBE.workDone(this);
            }

        } else {
            this.getLookControl().setLookAt(this.originPos.getX() + 0.5D, this.originPos.getY() + 0.5D, this.originPos.getZ() + 0.5D, 10.0F, (float) this.getMaxHeadXRot());

            if (--this.pathUpdateCountdown <= 0) {
                this.pathUpdateCountdown = 10;

                this.getNavigation().moveTo(this.originPos.getCenter().x(), this.originPos.getY(), this.originPos.getCenter().z(), SPEED_MODIFIER);
            }
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.entityData.set(HAS_LIQUID, compound.getBoolean("HasLiquid"));
        this.originPos = compound.contains("OriginPos") ? BlockPos.of(compound.getLong("OriginPos")) : null;
        this.destinyPos = compound.contains("DestinyPos") ? BlockPos.of(compound.getLong("DestinyPos")) : null;
        this.isGoingToListener = compound.getBoolean("GoingToListener");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("HasLiquid", this.entityData.get(HAS_LIQUID));
        compound.putBoolean("GoingToListener", this.isGoingToListener);
        if (this.originPos != null) {
            compound.putLong("OriginPos", this.originPos.asLong());
        }
        if (this.destinyPos != null) {
            compound.putLong("DestinyPos", this.destinyPos.asLong());
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 1000d)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FOLLOW_RANGE, 24D);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    @Override
    public void checkDespawn() {

    }

    @Override
    public void knockback(double strength, double x, double z) {

    }

    @Override
    public boolean canBeLeashed() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    public boolean hasLiquid() {
        return this.entityData.get(HAS_LIQUID);
    }

    public void setHasLiquid(boolean hasLiquid) {
        this.entityData.set(HAS_LIQUID, hasLiquid);
    }

    public BlockPos getOriginPos() {
        return this.originPos;
    }

    public void setOriginPos(BlockPos originPos) {
        this.originPos = originPos;
    }

    public BlockPos getDestinyPos() {
        return this.destinyPos;
    }

    public void setDestinyPos(BlockPos destinyPos) {
        this.destinyPos = destinyPos;
    }

    public boolean isGoingToListener() {
        return this.isGoingToListener;
    }

    public void setGoingToListener(boolean isGoingToListener) {
        this.isGoingToListener = isGoingToListener;
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return null;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(controller);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
}

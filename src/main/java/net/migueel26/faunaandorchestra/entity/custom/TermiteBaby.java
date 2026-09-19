package net.migueel26.faunaandorchestra.entity.custom;

import net.migueel26.faunaandorchestra.block.ModBlocks;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public class TermiteBaby extends AgeableMob implements GeoEntity {
    private final int SCHEDULED_DEATH = 20;

    protected static final EntityDataAccessor<Boolean> IS_FOUND = SynchedEntityData.defineId(TermiteBaby.class, EntityDataSerializers.BOOLEAN);
    private static final RawAnimation WALK = RawAnimation.begin().thenPlay("walk");
    private static final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    private static final RawAnimation SUBMERGE = RawAnimation.begin().thenPlay("submerge");
    private int deathTimer;

    private final AnimationController<TermiteBaby> termiteBabyController = new AnimationController<>(this, "termite_baby_controller", 2, this::termiteBabyState)
            .triggerableAnim("submerge", SUBMERGE);
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public TermiteBaby(EntityType<? extends AgeableMob> entityType, Level level) {
        super(entityType, level);

        this.deathTimer = -1;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(IS_FOUND, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 6.0f));
        this.goalSelector.addGoal(2, new RandomLookAroundGoal(this));
    }

    private <E extends GeoAnimatable> PlayState termiteBabyState(AnimationState<E> state) {
        if (state.isMoving()) {
            state.getController().setAnimation(WALK);
        } else {
            state.getController().setAnimation(IDLE);
        }
        return PlayState.CONTINUE;
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        player.displayClientMessage(Component.translatable("text.faunaandorchestra.termite_baby_lost"), true);
        return InteractionResult.SUCCESS;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide() && this.tickCount % 2 == 0) {
            if (!isFound()) {
                spawnTears();
            }
        }

        if (deathTimer == SCHEDULED_DEATH) {
            this.discard();
        } else if (deathTimer >= 0) {
            if (deathTimer % 2 == 0) {
                ((ServerLevel) level()).sendParticles(
                        new BlockParticleOption(ParticleTypes.BLOCK, ModBlocks.TERMITE_MOUND.get().defaultBlockState()),
                        position().x, position().y, position().z,
                        10, 0.2f, 0.2f, 0.2f, 0.2f
                );
            }
            if (deathTimer % 5 == 0) {
                this.playSound(SoundEvents.ROOTED_DIRT_BREAK, 1.0F, 1.0F + (random.nextFloat() - 0.5f));
            }
            deathTimer++;
        }
    }

    private void spawnTears() {
        double eyeX = this.getX();
        double eyeY = this.getY() + this.getEyeHeight();
        double eyeZ = this.getZ();

        float yRot = this.yBodyRot * ((float) Math.PI / 180F);

        double fwdX = -Mth.sin(yRot);
        double fwdZ = Mth.cos(yRot);

        double rightX = Mth.cos(yRot);
        double rightZ = Mth.sin(yRot);

        // TEAR PARAMS
        double eyeSeparation = 0.2;
        double speedUp = 0.3;
        double speedForward = 0.2;
        double speedSide = 0.15;

        // LEFT EYE TEAR
        this.level().addParticle(ParticleTypes.SPLASH,
                eyeX - rightX * eyeSeparation, eyeY, eyeZ - rightZ * eyeSeparation,
                fwdX * speedForward - rightX * speedSide, speedUp, fwdZ * speedForward - rightZ * speedSide
        );

        // RIGHT EYE TEAR
        this.level().addParticle(ParticleTypes.SPLASH,
                eyeX + rightX * eyeSeparation, eyeY, eyeZ + rightZ * eyeSeparation,
                fwdX * speedForward + rightX * speedSide, speedUp, fwdZ * speedForward + rightZ * speedSide
        );
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("IsFound", this.entityData.get(IS_FOUND));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("IsFound")) {
            this.entityData.set(IS_FOUND, compound.getBoolean("IsFound"));
        }
    }

    public static AttributeSupplier.Builder createTermiteBabyAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 1000d)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FOLLOW_RANGE, 24D);
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return isFound() ? null : ModSounds.TERMITE_CRY.get();
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

    public boolean isFound() {
        return this.entityData.get(IS_FOUND);
    }

    public void setFound(boolean found) {
        this.entityData.set(IS_FOUND, found);
    }

    public void submerge() {
        if (deathTimer == -1) {
            deathTimer = 0;
        }
        triggerAnim("termite_baby_controller", "submerge");
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return null;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(termiteBabyController);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
}

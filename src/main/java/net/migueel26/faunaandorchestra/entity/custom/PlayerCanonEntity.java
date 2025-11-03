package net.migueel26.faunaandorchestra.entity.custom;

import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Optional;
import java.util.UUID;

public class PlayerCanonEntity extends AbstractCanonEntity {
    public static final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    public static final RawAnimation WALK = RawAnimation.begin().thenPlay("walk");
    public static final RawAnimation ATTACK = RawAnimation.begin().thenPlay("attack");
    protected static final EntityDataAccessor<Optional<UUID>> CONDUCTOR_UUID = SynchedEntityData.defineId(PlayerCanonEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    protected UUID conductorUUID;
    protected int scheduleDeath = -1;
    private final AnimationController<PlayerCanonEntity> canonController = new AnimationController<>(this, "player_canon_controller", 5, this::canonState)
            .triggerableAnim("attack", ATTACK);
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    public PlayerCanonEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.0, true) {
            @Override
            protected void checkAndPerformAttack(LivingEntity target) {
                if (this.canPerformAttack(target)) {
                    this.resetAttackCooldown();
                    this.mob.swing(InteractionHand.MAIN_HAND);
                    this.mob.doHurtTarget(target);
                    ((PlayerCanonEntity) mob).triggerAnim("player_canon_controller", "attack");
                }
            }
        });
        this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.0, 10.0F, 2.0F));
        this.goalSelector.addGoal(7, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(7, new NearestAttackableTargetGoal<Monster>(this, Monster.class, false));
    }

    private <E extends GeoAnimatable> PlayState canonState(AnimationState<E> state) {
        if (state.isMoving()) {
            state.getController().setAnimation(WALK);
        } else {
            state.getController().setAnimation(IDLE);
        }
        return PlayState.CONTINUE;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        InteractionResult interactionresult = super.mobInteract(player, hand);
        if (!interactionresult.consumesAction() && this.isOwnedBy(player)) {
            this.setOrderedToSit(!this.isOrderedToSit());
            this.jumping = false;
            this.navigation.stop();
            this.setTarget((LivingEntity)null);
            return InteractionResult.SUCCESS_NO_ITEM_USED;
        } else {
            return interactionresult;
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(CONDUCTOR_UUID, Optional.empty());
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (key.equals(CONDUCTOR_UUID)) {
            this.conductorUUID = entityData.get(CONDUCTOR_UUID).orElse(null);
        }
        super.onSyncedDataUpdated(key);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        compound.putUUID("ConductorUUID", conductorUUID);
        super.addAdditionalSaveData(compound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        if (compound.hasUUID("ConductorUUID")) {
            entityData.set(CONDUCTOR_UUID, Optional.of(compound.getUUID("ConductorUUID")));
        }
        super.readAdditionalSaveData(compound);
    }


    @Override
    public void tick() {
        if (level().isClientSide() && skin == null) {
            if (getOwner() != null) {
                this.setSkin(((AbstractClientPlayer) getOwner()).getSkin());
                this.setCustomName(getOwner() == null ? getName() : getOwner().getDisplayName());
            }
        }


        if (this.tickCount % 20 == 0) {
            if (scheduleDeath == -1 && !level().isClientSide() && ((ServerLevel) level()).getEntity(conductorUUID) instanceof ConductorEntity conductor
                && ((conductor.isAlive() && !conductor.isConducting()) || (conductor.isDeadOrDying()))) {
                // If the conductor is dying / has stopped conducting
                this.scheduleDeath = 3;
                ((ServerLevel) level()).sendParticles(ParticleTypes.CLOUD, position().x, blockPosition().getCenter().y, position().z, 30, 0.1, 0.1, 0.1, 0.2);
            }
        }

        if (scheduleDeath == 0) {
            this.discard();
        } else if (scheduleDeath > 0) {
            scheduleDeath--;
        }
        super.tick();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 50d)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FOLLOW_RANGE, 24D)
                .add(Attributes.ATTACK_DAMAGE, 7.0);
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.PLAYER_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PLAYER_DEATH;
    }

    public void setConductor(ConductorEntity conductor) {
        this.conductorUUID = conductor.getUUID();
    }

    public UUID getConductorUUID() {
        return conductorUUID;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(canonController);
    }


    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return false;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return null;
    }
}

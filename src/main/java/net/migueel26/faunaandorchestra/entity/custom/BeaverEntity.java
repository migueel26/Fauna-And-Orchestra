package net.migueel26.faunaandorchestra.entity.custom;

import net.migueel26.faunaandorchestra.entity.ModEntities;
import net.migueel26.faunaandorchestra.entity.goals.AnimalEatGoal;
import net.migueel26.faunaandorchestra.entity.goals.BeaverBuildsDamGoal;
import net.migueel26.faunaandorchestra.entity.goals.FaunaRandomLookAroundGoal;
import net.migueel26.faunaandorchestra.entity.goals.MusicalEntityPlayingInstrumentGoal;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
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

public class BeaverEntity extends MusicalEntity implements GeoEntity {
    protected static final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    protected static final RawAnimation IDLE_SAXOPHONE = RawAnimation.begin().thenPlay("holding_sax");
    protected static final RawAnimation WALKING = RawAnimation.begin().thenPlay("walk");
    protected static final RawAnimation SWIMMING = RawAnimation.begin().thenPlay("swim");
    protected static final RawAnimation WALKING_SAXOPHONE = RawAnimation.begin().thenPlay("walk_sax");
    protected static final RawAnimation PLAYING = RawAnimation.begin().thenPlay("playing");
    protected static final RawAnimation BUILD = RawAnimation.begin().thenPlay("build");
    protected static final EntityDataAccessor<Boolean> CAN_BUILD = SynchedEntityData.defineId(BeaverEntity.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Boolean> BUILDING = SynchedEntityData.defineId(BeaverEntity.class, EntityDataSerializers.BOOLEAN);
    private final AnimationController<BeaverEntity> beaverController = new AnimationController<>(this, "beaver_controller", 5, this::beaverState)
            .triggerableAnim("build_trigger", BUILD);
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    int bubbleTick = 0;
    Vec3 lastPosition = position();
    public BeaverEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);

        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER_BORDER, 0.0F);
        this.addOverridenGoals();
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(BUILDING, false);
        this.entityData.define(CAN_BUILD, true);
        super.defineSynchedData();
    }

    @Override
    public RegistryObject<Item> getInstrument() {
        return ModItems.SAXOPHONE;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this,2D));
        this.goalSelector.addGoal(1, new MusicalEntityPlayingInstrumentGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        // LookAtPlayerGoal (3)
        this.goalSelector.addGoal(3, new BeaverBuildsDamGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new AnimalEatGoal(this, Items.OAK_LOG, this::onEat));
        // RandomStrollGoal (4)
        this.goalSelector.addGoal(5, new FaunaRandomLookAroundGoal(this));

    }

    protected void addOverridenGoals() {
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F) {
            @Override
            public boolean canUse() {
                return super.canUse() && !((MusicalEntity) mob).isPlayingInstrument()
                        && !((BeaverEntity) mob).isBuilding();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && !((MusicalEntity) mob).isPlayingInstrument()
                        && !((BeaverEntity) mob).isBuilding();
            }
        });

        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, TravellingMusician.class, 6.0F) {
            @Override
            public boolean canUse() {
                return super.canUse() && !((MusicalEntity) mob).isHoldingInstrument()
                        && !((BeaverEntity) mob).isBuilding();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && !((MusicalEntity) mob).isHoldingInstrument()
                        && !((BeaverEntity) mob).isBuilding();
            }
        });

        this.goalSelector.addGoal(4, new RandomStrollGoal(this, 1.0D) {
            @Override
            public boolean canUse() {
                return super.canUse() && !((BeaverEntity) mob).isBuilding();
            }
        });

        this.goalSelector.addGoal(3, new BreedGoal(this, 1.0f) {
            @Override
            public boolean canUse() {
                return super.canUse() && !((BeaverEntity) animal).isBuilding() && !((BeaverEntity) animal).isHoldingInstrument();
            }
        });
    }

    private <E extends GeoAnimatable> PlayState beaverState(AnimationState<E> state) {
        if (isPlayingInstrument()) {
            state.getController().setAnimation(PLAYING);
        } else if (state.isMoving() && !isInWater()) {
            state.getController().setAnimation(isHoldingInstrument() ? WALKING_SAXOPHONE : WALKING);
        } else if (state.isMoving() && isInWater()) {
            state.getController().setAnimation(SWIMMING);
        } else if (isHoldingInstrument()) {
            state.getController().setAnimation(IDLE_SAXOPHONE);
        } else {
            state.getController().setAnimation(IDLE);
        }
        return PlayState.CONTINUE;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 15d)
                .add(Attributes.MOVEMENT_SPEED, 0.2D)
                .add(ForgeMod.SWIM_SPEED.get(),1.0D)
                .add(Attributes.FOLLOW_RANGE, 24D);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        if (compound.contains("CanBuild")) {
            this.entityData.set(CAN_BUILD, compound.getBoolean("CanBuild"));
        }
        super.readAdditionalSaveData(compound);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        compound.putBoolean("CanBuild", canBuild());
        super.addAdditionalSaveData(compound);
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.AXOLOTL_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.AXOLOTL_DEATH;
    }

    @Override
    public void aiStep() {
        if (level().isClientSide() && isInWater() && bubbleTick >= 10
                && (lastPosition.x != getX() || lastPosition.z != getZ())) {
            doWaterSplashEffect();
            bubbleTick = 0;
        } else {
            bubbleTick++;
        }
        lastPosition = position();
        super.aiStep();
    }

    public void build() {
        setBuilding(true);
        triggerAnim("beaver_controller", "build_trigger");
    }

    public boolean isBuilding() {
        return entityData.get(BUILDING);
    }

    public void setBuilding(boolean building) {
        entityData.set(BUILDING, building);
    }

    public boolean canBuild() {
        return entityData.get(CAN_BUILD);
    }

    public void setCanBuild(boolean canBuild) {
        this.entityData.set(CAN_BUILD, canBuild);
    }

    @Override
    public float getScale() {
        return this.isBaby() ? 0.7F : 1.0F;
    }

    public @NotNull EntityDimensions getDimensions(Pose pose) {
        return this.isBaby() ? ModEntities.BEAVER.get().getDimensions().scale(0.7f) : super.getDimensions(pose);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(Items.OAK_LOG);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return createBaby(ModEntities.BEAVER.get(), (BeaverEntity) otherParent);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(beaverController);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
}

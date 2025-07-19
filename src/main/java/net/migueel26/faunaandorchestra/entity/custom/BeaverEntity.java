package net.migueel26.faunaandorchestra.entity.custom;

import net.migueel26.faunaandorchestra.entity.goals.BeaverBuildsDamGoal;
import net.migueel26.faunaandorchestra.entity.goals.FaunaRandomLookAroundGoal;
import net.migueel26.faunaandorchestra.entity.goals.MusicalEntityPlayingInstrumentGoal;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public class BeaverEntity extends MusicalEntity implements GeoEntity {
    protected static final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    protected static final RawAnimation IDLE_SAXOPHONE = RawAnimation.begin().thenPlay("holding_sax");
    protected static final RawAnimation WALKING = RawAnimation.begin().thenPlay("walk");
    protected static final RawAnimation SWIMMING = RawAnimation.begin().thenPlay("swim");
    protected static final RawAnimation WALKING_SAXOPHONE = RawAnimation.begin().thenPlay("walk_sax");
    protected static final RawAnimation PLAYING = RawAnimation.begin().thenPlay("playing");
    protected static final RawAnimation BUILD = RawAnimation.begin().thenPlay("build");
    protected static final EntityDataAccessor<Boolean> BUILDING = SynchedEntityData.defineId(BeaverEntity.class, EntityDataSerializers.BOOLEAN);
    private final AnimationController<BeaverEntity> beaverController = new AnimationController<>(this, "beaver_controller", 5, this::beaverState)
            .triggerableAnim("build_trigger", BUILD);
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    int bubbleTick = 0;
    Vec3 lastPosition = position();
    public BeaverEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);

        this.addOverridenGoals();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(BUILDING, false);
    }

    @Override
    public DeferredItem<Item> getInstrument() {
        return ModItems.SAXOPHONE;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new TamableAnimal.TamableAnimalPanicGoal(1.25D));
        this.goalSelector.addGoal(1, new MusicalEntityPlayingInstrumentGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        // LookAtPlayerGoal (3)
        this.goalSelector.addGoal(3, new BeaverBuildsDamGoal(this, 1.0D));
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

        this.goalSelector.addGoal(4, new RandomStrollGoal(this, 1.0D) {
            @Override
            public boolean canUse() {
                return super.canUse() && !((BeaverEntity) mob).isBuilding();
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
                .add(Attributes.WATER_MOVEMENT_EFFICIENCY,1.0D)
                .add(Attributes.FOLLOW_RANGE, 24D);
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

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null;
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

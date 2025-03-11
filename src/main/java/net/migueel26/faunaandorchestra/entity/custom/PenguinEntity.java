package net.migueel26.faunaandorchestra.entity.custom;

import net.migueel26.faunaandorchestra.item.ModItems;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public class PenguinEntity extends MusicalEntity implements GeoEntity {
    protected static final RawAnimation RUN = RawAnimation.begin().thenPlay("run");
    protected static final RawAnimation WADDLE = RawAnimation.begin().thenPlay("waddle");
    protected static final RawAnimation WAVE = RawAnimation.begin().thenPlay("wave");
    protected static final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    private static final EntityDataAccessor<Boolean> IS_WAVING = SynchedEntityData.defineId(PenguinEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_RUNNING = SynchedEntityData.defineId(PenguinEntity.class, EntityDataSerializers.BOOLEAN);
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    public PenguinEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected DeferredItem<Item> getInstrument() {
        // TODO: FLUTE
        return ModItems.VIOLIN;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 2D) {
            final PenguinEntity penguin = (PenguinEntity) super.mob;
            @Override
            public void start() {
                penguin.setRunning(true);
                super.start();
            }

            @Override
            public void stop() {
                penguin.setRunning(false);
                super.stop();
            }
        });
        this.goalSelector.addGoal(4, new RandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0F) {
            @Override
            public void start() {
                PenguinEntity penguin = (PenguinEntity) super.mob;
                penguin.wave();
                super.start();
            }
        });
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }

    private <E extends GeoAnimatable> PlayState penguinState(AnimationState<E> state) {
        if (isWaving()) {
            this.navigation.stop();
            state.getController().setAnimation(WAVE);
            if (state.getController().hasAnimationFinished()) stopWaving();
        } else if (state.isMoving() && isRunning()) {
            state.getController().setAnimation(RUN);
        } else if (state.isMoving()) {
            state.getController().setAnimation(WADDLE);
        } else {
            state.getController().setAnimation(IDLE);
        }
        return PlayState.CONTINUE;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 15d)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FOLLOW_RANGE, 24D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(IS_WAVING, false);
        builder.define(IS_RUNNING, false);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        //TODO: ADD FOOD
        return false;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        //TODO: ADD OFFSPRING
        return null;
    }

    public void wave() {
        entityData.set(IS_WAVING, true);
    }

    public void stopWaving() {
        entityData.set(IS_WAVING, false);
    }

    public boolean isWaving() {
        return entityData.get(IS_WAVING);
    }

    public void setRunning(boolean flag) {
        entityData.set(IS_RUNNING, flag);
    }

    public boolean isRunning() {
        return entityData.get(IS_RUNNING);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "penguin_controller", 5, this::penguinState));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}

package net.migueel26.faunaandorchestra.entity.custom;

import net.migueel26.faunaandorchestra.entity.goals.MusicalEntityPlayingInstrumentGoal;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
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
    protected static final RawAnimation WADDLE_FLUTE = RawAnimation.begin().thenPlay("waddle_flute");
    protected static final RawAnimation WAVE = RawAnimation.begin().thenPlay("wave");
    protected static final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    protected static final RawAnimation IDLE_FLUTE = RawAnimation.begin().thenPlay("holding_flute");
    protected static final RawAnimation PLAYING = RawAnimation.begin().thenPlay("playing");
    private static final EntityDataAccessor<Boolean> IS_WAVING = SynchedEntityData.defineId(PenguinEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_RUNNING = SynchedEntityData.defineId(PenguinEntity.class, EntityDataSerializers.BOOLEAN);
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    public PenguinEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);

        addOverriddenGoals();
    }

    @Override
    public DeferredItem<Item> getInstrument() {
        return ModItems.FLUTE;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        // TamableAnimalPanicGoal(0)
        this.goalSelector.addGoal(1, new MusicalEntityPlayingInstrumentGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        // LookAtPlayerGoal(4)
        // RandomStrollGoal(5)
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }

    private <E extends GeoAnimatable> PlayState penguinState(AnimationState<E> state) {
        if (isPlayingInstrument()) {
            state.getController().setAnimation(PLAYING);
        } else if (isWaving()) {
            state.getController().setAnimation(WAVE);
            if (state.getController().hasAnimationFinished()) stopWaving();
        } else if (isHoldingInstrument()) {
            state.getController().setAnimation(IDLE_FLUTE);
        } else if (state.isMoving() && isRunning()) {
            state.getController().setAnimation(RUN);
        } else if (state.isMoving()) {
            state.getController().setAnimation(isHoldingInstrument() ? WADDLE_FLUTE : WADDLE);
        } else {
            state.getController().setAnimation(IDLE);
        }
        return PlayState.CONTINUE;
    }
    private void addOverriddenGoals() {
        this.goalSelector.addGoal(0, new TamableAnimalPanicGoal(2.0D, DamageTypeTags.PANIC_CAUSES) {
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

        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 6.0F) {
            final PenguinEntity penguin = (PenguinEntity) super.mob;
            @Override
            public void start() {
                penguin.getNavigation().moveTo(penguin.getX(), penguin.getY(), penguin.getZ(), 1.0D);
                this.mob.getLookControl().setLookAt(this.lookAt.getX(), this.lookAt.getEyeY(), this.lookAt.getZ());
                penguin.wave();
                super.start();
            }

            @Override
            public boolean canUse() {
                return super.canUse() && penguin.getNavigation().isDone();
            }
        });

        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 1.0) {
            final PenguinEntity penguin = (PenguinEntity) super.mob;

            @Override
            public boolean canUse() {
                return super.canUse() && !penguin.isWaving();
            }
        });
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
        //TODO: ADD OFFSPRING AND ADULT
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

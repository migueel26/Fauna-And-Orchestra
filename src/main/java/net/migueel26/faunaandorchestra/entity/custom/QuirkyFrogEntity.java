package net.migueel26.faunaandorchestra.entity.custom;

import net.migueel26.faunaandorchestra.entity.goals.ConductorEntityConductingOrchestra;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public class QuirkyFrogEntity extends ConductorEntity implements GeoEntity {
    protected static final RawAnimation WALK = RawAnimation.begin().thenPlay("walk");
    protected static final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    protected static final RawAnimation CONDUCTING = RawAnimation.begin().thenPlay("conducting");
    protected static final RawAnimation HOLDING_BATON = RawAnimation.begin().thenPlay("holding_baton");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public QuirkyFrogEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);

        addOverridenGoals();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 15d)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FOLLOW_RANGE, 24D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new TamableAnimalPanicGoal(1.25D));
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new ConductorEntityConductingOrchestra(this));
        // LookAtPlayerGoal(2);
        this.goalSelector.addGoal(3, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(4, new RandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
    }

    private void addOverridenGoals() {
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 6.0F) {
            private int lookTime;
            @Override
            public boolean canContinueToUse() {
                if (!this.lookAt.isAlive()) {
                    return false;
                } else {
                    return this.mob.distanceToSqr(this.lookAt) > (double)(this.lookDistance * this.lookDistance) ? false : this.lookTime > 0;
                }
            }

            @Override
            public void start() {
                this.lookTime = this.adjustedTickDelay(40 + this.mob.getRandom().nextInt(40));
            }

            @Override
            public void tick() {
                if (this.lookAt.isAlive()) {
                    double eyeOffset = ((ConductorEntity) mob).isHoldingBaton() ? 1F : 2F;
                    double d0 = this.lookAt.getEyeY() - eyeOffset;
                    this.mob.getLookControl().setLookAt(this.lookAt.getX(), d0, this.lookAt.getZ());
                    this.lookTime--;
                }
            }
        });
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null;
    }

    protected <E extends GeoAnimatable> PlayState quirkyFrogState(AnimationState<E> state) {
        if (isConducting()) {
            state.getController().setAnimation(CONDUCTING);
        } else if (isHoldingBaton()) {
            state.getController().transitionLength(0);
            state.getController().setAnimation(HOLDING_BATON);
        } else if (state.isMoving()) {
            state.getController().transitionLength(3);
            state.getController().setAnimation(WALK);
        } else {
            state.getController().setAnimation(IDLE);
        }
        return PlayState.CONTINUE;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!isTame()) {
            this.tame(player);
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "conductor_controller", 5, this::quirkyFrogState));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}

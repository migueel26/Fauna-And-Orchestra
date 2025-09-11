package net.migueel26.faunaandorchestra.entity.custom;

import net.migueel26.faunaandorchestra.entity.goals.FaunaRandomLookAroundGoal;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public class SproutlingEntity extends AgeableMob implements GeoEntity {
    protected static final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    protected static final RawAnimation SING = RawAnimation.begin().thenPlay("sing");
    protected static final RawAnimation WALK = RawAnimation.begin().thenPlay("walk");
    protected static final EntityDataAccessor<Boolean> SINGING = SynchedEntityData.defineId(SproutlingEntity.class, EntityDataSerializers.BOOLEAN);
    public static final int TICKS_UNTIL_SING = 400;
    public static final int MAX_TICKS_SINGING = 42;
    private final AnimationController<SproutlingEntity> sproutlingController = new AnimationController<>(this, "sproutling_controller", 5, this::sproutlingState)
            .triggerableAnim("sing_trigger", SING);
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    protected boolean isSinging;
    protected int ticksUntilSing;
    protected int ticksSinging;
    public SproutlingEntity(EntityType<? extends AgeableMob> entityType, Level level) {
        super(entityType, level);

        this.ticksUntilSing = TICKS_UNTIL_SING;
        this.ticksSinging = -1;
        addOverridenGoals();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);

        builder.define(SINGING, false);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (key.equals(SINGING)) {
            this.isSinging = entityData.get(SINGING);
        }
        super.onSyncedDataUpdated(key);
    }

    @Override
    protected void registerGoals() {
        // 0 - Create Wise Tree
        goalSelector.addGoal(0, new PanicGoal(this, 2.0));
        // WaterAvoidingRandomStroll (1)
        // LookAtPlayerGoal (2)
        goalSelector.addGoal(3, new FaunaRandomLookAroundGoal(this));
    }
    private void addOverridenGoals() {
        goalSelector.addGoal(1, new WaterAvoidingRandomStrollGoal(this, 1.0) {
            @Override
            public boolean canUse() {
                return super.canUse() && !((SproutlingEntity) mob).isSinging();
            }
        });
        goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 8.0F) {
            @Override
            public boolean canUse() {
                return super.canUse() && !((SproutlingEntity) mob).isSinging();
            }
        });
    }

    private <E extends GeoAnimatable> PlayState sproutlingState(AnimationState<E> state) {
        if (state.isMoving()) {
            state.getController().setAnimation(WALK);
        } else {
            state.getController().setAnimation(IDLE);
        }
        return PlayState.CONTINUE;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 8.0d)
                .add(Attributes.MOVEMENT_SPEED, 0.2D)
                .add(Attributes.FOLLOW_RANGE, 24D);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(sproutlingController);
    }

    public boolean isSinging() {
        return isSinging;
    }

    public void setSinging(boolean singing) {
        entityData.set(SINGING, singing);
        this.isSinging = singing;
    }

    @Override
    public void tick() {
        if (ticksUntilSing > 0) {
            ticksUntilSing--;
        } else if (!isSinging() && navigation.isDone()) {
            this.setSinging(true);
            triggerAnim("sproutling_controller", "sing_trigger");
            this.ticksSinging = 0;


        }

        if (ticksSinging == MAX_TICKS_SINGING / 3 && !level().isClientSide) {
            ((ServerLevel) level()).sendParticles(ParticleTypes.NOTE,
                    this.getX(), this.getY() + 1.0F, this.getZ(),
                    1, 0, 0, 0, 1);
        }

        if (ticksSinging >= 0 && ticksSinging < MAX_TICKS_SINGING) {
            ticksSinging++;
        } else if (isSinging()) {
            // We bonemeal the block beneath the sproutling and play sound + particles
            if (!level().isClientSide()) {
                BlockState state = level().getBlockState(blockPosition().below());
                if (state.getBlock() instanceof BonemealableBlock block) {
                    block.performBonemeal((ServerLevel) level(), random, blockPosition().below(), state);
                }

                level().playSound(null, getX(), getY(), getZ(), ModSounds.MAGIC_GROWTH.get(), SoundSource.NEUTRAL);
                ((ServerLevel) level()).sendParticles(ParticleTypes.HAPPY_VILLAGER,
                        this.getX(), this.getY(), this.getZ(),
                        60, 1.5, 1, 5, 0.3);
            }
            this.setSinging(false);
            this.ticksUntilSing = TICKS_UNTIL_SING;
            this.ticksSinging = -1;
        }
        super.tick();
    }

    @Override
    public boolean isPersistenceRequired() {
        return true;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
}

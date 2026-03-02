package net.migueel26.faunaandorchestra.entity.custom;

import net.migueel26.faunaandorchestra.entity.custom.variants.ButterflyVariant;
import net.migueel26.faunaandorchestra.entity.custom.variants.MantisVariant;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.AirAndWaterRandomPos;
import net.minecraft.world.entity.ai.util.HoverRandomPos;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class ButterflyEntity extends Animal implements FlyingAnimal, GeoEntity, VariantEntity<ButterflyVariant> {
    public int scheduleDeath = -1;
    protected final static RawAnimation FLY = RawAnimation.begin().thenPlay("fly");
    protected final static RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    public static final int TICKS_PER_FLAP = 5;
    protected static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(ButterflyEntity.class, EntityDataSerializers.INT);
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    public ButterflyEntity(EntityType<? extends Animal> p_27403_, Level p_27404_) {
        super(p_27403_, p_27404_);
        this.moveControl = new FlyingMoveControl(this, 20, true);
        this.setPathfindingMalus(PathType.DANGER_FIRE, -1.0F);
        this.setPathfindingMalus(PathType.DAMAGE_FIRE, -1.0F);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new ButterflyWanderGoal());
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.5F));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(VARIANT, 0);
        super.defineSynchedData(builder);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("Variant", this.getVariantId());
        super.addAdditionalSaveData(compound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        this.entityData.set(VARIANT, compound.getInt("Variant"));
        super.readAdditionalSaveData(compound);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @org.jetbrains.annotations.Nullable SpawnGroupData spawnGroupData) {
        ButterflyVariant variant = Util.getRandom(ButterflyVariant.values(), this.random);
        this.setVariant(variant);
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    private <E extends GeoAnimatable> PlayState butterflyState(AnimationState<E> state) {
        if (isFlying() || state.isMoving()) {
            state.getController().setAnimation(FLY);
        } else {
            state.getController().setAnimation(IDLE);
        }
        return PlayState.CONTINUE;
    }

    @Override
    protected void pushEntities() {

    }

    @Override
    public void push(Entity entity) {

    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(ModItems.BUTTERFLY_NET) && scheduleDeath == -1) {
            if (!level().isClientSide()) {
                ((ServerLevel) level()).sendParticles(ParticleTypes.CLOUD,
                        getX(), getY(), getZ(),
                        5, 0, 0, 0, 0.05);
            }
            level().playSound(null, blockPosition(), SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL, 1.0f, 0.75f);

            if (hand.equals(InteractionHand.MAIN_HAND)) {
                stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
            } else {
                stack.hurtAndBreak(1, player, EquipmentSlot.OFFHAND);
            }

            player.addItem(new ItemStack(ModItems.BUTTERFLY_SPAWN_EGG.get(), 1));
            this.scheduleDeath = 3;
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 3.0)
                .add(Attributes.FLYING_SPEED, 0.6F)
                .add(Attributes.MOVEMENT_SPEED, 0.3F)
                .add(Attributes.ATTACK_DAMAGE, 2.0)
                .add(Attributes.FOLLOW_RANGE, 48.0);
    }

    @Override
    public void tick() {
        if (scheduleDeath == 0) {
            this.discard();
        } else if (scheduleDeath > 0) {
            scheduleDeath--;
        }
        super.tick();
    }

    @Override
    protected PathNavigation createNavigation(Level p_level) {
        FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, p_level) {
            @Override
            public boolean isStableDestination(BlockPos p_27947_) {
                return !this.level.getBlockState(p_27947_.below()).isAir();
            }

        };
        flyingpathnavigation.setCanOpenDoors(false);
        flyingpathnavigation.setCanFloat(false);
        flyingpathnavigation.setCanPassDoors(true);
        return flyingpathnavigation;
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader level) {
        return level.getBlockState(pos).isAir() ? 10.0F : 0.0F;
    }

    @Override
    public boolean canBeLeashed() {
        return false;
    }

    @Override
    public boolean isFlapping() {
        return this.isFlying() && this.tickCount % TICKS_PER_FLAP == 0;
    }

    @Override
    public boolean isFlying() {
        return !this.onGround();
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "butterfly_controller", 5, this::butterflyState));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    @Override
    public int getVariantId() {
        return this.entityData.get(VARIANT);
    }

    @Override
    public ButterflyVariant getVariant() {
        return ButterflyVariant.byId(getVariantId() & 255);
    }

    @Override
    public void setVariant(ButterflyVariant variant) {
        this.entityData.set(VARIANT, variant.getId());
    }

    class ButterflyWanderGoal extends Goal {
        private static final int WANDER_THRESHOLD = 22;

        ButterflyWanderGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return ButterflyEntity.this.navigation.isDone() && ButterflyEntity.this.random.nextInt(10) == 0;
        }

        @Override
        public boolean canContinueToUse() {
            return ButterflyEntity.this.navigation.isInProgress();
        }

        @Override
        public void start() {
            Vec3 vec3 = this.findPos();
            if (vec3 != null) {
                ButterflyEntity.this.navigation.moveTo(ButterflyEntity.this.navigation.createPath(BlockPos.containing(vec3), 1), 1.0);
            }
        }

        @Nullable
        private Vec3 findPos() {
            Vec3 vec3 =  ButterflyEntity.this.getViewVector(0.0F);

            int i = 8;
            Vec3 vec32 = HoverRandomPos.getPos(ButterflyEntity.this, 8, 7, vec3.x, vec3.z, (float) (Math.PI / 2), 3, 1);
            return vec32 != null ? vec32 : AirAndWaterRandomPos.getPos(ButterflyEntity.this, 8, 4, -2, vec3.x, vec3.z, (float) (Math.PI / 2));
        }
    }
}

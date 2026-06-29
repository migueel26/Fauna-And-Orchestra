package net.migueel26.faunaandorchestra.entity.custom;

import net.migueel26.faunaandorchestra.entity.goals.AlertWhenAttackedGoal;
import net.migueel26.faunaandorchestra.entity.goals.MusicalEntityPlayingInstrumentGoal;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class SeaLionEntity extends MusicalEntity {
    protected static final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    protected static final RawAnimation WALK = RawAnimation.begin().thenPlay("walk");
    protected static final RawAnimation PLAYING = RawAnimation.begin().thenPlay("playing");
    protected static final RawAnimation SWIM = RawAnimation.begin().thenPlay("swim");
    protected static final RawAnimation SCREAM = RawAnimation.begin().thenPlay("scream");
    protected static final RawAnimation EMOTE = RawAnimation.begin().thenPlay("emote");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private final AnimationController<SeaLionEntity> seaLionController = new AnimationController<>(this, "sea_lion_controller", 5, this::seaLionState)
            .triggerableAnim("emote", EMOTE);
    private final AnimationController<SeaLionEntity> screamController = new AnimationController<>(this, "sea_lion_scream_controller", 2, this::emptyState)
            .triggerableAnim("scream", SCREAM);
    protected int bubbleTick = 0;
    protected Vec3 lastPosition = position();
    protected int emoteTicks = 0;
    public SeaLionEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);

        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER_BORDER, 0.0F);
        addOverridenGoals();
    }

    @Override
    public RegistryObject<Item> getInstrument() {
        return ModItems.DRUM;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.25f));
        this.goalSelector.addGoal(1, new MusicalEntityPlayingInstrumentGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        // LookAtPlayerGoal(3)
        // LookAtPlayerGoal(3, TravellingMusician)
        // RandomStrollGoal(5)
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }

    private void addOverridenGoals() {
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 6.0F) {
            @Override
            public boolean canUse() {
                return super.canUse() && !((MusicalEntity) mob).isPlayingInstrument();
            }
        });
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, TravellingMusician.class, 6.0F) {
            @Override
            public boolean canUse() {
                return super.canUse() && !((MusicalEntity) mob).isPlayingInstrument();
            }
        });
        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 1.0) {
            @Override
            public boolean canUse() {
                return super.canUse() && !((SeaLionEntity) mob).isEmoting();
            }
        });
    }

    private <E extends GeoAnimatable> PlayState seaLionState(AnimationState<E> state) {
        if (isPlayingInstrument()) {
            state.getController().setAnimation(PLAYING);
        } else if (state.isMoving()) {
            if (isInWater()) {
                state.getController().setAnimation(SWIM);
            } else {
                state.getController().setAnimation(WALK);
            }
        } else {
            state.getController().setAnimation(IDLE);
        }
        return PlayState.CONTINUE;
    }

    private <E extends GeoAnimatable> PlayState emptyState(AnimationState<E> state) {
        return PlayState.CONTINUE;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 15d)
                .add(Attributes.MOVEMENT_SPEED, 0.2D)
                .add(Attributes.FOLLOW_RANGE, 24D);
    }

    public static boolean checkSeaLionSpawnRules(
            EntityType<? extends Animal> animal, LevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random
    ) {

        if (level.getRandom().nextInt(20) != 0) {
            return false;
        }

        return level.getBlockState(pos.below()).is(BlockTags.SAND) ||
                level.getBlockState(pos.below()).is(Blocks.WATER);
    }

    @Override
    public void tick() {
        if (!isPlayingInstrument() && emoteTicks == 0 && tickCount % 20 == 0 && random.nextFloat() <= 0.05f) {
            emote();
            emoteTicks = 40;
            navigation.stop();
        }

        if (isEmoting()) {
            emoteTicks--;
            navigation.stop();
        }

        super.tick();
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

    public void scream() {
        triggerAnim("sea_lion_scream_controller", "scream");
    }

    public void emote() {
        triggerAnim("sea_lion_controller", "emote");
    }

    public boolean isEmoting() {
        return emoteTicks > 0;
    }

    @Override
    public void playAmbientSound() {
        if (!isPlayingInstrument() && level().random.nextBoolean()) {
            scream();
            super.playAmbientSound();
        }
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return isPlayingInstrument() ? null : ModSounds.SEA_LION_AMBIENT.get();
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return false;
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return null;
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
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(seaLionController);
        controllers.add(screamController);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
}

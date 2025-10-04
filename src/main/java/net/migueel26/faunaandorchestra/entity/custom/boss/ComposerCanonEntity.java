package net.migueel26.faunaandorchestra.entity.custom.boss;

import net.migueel26.faunaandorchestra.entity.custom.MantisEntity;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ComposerCanonEntity extends Monster implements GeoEntity {
    int ticks = 0;
    public static final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    public static final RawAnimation WALK = RawAnimation.begin().thenPlay("walk_fast");
    public static final RawAnimation SPAWN = RawAnimation.begin().thenPlay("spawn");
    public static final RawAnimation ATTACK = RawAnimation.begin().thenPlay("attack_fast");
    public final AnimationController<ComposerCanonEntity> canonController = new AnimationController<>(this, "composer_canon_controller", 5, this::composerCanonState)
            .triggerableAnim("canon_attack", ATTACK)
            .triggerableAnim("canon_spawn", SPAWN);
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    public ComposerCanonEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    private <E extends GeoAnimatable> PlayState composerCanonState(AnimationState<E> state) {
        if (state.isMoving()) {
            state.setAnimation(WALK);
        } else {
            state.setAnimation(IDLE);
        }
        return PlayState.CONTINUE;
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new MeleeAttackGoal(this, 1.0f, true) {
            @Override
            protected void checkAndPerformAttack(LivingEntity target) {
                if (this.canPerformAttack(target)) {
                    ((ComposerCanonEntity) this.mob).triggerAnim("composer_canon_controller", "canon_attack");
                    this.resetAttackCooldown();
                    this.mob.doHurtTarget(target);

                }
            }
        });
        goalSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, false, true));
        goalSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Animal.class, false, true));
    }

    @Override
    public void tick() {
        if (ticks == 1) {
            triggerAnim("composer_canon_controller", "canon_spawn");
        }
        if (ticks < 40) {
            this.navigation.stop();
        }
        ticks++;
        super.tick();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 35d)
                .add(Attributes.MOVEMENT_SPEED, 0.45D)
                .add(Attributes.FOLLOW_RANGE, 24D)
                .add(Attributes.ATTACK_DAMAGE, 12.0);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(canonController);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
}

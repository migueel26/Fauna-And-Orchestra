package net.migueel26.faunaandorchestra.entity.goals;

import net.migueel26.faunaandorchestra.entity.custom.ConductorEntity;
import net.migueel26.faunaandorchestra.entity.custom.MusicalEntity;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Consumer;

public class AnimalEatGoal extends Goal {
    // Partially inspired by Alex's Mobs CreatureAITargetItems
    protected static final int DEFAULT_RADIUS = 10;
    protected static final int DEFAULT_WAIT_TIME = 10;
    protected final Consumer<ItemEntity> onEat;
    protected final Animal mob;
    protected final int radius;
    protected ItemEntity targetEntity;
    protected boolean mustUpdate;
    protected int waitTime;
    protected final Item food;
    protected int pathRecalcDelay;
    public AnimalEatGoal(Animal mob, Item food, Consumer<ItemEntity> onEat) {
        this(mob, food, DEFAULT_RADIUS, onEat);
    }

    public AnimalEatGoal(Animal mob, Item food, int radius, Consumer<ItemEntity> onEat) {
        this.mob = mob;
        this.food = food;
        this.radius = radius;
        this.onEat = onEat;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (!this.mustUpdate) {
            if (this.mob.getRandom().nextInt(20) != 0) {
                return false;
            }
        }

        if (mob.isInLove() || mob.isBaby()) return false;
        if (mob instanceof NeutralMob neutralMob && neutralMob.isAngry()) return false;
        if (mob instanceof MusicalEntity musicalEntity && musicalEntity.isHoldingInstrument()) return false;
        if (mob instanceof ConductorEntity conductor && conductor.isHoldingBaton()) return false;

        List<ItemEntity> list = this.mob.level().getEntitiesOfClass(ItemEntity.class,
                this.mob.getBoundingBox().inflate(radius),
                item -> item.getItem().is(food) && item.onGround()); // onGround opcional, evita que persigan items volando

        if (list.isEmpty()) {
            return false;
        } else {
            // We pick the closest one
            this.targetEntity = list.stream()
                    .min((i1, i2) -> Double.compare(this.mob.distanceToSqr(i1), this.mob.distanceToSqr(i2)))
                    .orElse(list.get(0));
            return true;
        }
    }

    @Override
    public boolean canContinueToUse() {
        return this.targetEntity != null && this.targetEntity.isAlive() && !this.mob.getNavigation().isDone();
    }

    @Override
    public void start() {
        this.mustUpdate = false;
        this.pathRecalcDelay = 0;
        this.mob.getNavigation().moveTo(this.targetEntity.getX(), this.targetEntity.getY(), this.targetEntity.getZ(), 1.25f);
    }

    @Override
    public void tick() {
        if (this.targetEntity == null || !this.targetEntity.isAlive()) {
            this.stop();
            return;
        }

        if (pathRecalcDelay > 0) {
            pathRecalcDelay--;
        } else {
            this.mob.getNavigation().moveTo(this.targetEntity.getX(), this.targetEntity.getY(), this.targetEntity.getZ(), 1.25f);
            pathRecalcDelay = 10;
        }

        double distance = this.mob.distanceToSqr(this.targetEntity);
        if (distance < 2.0D || (distance < 4.0D && this.targetEntity.getBoundingBox().intersects(this.mob.getBoundingBox()))) {
            onEat.accept(this.targetEntity);
            stop();
        }
    }

    @Override
    public void stop() {
        this.mob.getNavigation().stop();
        this.targetEntity = null;
    }
}

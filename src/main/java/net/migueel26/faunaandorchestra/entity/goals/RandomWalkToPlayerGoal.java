package net.migueel26.faunaandorchestra.entity.goals;

import net.migueel26.faunaandorchestra.entity.custom.koala_workers.WorkerKoalaEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

public class RandomWalkToPlayerGoal extends Goal {
    private final WorkerKoalaEntity koala;
    private final double speedModifier;
    private final int interval;
    private final float searchRange;
    private Player targetPlayer;

    public RandomWalkToPlayerGoal(WorkerKoalaEntity koala, double speedModifier, int interval, float searchRange) {
        this.koala = koala;
        this.speedModifier = speedModifier;
        this.interval = interval;
        this.searchRange = searchRange;

        this.setFlags(java.util.EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.koala.getRandom().nextInt(this.interval) != 0) {
            return false;
        }

        this.targetPlayer = this.koala.level().getNearestPlayer(this.koala, this.searchRange);

        return this.targetPlayer != null && !this.targetPlayer.isSpectator();
    }

    @Override
    public boolean canContinueToUse() {
        if (this.targetPlayer == null || !this.targetPlayer.isAlive() || this.targetPlayer.isSpectator()) {
            return false;
        }
        if (this.koala.distanceToSqr(this.targetPlayer) < 4.0D) {
            return false;
        }
        return !this.koala.getNavigation().isDone();
    }

    @Override
    public void start() {
        this.koala.getNavigation().moveTo(this.targetPlayer, this.speedModifier);
    }

    @Override
    public void stop() {
        this.targetPlayer = null;
        this.koala.getNavigation().stop();
    }

    @Override
    public void tick() {
        this.koala.getLookControl().setLookAt(this.targetPlayer, 30.0F, 30.0F);

        if (this.koala.tickCount % 10 == 0) {
            this.koala.getNavigation().moveTo(this.targetPlayer, this.speedModifier);
        }
    }
}

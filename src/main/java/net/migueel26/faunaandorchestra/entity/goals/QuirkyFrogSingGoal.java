package net.migueel26.faunaandorchestra.entity.goals;

import net.migueel26.faunaandorchestra.entity.custom.QuirkyFrogEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.pathfinder.Path;

public class QuirkyFrogSingGoal extends Goal {
    private final QuirkyFrogEntity chorister;
    private QuirkyFrogEntity conductor;
    private int ticksUntilNextPath;

    public QuirkyFrogSingGoal(QuirkyFrogEntity chorister) {
        this.chorister = chorister;
    }
    @Override
    public boolean canUse() {
        return !chorister.isTame() && chorister.isSinging() && chorister.getFrogConductor() != null;
    }

    @Override
    public boolean canContinueToUse() {
        return !chorister.isDeadOrDying() && canUse();
    }

    @Override
    public void start() {
        this.conductor = chorister.getFrogConductor();
        this.ticksUntilNextPath = 20;
    }

    @Override
    public void stop() {
        this.chorister.setSinging(false);
        this.chorister.setReady(false);
        super.stop();
    }

    @Override
    public void tick() {
        if (ticksUntilNextPath <= 0) {
            float distance = chorister.distanceTo(conductor);
            if (distance > 5.0F) {
                Path path = chorister.getNavigation().createPath(conductor, 0);
                chorister.getNavigation().moveTo(path, 1.0D);
                chorister.setReady(false);
            } else if (chorister.getNavigation().isDone()) {
                chorister.setReady(true);
            }
            ticksUntilNextPath = 20;
        } else {
            ticksUntilNextPath--;
        }

        chorister.getLookControl().setLookAt(conductor);
    }
}

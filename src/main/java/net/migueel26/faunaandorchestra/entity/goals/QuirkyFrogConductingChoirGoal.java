package net.migueel26.faunaandorchestra.entity.goals;

import net.migueel26.faunaandorchestra.entity.custom.QuirkyFrogEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.pathfinder.Path;

public class QuirkyFrogConductingChoirGoal extends Goal {
    private QuirkyFrogEntity conductor;
    private boolean startSinging;
    private int tick;
    public QuirkyFrogConductingChoirGoal(QuirkyFrogEntity quirkyFrog) {
        this.conductor = quirkyFrog;
    }

    @Override
    public boolean canUse() {
        return !conductor.isTame() && conductor.isReady() && !conductor.isDeadOrDying();
    }

    @Override
    public void start() {
        for (QuirkyFrogEntity chorister : conductor.getFrogChoir()) {
            chorister.setSinging(true);

            System.out.println("IN!");

            Path path = chorister.getNavigation().createPath(conductor, 0);
            chorister.getNavigation().moveTo(path, 1.0D);

            this.startSinging = false;
            this.tick = 0;
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return super.requiresUpdateEveryTick();
    }

    @Override
    public void tick() {
        conductor.getNavigation().stop();

        super.tick();
    }
}

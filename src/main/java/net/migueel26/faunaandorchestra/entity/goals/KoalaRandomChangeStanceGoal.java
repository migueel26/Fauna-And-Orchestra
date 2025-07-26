package net.migueel26.faunaandorchestra.entity.goals;

import net.migueel26.faunaandorchestra.entity.custom.KoalaEntity;
import net.migueel26.faunaandorchestra.entity.custom.RedPandaEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public class KoalaRandomChangeStanceGoal extends Goal {
    private final int threshold = 500;
    private final KoalaEntity koala;
    private final float probability;
    private int timeSpentInCurrentStance = 0;
    public KoalaRandomChangeStanceGoal(KoalaEntity koala, float probability) {
        this.koala = koala;
        this.probability = probability/20;
    }

    @Override
    public boolean canUse() {
        timeSpentInCurrentStance++;
        return this.koala.getRandom().nextFloat() < probability * timeSpentInCurrentStance/200 && !koala.isInWater()
                && !koala.isKoalaSleeping()
                && koala.getNavigation().isDone()
                && timeSpentInCurrentStance > threshold;
    }

    @Override
    public boolean canContinueToUse() {
        boolean flag = this.timeSpentInCurrentStance < threshold;
        if (!flag) {
            flag = this.koala.getRandom().nextFloat() > probability * timeSpentInCurrentStance/200;
        }
        return flag && !koala.isInWater() && koala.getNavigation().isDone();
    }

    @Override
    public void start() {
        koala.getNavigation().stop();
        if (!koala.isSitting()) koala.sitDown();
        timeSpentInCurrentStance = 0;
    }

    @Override
    public void stop() {
        koala.getNavigation().stop();
        if (!koala.isKoalaSleeping()) koala.standUp();
        timeSpentInCurrentStance = 0;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return super.requiresUpdateEveryTick();
    }

    @Override
    public void tick() {
        timeSpentInCurrentStance++;
    }
}

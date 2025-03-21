package net.migueel26.faunaandorchestra.entity.goals;

import net.migueel26.faunaandorchestra.entity.custom.RedPandaEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public class RedPandaRandomChangeStanceGoal extends Goal {
    private final int threshold = 500;
    private final RedPandaEntity redPanda;
    private final float probability;
    private int timeSpentInCurrentStance = 0;
    public RedPandaRandomChangeStanceGoal(RedPandaEntity redPanda, float probability) {
        this.redPanda = redPanda;
        this.probability = probability/20;
    }

    @Override
    public boolean canUse() {
        timeSpentInCurrentStance++;
        boolean flag = this.redPanda.getRandom().nextFloat() < probability && !redPanda.isInWater()
                && !redPanda.isHoldingInstrument()
                && timeSpentInCurrentStance > threshold;
        return redPanda.isStanding() || flag;
    }

    @Override
    public boolean canContinueToUse() {
        boolean flag = this.timeSpentInCurrentStance < threshold;
        if (!flag) {
            flag = this.redPanda.getRandom().nextFloat() > probability * timeSpentInCurrentStance/200;
        }
        return flag && !redPanda.isInWater();
    }

    @Override
    public void start() {
        redPanda.getNavigation().stop();
        if (!redPanda.isStanding()) redPanda.standUp(true);
        timeSpentInCurrentStance = 0;
    }

    @Override
    public void stop() {
        redPanda.getNavigation().stop();
        redPanda.sitDownAnimated();
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

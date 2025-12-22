package net.migueel26.faunaandorchestra.entity.goals;

import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;

public class TamableAnimalPanicGoal extends Goal {
    public TamableAnimalPanicGoal(double speed) {

    }

    @Override
    public boolean canUse() {
        return true;
    }
}

package net.migueel26.faunaandorchestra.entity.goals;

import net.migueel26.faunaandorchestra.entity.custom.ConductorEntity;
import net.migueel26.faunaandorchestra.entity.custom.MantisEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.List;
import java.util.Optional;

public class MantisPlayingInstrumentGoal extends Goal {
    private final MantisEntity mantis;

    public MantisPlayingInstrumentGoal(MantisEntity mantis) {
        this.mantis = mantis;
    }

    @Override
    public boolean canUse() {
        Optional<ConductorEntity> conductor = this.mantis.level().getEntitiesOfClass(ConductorEntity.class,
                this.mantis.getBoundingBox().inflate(5)).stream().filter(cond -> !cond.isConducting()).findAny();

        if (conductor.isEmpty()) {
            return false;
        }
        return !mantis.isDeadOrDying() && mantis.isPlayingInstrument()
                && mantis.onGround();
    }

    @Override
    public void start() {
        mantis.getNavigation().stop();

    }
}

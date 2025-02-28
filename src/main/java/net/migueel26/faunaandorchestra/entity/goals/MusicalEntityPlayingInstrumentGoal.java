package net.migueel26.faunaandorchestra.entity.goals;

import net.migueel26.faunaandorchestra.entity.custom.ConductorEntity;
import net.migueel26.faunaandorchestra.entity.custom.MusicalEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.Optional;

public class MusicalEntityPlayingInstrumentGoal extends Goal {
    private final MusicalEntity musicalEntity;

    public MusicalEntityPlayingInstrumentGoal(MusicalEntity musicalEntity) {
        this.musicalEntity = musicalEntity;
    }

    @Override
    public boolean canUse() {
        Optional<ConductorEntity> conductor = this.musicalEntity.level().getEntitiesOfClass(ConductorEntity.class,
                this.musicalEntity.getBoundingBox().inflate(5)).stream().filter(cond -> !cond.isConducting()).findAny();

        musicalEntity.setConductor(conductor.orElse(null));

        return !musicalEntity.isDeadOrDying() && musicalEntity.isPlayingInstrument()
                && musicalEntity.onGround() && conductor.isPresent();
    }

    @Override
    public void start() {
        musicalEntity.getNavigation().stop();
    }
}

package net.migueel26.faunaandorchestra.entity.goals;

import net.migueel26.faunaandorchestra.entity.custom.ConductorEntity;
import net.migueel26.faunaandorchestra.entity.custom.MusicalEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

public class ConductorEntityConductingOrchestra extends Goal {
    private final ConductorEntity conductor;
    public ConductorEntityConductingOrchestra(ConductorEntity conductor) {
        this.conductor = conductor;
    }

    @Override
    public boolean canUse() {
        return !conductor.isOrchestraEmpty() && !conductor.isDeadOrDying() && conductor.isHoldingBaton();
    }

    @Override
    public boolean canContinueToUse() {
        return !conductor.isOrchestraEmpty() && !conductor.isDeadOrDying() && conductor.isHoldingBaton();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        conductor.getNavigation().stop();
        conductor.getLookControl().setLookAt(getCentroid());
    }

    private Vec3 getCentroid() {
        if (!conductor.getOrchestra().isEmpty()) {
            Set<MusicalEntity> orchestra = conductor.getOrchestra();
            double n = orchestra.size();

            return new Vec3(
                    orchestra.stream().map(Entity::getX).reduce(0.0, Double::sum)/n,
                    conductor.getY(),
                    orchestra.stream().map(Entity::getZ).reduce(0.0, Double::sum)/n);
        } else {
            return new Vec3(0.0,0.0,0.0);
        }
    }
}

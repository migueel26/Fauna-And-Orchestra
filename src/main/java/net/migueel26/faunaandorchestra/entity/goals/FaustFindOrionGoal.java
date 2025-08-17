package net.migueel26.faunaandorchestra.entity.goals;

import net.migueel26.faunaandorchestra.entity.custom.Faust;
import net.migueel26.faunaandorchestra.entity.custom.Orion;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class FaustFindOrionGoal extends Goal {
    protected final Faust faust;
    protected Orion orion;

    public FaustFindOrionGoal(Faust faust) {
        this.faust = faust;
    }
    @Override
    public boolean canUse() {
        if ((orion == null || orion.isDeadOrDying())) {
            faust.setOrion(null);
            Orion orion = faust.level().getNearestEntity(Orion.class,
                    TargetingConditions.DEFAULT,
                    faust,
                    faust.getX(), faust.getY(), faust.getZ(),
                    faust.getBoundingBox().inflate(10.0));
            if (orion != null) {
                this.orion = orion;
                faust.setOrion(orion);
                orion.setFaust(faust);
            }
        } else if (faust.distanceTo(orion) <= 2.5) {
            faust.setPlaying(true);
            orion.setPlaying(true);
        }

        return this.orion != null && faust.distanceTo(orion) > 2.5 && this.faust.getLastDamageSource() == null
                && this.orion.getLastDamageSource() == null;
    }

    @Override
    public boolean canContinueToUse() {
        return faust.getNavigation().isInProgress() || orion.getNavigation().isInProgress();
    }

    @Override
    public void start() {
        Vec3 middle = faust.position().add(orion.position()).scale(0.5);
        faust.getNavigation().moveTo(middle.x, middle.y, middle.z, 1.0D);
        orion.getNavigation().moveTo(middle.x, middle.y, middle.z, 1.0D);
        faust.setPlaying(false);
        orion.setPlaying(false);
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
        faust.setPlaying(true);
        orion.setPlaying(true);
    }
}

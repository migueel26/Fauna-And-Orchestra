package net.migueel26.faunaandorchestra.entity.goals;

import net.migueel26.faunaandorchestra.entity.custom.jazzy_dammys.DanB;
import net.migueel26.faunaandorchestra.entity.custom.jazzy_dammys.Delroy;
import net.migueel26.faunaandorchestra.entity.custom.jazzy_dammys.Denise;
import net.migueel26.faunaandorchestra.entity.custom.jazzy_dammys.Denzel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

public class DanBFindJazzyDammysGoal extends Goal {
    protected final DanB danB;
    protected Denise denise;
    protected Denzel denzel;
    protected Delroy delroy;

    public DanBFindJazzyDammysGoal(DanB danB) {
        this.danB = danB;
    }

    @Override
    public boolean canUse() {
        // Buscar a Denise
        if (denise == null || denise.isDeadOrDying()) {
            danB.setDenise(null);
            denise = danB.level().getNearestEntity(Denise.class, TargetingConditions.DEFAULT,
                    danB, danB.getX(), danB.getY(), danB.getZ(), danB.getBoundingBox().inflate(15.0));
            if (denise != null) {
                danB.setDenise(denise);
                denise.setDanB(danB);
            }
        }

        // Buscar a Denzel
        if (denzel == null || denzel.isDeadOrDying()) {
            danB.setDenzel(null);
            denzel = danB.level().getNearestEntity(Denzel.class, TargetingConditions.DEFAULT,
                    danB, danB.getX(), danB.getY(), danB.getZ(), danB.getBoundingBox().inflate(15.0));
            if (denzel != null) {
                danB.setDenzel(denzel);
                denzel.setDanB(danB);
            }
        }

        // Buscar a Delroy
        if (delroy == null || delroy.isDeadOrDying()) {
            danB.setDelroy(null);
            delroy = danB.level().getNearestEntity(Delroy.class, TargetingConditions.DEFAULT,
                    danB, danB.getX(), danB.getY(), danB.getZ(), danB.getBoundingBox().inflate(15.0));
            if (delroy != null) {
                danB.setDelroy(delroy);
                delroy.setDanB(danB);
            }
        }

        boolean allFound = (denise != null && denzel != null && delroy != null);
        boolean noDamage = danB.getLastDamageSource() == null
                && (denise == null || denise.getLastDamageSource() == null)
                && (denzel == null || denzel.getLastDamageSource() == null)
                && (delroy == null || delroy.getLastDamageSource() == null);

        // Si están los 4 y nadie ha recibido daño, tocan música. Si no, paran.
        if (allFound && noDamage) {
            danB.setPlaying(true);
            denise.setPlaying(true);
            denzel.setPlaying(true);
            delroy.setPlaying(true);
        } else {
            danB.setPlaying(false);
            if (denise != null) denise.setPlaying(false);
            if (denzel != null) denzel.setPlaying(false);
            if (delroy != null) delroy.setPlaying(false);
        }

        // Devolvemos false para que solo actúe como un escáner y no bloquee el movimiento de DanB
        return false;
    }
}
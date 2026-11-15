package net.migueel26.faunaandorchestra.entity.goals;

import net.migueel26.faunaandorchestra.entity.custom.village_trotters.Cornelius;
import net.migueel26.faunaandorchestra.entity.custom.village_trotters.VillageTrotterEntity;
import net.migueel26.faunaandorchestra.entity.custom.village_trotters.VillageTrotterEntity.VillageTrotter;
import net.minecraft.Util;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.List;

public class CorneliusFindVillageTrottersGoal extends Goal {
    protected final Cornelius cornelius;

    public CorneliusFindVillageTrottersGoal(Cornelius cornelius) {
        this.cornelius = cornelius;
    }

    @Override
    public boolean canUse() {
        // If already has village trotters assigned
        if (cornelius.hasVillageTrotters()) return false;

        List<VillageTrotterEntity> trotters = cornelius.level().getEntitiesOfClass(VillageTrotterEntity.class,
                cornelius.getBoundingBox().inflate(15.0),
                trotter -> trotter.isAlive() && trotter.getCorneliusUUID() == null);

        VillageTrotter[] types = VillageTrotter.values();
        if (trotters.size() < types.length) return false;

        // Assign each one an unassigned type
        List<VillageTrotter> shuffledTypes = Util.shuffledCopy(types, cornelius.getRandom());
        for (int i = 0; i < shuffledTypes.size(); i++) {
            VillageTrotterEntity trotter = trotters.get(i);
            VillageTrotter type = shuffledTypes.get(i);

            trotter.setVillageTrotterType(type);
            trotter.setCornelius(cornelius);
            cornelius.setVillageTrotter(type, trotter.getUUID());
        }

        return false;
    }
}

package net.migueel26.faunaandorchestra.entity.goals;

import net.migueel26.faunaandorchestra.entity.custom.AbstractKoalaEntity;
import net.migueel26.faunaandorchestra.entity.custom.WanderingKoalaEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;

public class TradeWithPlayerGoal extends Goal {
    private final AbstractKoalaEntity mob;

    public TradeWithPlayerGoal(AbstractKoalaEntity mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (!this.mob.isAlive()) {
            return false;
        } else if (this.mob.isInWater()) {
            return false;
        } else if (!this.mob.onGround()) {
            return false;
        } else if (this.mob.hurtMarked) {
            return false;
        } else {
            Player player = this.mob.getTradingPlayer();
            if (player == null) {
                return false;
            } else {
                return this.mob.distanceToSqr(player) > 16.0 ? false : player.containerMenu != null;
            }
        }
    }

    @Override
    public void start() {
        this.mob.getNavigation().stop();
    }

    @Override
    public void stop() {
        this.mob.setTradingPlayer(null);
    }
}

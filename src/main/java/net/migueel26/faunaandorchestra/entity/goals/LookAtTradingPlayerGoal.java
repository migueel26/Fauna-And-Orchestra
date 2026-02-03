package net.migueel26.faunaandorchestra.entity.goals;

import net.migueel26.faunaandorchestra.entity.custom.WanderingKoalaEntity;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;

public class LookAtTradingPlayerGoal extends LookAtPlayerGoal {
    private final WanderingKoalaEntity koala;

    public LookAtTradingPlayerGoal(WanderingKoalaEntity koala) {
        super(koala, Player.class, 8.0F);
        this.koala = koala;
    }

    @Override
    public boolean canUse() {
        if (this.koala.isTrading()) {
            this.lookAt = this.koala.getTradingPlayer();
            return true;
        } else {
            return false;
        }
    }
}
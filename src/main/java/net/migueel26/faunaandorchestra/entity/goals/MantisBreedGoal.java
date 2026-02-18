package net.migueel26.faunaandorchestra.entity.goals;

import net.migueel26.faunaandorchestra.entity.custom.MantisEntity;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.level.GameRules;

public class MantisBreedGoal extends BreedGoal {
    private final MantisEntity mantis;

    public MantisBreedGoal(MantisEntity mantis, double speedModifier) {
        super(mantis, speedModifier);
        this.mantis = mantis;
    }

    public boolean canUse() {
        return super.canUse() && !this.mantis.hasEgg();
    }

    protected void breed() {
        ServerPlayer serverplayer = this.animal.getLoveCause();
        if (serverplayer == null && this.partner.getLoveCause() != null) {
            serverplayer = this.partner.getLoveCause();
        }

        if (serverplayer != null) {
            serverplayer.awardStat(Stats.ANIMALS_BRED);
            CriteriaTriggers.BRED_ANIMALS.trigger(serverplayer, this.animal, this.partner, null);
        }

        this.mantis.setHasEgg(true);
        this.animal.setAge(6000);
        this.partner.setAge(6000);
        this.animal.resetLove();
        this.partner.resetLove();
        RandomSource randomsource = this.animal.getRandom();
        if (this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            this.level.addFreshEntity(new ExperienceOrb(this.level, this.animal.getX(), this.animal.getY(), this.animal.getZ(), randomsource.nextInt(7) + 1));
        }

    }
}

package net.migueel26.faunaandorchestra.entity.goals;

import net.migueel26.faunaandorchestra.entity.custom.*;
import net.migueel26.faunaandorchestra.entity.custom.koala_workers.AbstractKoalaWorker;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class FaunaRandomLookAroundGoal extends Goal {
    protected final Mob mob;
    private double relX;
    private double relZ;
    private int lookTime;

    public FaunaRandomLookAroundGoal(Mob mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    public enum Fauna {
        MusicalEntity, ConductorEntity, AgeableMob, QuirkyFrogEntity, AbstractKoalaMerchant, WanderingKoalaEntity, ButlerKoalaEntity, SproutlingEntity, AbstractKoalaWorker, LivingMusicEntity
    }

    @Override
    public boolean canUse() {
        boolean condition = this.mob.getRandom().nextFloat() < 0.02F;
        if (condition) {
            Fauna mobType = Fauna.valueOf(this.mob.getClass().getSuperclass().getSimpleName());
            if (mobType != Fauna.MusicalEntity &&
                    mobType != Fauna.AbstractKoalaWorker) mobType = Fauna.valueOf(this.mob.getClass().getSimpleName());

            switch (mobType) {
                case MusicalEntity -> condition = !((MusicalEntity) mob).isPlayingInstrument();
                case QuirkyFrogEntity -> condition = !((QuirkyFrogEntity) mob).isConducting()
                        && !((QuirkyFrogEntity) mob).isSinging();
                case AbstractKoalaMerchant -> condition = !((WanderingKoalaEntity) mob).isKoalaSleeping();
                case ButlerKoalaEntity -> condition = !((ButlerKoalaEntity) mob).isServing();
                case SproutlingEntity  -> condition = !((SproutlingEntity) mob).isSinging();
                case LivingMusicEntity -> condition = !((LivingMusicEntity) mob).isTrapped();
                case AbstractKoalaWorker -> {
                    AbstractKoalaWorker koala = (AbstractKoalaWorker) mob;
                    condition = !koala.isKoalaSleeping() && (!koala.isWorking() || koala.isInLunchBreak());
                }
            }
        }

        return condition;
    }

    @Override
    public boolean canContinueToUse() {
        return this.lookTime >= 0;
    }

    @Override
    public void start() {
        double d0 = (Math.PI * 2) * this.mob.getRandom().nextDouble();
        this.relX = Math.cos(d0);
        this.relZ = Math.sin(d0);
        this.lookTime = 20 + this.mob.getRandom().nextInt(20);
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        this.lookTime--;
        this.mob.getLookControl().setLookAt(this.mob.getX() + this.relX, this.mob.getEyeY(), this.mob.getZ() + this.relZ);
    }
}

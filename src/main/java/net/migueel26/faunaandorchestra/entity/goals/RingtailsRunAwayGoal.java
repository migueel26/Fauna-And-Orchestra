package net.migueel26.faunaandorchestra.entity.goals;

import net.migueel26.faunaandorchestra.entity.custom.Faust;
import net.migueel26.faunaandorchestra.entity.custom.Orion;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.PanicGoal;

public class RingtailsRunAwayGoal extends PanicGoal {
    protected int runTicks;
    public RingtailsRunAwayGoal(PathfinderMob mob, double speedModifier) {
        super(mob, speedModifier);
    }

    @Override
    public void start() {
        super.start();

        if (mob instanceof Faust faust && faust.getOrion() != null) {
            findRandomPosition();
            faust.getOrion().getNavigation().moveTo(this.posX, this.posY, this.posZ, this.speedModifier);
            faust.getOrion().setPlaying(false);
            faust.setPlaying(false);
        } else if (mob instanceof Orion orion && orion.getFaust() != null) {
            findRandomPosition();
            orion.getFaust().getNavigation().moveTo(this.posX, this.posY, this.posZ, this.speedModifier);
            orion.getFaust().setPlaying(false);
            orion.setPlaying(false);
        }

        runTicks = 10;
    }

    @Override
    public void tick() {
        runTicks--;
        super.tick();
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse() || runTicks > 0;
    }

    @Override
    public void stop() {
        Faust faust = null;
        Orion orion = null;

        super.stop();
        ServerLevel level = (ServerLevel) mob.level();

        if (mob instanceof Faust) {
            orion = ((Faust) mob).getOrion();
        } else {
            faust = ((Orion) mob).getFaust();
        }

        if (orion != null) {
            level.sendParticles(ParticleTypes.POOF, orion.getX(), orion.getY(), orion.getZ(), 30, 0.1, 0.5, 0.1, 0.3);
            orion.remove(Entity.RemovalReason.DISCARDED);
        }

        if (faust != null) {
            level.sendParticles(ParticleTypes.POOF, faust.getX(), faust.getY(), faust.getZ(), 30, 0.1, 0.5, 0.1, 0.3);
            faust.remove(Entity.RemovalReason.DISCARDED);
        }

        level.sendParticles(ParticleTypes.POOF, mob.getX(), mob.getY(),mob.getZ(), 30, 0.1, 0.5, 0.1, 0.3);
        mob.remove(Entity.RemovalReason.DISCARDED);
    }
}

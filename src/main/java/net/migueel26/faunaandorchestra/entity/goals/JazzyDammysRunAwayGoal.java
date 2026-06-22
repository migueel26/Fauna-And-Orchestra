package net.migueel26.faunaandorchestra.entity.goals;

import net.migueel26.faunaandorchestra.entity.custom.TravellingMusician;
import net.migueel26.faunaandorchestra.entity.custom.jazzy_dammys.DanB;
import net.migueel26.faunaandorchestra.entity.custom.jazzy_dammys.Delroy;
import net.migueel26.faunaandorchestra.entity.custom.jazzy_dammys.Denise;
import net.migueel26.faunaandorchestra.entity.custom.jazzy_dammys.Denzel;
import net.migueel26.faunaandorchestra.util.ModSavedData;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.player.Player;

public class JazzyDammysRunAwayGoal extends PanicGoal {
    protected int runTicks;

    public JazzyDammysRunAwayGoal(PathfinderMob mob, double speedModifier) {
        super(mob, speedModifier);
    }

    @Override
    public void start() {
        super.start(); // El mob que recibió el golpe busca una posición aleatoria

        DanB danB = getDanB();
        if (danB != null) {
            // Mandamos a toda la banda a huir al mismo punto
            makeRun(danB);
            if (danB.getDenise() != null) makeRun(danB.getDenise());
            if (danB.getDenzel() != null) makeRun(danB.getDenzel());
            if (danB.getDelroy() != null) makeRun(danB.getDelroy());
        } else {
            if (mob instanceof TravellingMusician mus) mus.setPlaying(false);
        }

        runTicks = 10;
    }

    private void makeRun(PathfinderMob member) {
        if (member != mob) {
            member.getNavigation().moveTo(this.posX, this.posY, this.posZ, this.speedModifier);
        }
        if (member instanceof TravellingMusician mus) {
            mus.setPlaying(false);
        }
    }

    private DanB getDanB() {
        if (mob instanceof DanB) return (DanB) mob;
        if (mob instanceof Denise denise) return denise.getDanB();
        if (mob instanceof Denzel denzel) return denzel.getDanB();
        if (mob instanceof Delroy delroy) return delroy.getDanB();
        return null;
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
        super.stop();
        ServerLevel level = (ServerLevel) mob.level();
        Entity entity = mob.getLastHurtByMob();
        Player player = entity instanceof Player p ? p : null;

        DanB danB = getDanB();
        if (danB != null) {
            discardMember(danB, level, player);
            if (danB.getDenise() != null) discardMember(danB.getDenise(), level, player);
            if (danB.getDenzel() != null) discardMember(danB.getDenzel(), level, player);
            if (danB.getDelroy() != null) discardMember(danB.getDelroy(), level, player);
        } else {
            discardMember((TravellingMusician) mob, level, player);
        }
    }

    private void discardMember(TravellingMusician member, ServerLevel level, Player player) {
        if (!member.isRemoved()) {
            level.sendParticles(ParticleTypes.POOF, member.getX(), member.getY(), member.getZ(), 30, 0.1, 0.5, 0.1, 0.3);
            if (mob instanceof DanB) {
                if (player != null) ModSavedData.saveConfidence(level, member, player.getUUID(), -1);
            }
            member.remove(Entity.RemovalReason.DISCARDED);
        }
    }
}
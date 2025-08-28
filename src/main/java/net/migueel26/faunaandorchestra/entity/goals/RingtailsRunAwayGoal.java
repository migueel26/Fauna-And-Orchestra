package net.migueel26.faunaandorchestra.entity.goals;

import net.migueel26.faunaandorchestra.block.ModBlocks;
import net.migueel26.faunaandorchestra.entity.custom.Faust;
import net.migueel26.faunaandorchestra.entity.custom.Orion;
import net.migueel26.faunaandorchestra.entity.custom.TalkableEntity;
import net.migueel26.faunaandorchestra.util.ModSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.UUID;

public class RingtailsRunAwayGoal extends PanicGoal {
    protected int runTicks;
    protected BlockPos tipCasePos;
    public RingtailsRunAwayGoal(PathfinderMob mob, double speedModifier) {
        super(mob, speedModifier);
    }

    @Override
    public boolean canUse() {
        boolean tipCaseBroken = false;
        if (mob instanceof Faust faust) this.tipCasePos = faust.getTipCasePos();

        if (tipCasePos != null) {
            if (mob.level().getBlockState(tipCasePos).is(Blocks.AIR)) tipCaseBroken = true;
        }

        return super.canUse() || tipCaseBroken;
    }

    @Override
    public void start() {
        BlockState state = null;

        super.start();

        if (mob instanceof Faust faust && faust.getOrion() != null) {
            findRandomPosition();
            faust.getOrion().getNavigation().moveTo(this.posX, this.posY, this.posZ, this.speedModifier);
            faust.getOrion().setPlaying(false);
            faust.setPlaying(false);

            this.tipCasePos = faust.getTipCasePos();

        } else if (mob instanceof Orion orion && orion.getFaust() != null) {
            findRandomPosition();
            orion.getFaust().getNavigation().moveTo(this.posX, this.posY, this.posZ, this.speedModifier);
            orion.getFaust().setPlaying(false);
            orion.setPlaying(false);

            this.tipCasePos = orion.getFaust().getTipCasePos();
        }

        if (tipCasePos != null) state = mob.level().getBlockState(tipCasePos);

        if (tipCasePos != null && state.is(ModBlocks.TIP_CASE)) {
            mob.level().setBlock(tipCasePos, Blocks.AIR.defaultBlockState(), 3);
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
        Entity entity = mob.getLastHurtByMob();

        if (mob instanceof Faust) {
            orion = ((Faust) mob).getOrion();
        } else {
            faust = ((Orion) mob).getFaust();
        }

        if (orion != null) {
            level.sendParticles(ParticleTypes.POOF, orion.getX(), orion.getY(), orion.getZ(), 30, 0.1, 0.5, 0.1, 0.3);
            if (entity instanceof Player player) ModSavedData.saveConfidence(level, orion, player.getUUID(), -1);
            orion.remove(Entity.RemovalReason.DISCARDED);
        }

        if (faust != null) {
            level.sendParticles(ParticleTypes.POOF, faust.getX(), faust.getY(), faust.getZ(), 30, 0.1, 0.5, 0.1, 0.3);
            if (entity instanceof Player player) ModSavedData.saveConfidence(level, faust, player.getUUID(), -1);
            faust.remove(Entity.RemovalReason.DISCARDED);
        }

        level.sendParticles(ParticleTypes.POOF, mob.getX(), mob.getY(),mob.getZ(), 30, 0.1, 0.5, 0.1, 0.3);
        if (entity instanceof Player player) ModSavedData.saveConfidence(level, (TalkableEntity) mob, player.getUUID(), -1);
        mob.remove(Entity.RemovalReason.DISCARDED);
    }
}

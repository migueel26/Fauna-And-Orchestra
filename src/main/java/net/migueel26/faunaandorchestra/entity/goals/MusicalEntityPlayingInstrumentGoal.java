package net.migueel26.faunaandorchestra.entity.goals;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.custom.ConductorEntity;
import net.migueel26.faunaandorchestra.entity.custom.MusicalEntity;
import net.migueel26.faunaandorchestra.networking.StartOrchestraMusicS2CPayload;
import net.migueel26.faunaandorchestra.util.MusicUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.goal.Goal;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.EnumSet;
import java.util.Optional;

public class MusicalEntityPlayingInstrumentGoal extends Goal {
    private final MusicalEntity musician;
    private ConductorEntity conductor;

    public MusicalEntityPlayingInstrumentGoal(MusicalEntity musician) {
        this.musician = musician;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        Optional<ConductorEntity> conductor = this.musician.level()
                .getEntitiesOfClass(ConductorEntity.class, this.musician.getBoundingBox().inflate(7))
                .stream()
                .filter(cond -> cond.getOrchestra().stream().noneMatch(musician.getClass()::isInstance))
                .filter(ConductorEntity::isHoldingBaton)
                .filter(ConductorEntity::isHoldingASheetMusic)
                .findAny();

        if (!musician.isHoldingInstrument()) return false;

        conductor.ifPresent(musician::setConductor);

        if (musician.getConductor() != null && musician.getConductor().isOrchestraFull()) {
            musician.setConductor(null);
            return false;
        }

        return !musician.isDeadOrDying() && musician.isHoldingInstrument()
                && conductor.isPresent();
    }

    @Override
    public boolean canContinueToUse() {
        return musician.isHoldingInstrument() && conductor != null && !conductor.isDeadOrDying() && conductor.isHoldingBaton()
                && musician.distanceTo(conductor) <= 10 && conductor.isHoldingASheetMusic();
    }

    @Override
    public void start() {
        System.out.println("In!");
        conductor = musician.getConductor();

        // The musician joins the conductor's orchestra and we disable any possible inconsistent goal
        if (conductor.isOrchestraEmpty()) conductor.setTicksPlaying(0);
        conductor.addMusician(musician);
        //musician.updateGoals();

        // We get how many ticks the conductor has been conducting
        int ticksOffset = conductor.getTicksPlaying();

        // Start the musician's part
        PacketDistributor.sendToAllPlayers(new StartOrchestraMusicS2CPayload(musician.getUUID(),
                ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID,
                        MusicUtil.getLocation(conductor.getSheetMusic(), musician.getInstrument().get())),
                ticksOffset));
    }

    @Override
    public void stop() {
        if (conductor != null) {
            conductor.removeMusician(musician);
        }

        if (conductor.isOrchestraEmpty()) {
            conductor.setTicksPlaying(0);
        }

        musician.setConductor(null);
        conductor = null;

        System.out.println("Out!");
        //musician.updateGoals();
    }

    @Override
    public void tick() {
        // Make the musician look at the conductor at all times
        musician.getNavigation().stop();
        musician.getLookControl().setLookAt(conductor);
    }
}

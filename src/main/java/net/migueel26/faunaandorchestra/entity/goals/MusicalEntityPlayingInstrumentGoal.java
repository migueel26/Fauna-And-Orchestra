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
        if (musician.getTicksSinceLoaded() <= 20) return false;
        Optional<ConductorEntity> potentialConductor = this.musician.level()
                .getEntitiesOfClass(ConductorEntity.class, this.musician.getBoundingBox().inflate(7))
                .stream()
                .filter(cond -> cond.getOrchestra().stream().noneMatch(musician.getClass()::isInstance))
                .filter(ConductorEntity::isHoldingBaton)
                .filter(ConductorEntity::isHoldingASheetMusic)
                .findAny();

        if (!musician.isHoldingInstrument()) return false;

        potentialConductor.ifPresent(musician::setConductor);

        if (musician.getConductor() != null && musician.getConductor().isOrchestraFull()) {
            musician.setConductor(null);
            return false;
        }

        return !musician.isDeadOrDying() && musician.isHoldingInstrument()
                && potentialConductor.isPresent();
    }

    @Override
    public boolean canContinueToUse() {
        return musician.isHoldingInstrument() && conductor != null && conductor.isAlive() && conductor.isHoldingBaton()
                && musician.distanceTo(conductor) <= 10 && conductor.isHoldingASheetMusic();
    }

    @Override
    public void start() {
        //System.out.println("Musician IN!");
        conductor = musician.getConductor();

        // The musician joins the conductor's orchestra
        conductor.addMusician(musician);

        // We get how many ticks the conductor has been conducting
        int ticksOffset = conductor.getTicksPlaying();

        // Start the musician's part if it's a new musician
        if (!conductor.isReady()) {
            PacketDistributor.sendToAllPlayers(new StartOrchestraMusicS2CPayload(musician.getUUID(),
                    ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID,
                            MusicUtil.getLocation(conductor.getSheetMusic(), musician.getInstrument().get())),
                    ticksOffset));
        }
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

        //System.out.println("Musician OUT!");
    }

    @Override
    public void tick() {
        // Make the musician look at the conductor at all times
        musician.getNavigation().stop();
        musician.getLookControl().setLookAt(conductor);
    }
}

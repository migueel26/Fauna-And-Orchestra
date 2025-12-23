package net.migueel26.faunaandorchestra.entity.goals;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.advancements.ModAdvancements;
import net.migueel26.faunaandorchestra.entity.custom.ConductorEntity;
import net.migueel26.faunaandorchestra.entity.custom.MusicalEntity;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.networking.ModNetwork;
import net.migueel26.faunaandorchestra.networking.packets.StartOrchestraMusicS2CPacket;
import net.migueel26.faunaandorchestra.util.MusicUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;
import java.util.List;
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
                .filter(ConductorEntity::isHoldingBaton)
                .filter(ConductorEntity::isHoldingASheetMusic)
                .filter(cond -> cond.isMusicianApt(musician))
                .filter(cond -> cond.getOrchestra().stream().noneMatch(musician.getClass()::isInstance))
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

        List<Player> nearbyPlayers = this.conductor.level().getEntitiesOfClass(
                Player.class, this.conductor.getBoundingBox().inflate(32.0, 32.0, 32.0), EntitySelector.LIVING_ENTITY_STILL_ALIVE);

        // Start the musician's part if it's a new musician
        if (!conductor.isReady() && !nearbyPlayers.isEmpty()) {
            for (Player player : nearbyPlayers) {
                ModNetwork.sendToPlayer(new StartOrchestraMusicS2CPacket(musician.getUUID(),
                        ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID,
                                MusicUtil.getLocation(conductor.getSheetMusic(), musician.getInstrument().get())),
                        ticksOffset), (ServerPlayer) player);
            }

        }

        if (this.conductor.isOrchestraFull()) {
            if (conductor.getOwner() != null) {
                ModAdvancements.FULL_ORCHESTRA.trigger((ServerPlayer) conductor.getOwner());
            }
            int data = conductor.level().registryAccess().registryOrThrow(ModItems.ITEMS.getRegistryKey()).getId(conductor.getSheetMusic());
            conductor.level().levelEvent(null, 4005, this.conductor.blockPosition(), data);
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

package net.migueel26.faunaandorchestra.entity.goals;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.custom.ConductorEntity;
import net.migueel26.faunaandorchestra.entity.custom.MusicalEntity;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.item.custom.InstrumentItem;
import net.migueel26.faunaandorchestra.networking.StartOrchestraMusicPayload;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.migueel26.faunaandorchestra.sound.custom.InstrumentSoundInstance;
import net.migueel26.faunaandorchestra.util.MusicUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
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
                && musician.distanceTo(conductor) <= 10;
    }

    @Override
    public void start() {
        System.out.println("In!");
        conductor = musician.getConductor();

        // The musician joins the conductor's orchestra and we disable any possible inconsistent goal
        conductor.addMusician(musician);
        //musician.updateGoals();

        // We get how many ticks the conductor has been conducting
        int ticksOffset = conductor.getTicksPlaying();

        // Start the musician's part
        PacketDistributor.sendToAllPlayers(new StartOrchestraMusicPayload(musician.getUUID(),
                ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID,
                        // TODO: REPLACE WITH GENERIC SHEET
                        MusicUtil.getLocation(ModItems.BACH_AIR_SHEET_MUSIC.get(), musician.getInstrument().get())),
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

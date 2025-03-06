package net.migueel26.faunaandorchestra.entity.goals;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.custom.ConductorEntity;
import net.migueel26.faunaandorchestra.entity.custom.MusicalEntity;
import net.migueel26.faunaandorchestra.networking.StartOrchestraMusicPayload;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.migueel26.faunaandorchestra.sound.custom.InstrumentSoundInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Optional;

public class MusicalEntityPlayingInstrumentGoal extends Goal {
    private final MusicalEntity musician;

    public MusicalEntityPlayingInstrumentGoal(MusicalEntity musician) {
        this.musician = musician;
    }

    @Override
    public boolean canUse() {
        Optional<ConductorEntity> conductor = this.musician.level()
                .getEntitiesOfClass(ConductorEntity.class, this.musician.getBoundingBox().inflate(5))
                .stream()
                .filter(cond -> cond.getOrchestra().stream().noneMatch(musician.getClass()::isInstance))
                .filter(ConductorEntity::isHoldingBaton)
                .findAny();

        conductor.ifPresent(musician::setConductor);

        return !musician.isDeadOrDying() && musician.isHoldingInstrument()
                && musician.onGround() && musician.getConductor() != null;
    }

    @Override
    public boolean canContinueToUse() {
        return musician.isHoldingInstrument() && musician.getConductor().isHoldingBaton();
    }

    @Override
    public void start() {
        musician.getNavigation().stop();
        System.out.println("Hi!");
        musician.getConductor().addMusician(musician);
        PacketDistributor.sendToAllPlayers(new StartOrchestraMusicPayload(musician.getUUID(),
                ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "sounds/music/bach_air_violin"), 0));
    }

    @Override
    public void stop() {
        if (musician.getConductor() != null) {
            musician.getConductor().removeMusician(musician);
            musician.setConductor(null);
        }
        System.out.println("hANNA!");
    }

    @Override
    public void tick() {
        ConductorEntity conductor = musician.getConductor();
        this.musician.getLookControl().setLookAt(conductor);
    }
}

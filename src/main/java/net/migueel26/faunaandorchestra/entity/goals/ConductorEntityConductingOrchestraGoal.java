package net.migueel26.faunaandorchestra.entity.goals;

import net.migueel26.faunaandorchestra.entity.custom.ConductorEntity;
import net.migueel26.faunaandorchestra.entity.custom.MusicalEntity;
import net.migueel26.faunaandorchestra.networking.RestartOrchestraMusicS2CPayload;
import net.migueel26.faunaandorchestra.networking.StopOrchestraMusicS2CPayload;
import net.migueel26.faunaandorchestra.util.MusicUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.*;

public class ConductorEntityConductingOrchestraGoal extends Goal {
    private final ConductorEntity conductor;
    private List<Player> playersListening;
    private int lookCooldown;
    private int waitForMoreMusicians;
    private int currentOrchestraSize;
    public ConductorEntityConductingOrchestraGoal(ConductorEntity conductor) {
        this.conductor = conductor;
    }

    @Override
    public boolean canUse() {
        return !conductor.isOrchestraEmpty() && !conductor.isDeadOrDying() && conductor.isHoldingBaton();
    }

    @Override
    public boolean canContinueToUse() {
        return !conductor.isOrchestraEmpty() && !conductor.isDeadOrDying() && conductor.isHoldingBaton();
    }

    @Override
    public void start() {
        //System.out.println("Conductor IN!");
        MusicUtil.addNewOrchestra(conductor.getUUID(), conductor.getSheetMusic());
        this.lookCooldown = 0;
        this.playersListening = this.conductor.level().getEntitiesOfClass(
                Player.class, this.conductor.getBoundingBox().inflate(50.0, 50.0, 50.0), EntitySelector.LIVING_ENTITY_STILL_ALIVE);

        this.currentOrchestraSize = this.conductor.getOrchestra().size();
        this.waitForMoreMusicians = conductor.isReady() ? 140 : -1;
        super.start();

    }

    @Override
    public void stop() {
        //System.out.println("Conductor OUT!");
        MusicUtil.deleteOrchestra(conductor.getUUID());
        super.stop();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        conductor.getNavigation().stop();

        if (lookCooldown <= 0) {
            conductor.getLookControl().setLookAt(getCentroid());
            lookCooldown = 20;
        } else {
            lookCooldown--;
        }

        if (waitForMoreMusicians > 0) {
            if (currentOrchestraSize != this.conductor.getOrchestra().size()) {
                waitForMoreMusicians = 140;
                this.currentOrchestraSize = this.conductor.getOrchestra().size();
            } else {
                waitForMoreMusicians--;
            }
        }

        if (waitForMoreMusicians == 0 || conductor.getTicksPlaying() == 2550) {
            waitForMoreMusicians = -1;
            conductor.setTicksPlaying(0);

            List<Player> nearbyPlayers = this.conductor.level().getEntitiesOfClass(
                    Player.class, this.conductor.getBoundingBox().inflate(32.0, 32.0, 32.0), EntitySelector.LIVING_ENTITY_STILL_ALIVE);

            for (Player player : nearbyPlayers) {
                PacketDistributor.sendToPlayer((ServerPlayer) player, new RestartOrchestraMusicS2CPayload(
                        conductor.getUUID(),
                        conductor.getOrchestra().stream().map(Entity::getUUID).toList(),
                        conductor.getTicksPlaying(),
                        conductor.getCurrentVolume(),
                        conductor.getSheetMusic().toString()));
            }
        }

        List<Player> nearbyPlayers = this.conductor.level().getEntitiesOfClass(
                Player.class, this.conductor.getBoundingBox().inflate(32.0, 32.0, 32.0), EntitySelector.LIVING_ENTITY_STILL_ALIVE);

        // We find which players weren't nearby before and now are and send Packets to them
        List<Player> newPlayers = new ArrayList<>(nearbyPlayers);
        List<Player> exitPlayers = new ArrayList<>(playersListening);
        exitPlayers.removeAll(nearbyPlayers);
        newPlayers.removeAll(playersListening);
        for (Player player : newPlayers) {
            PacketDistributor.sendToPlayer((ServerPlayer) player, new RestartOrchestraMusicS2CPayload(
                    conductor.getUUID(),
                    conductor.getOrchestra().stream().map(Entity::getUUID).toList(),
                    conductor.getTicksPlaying(),
                    conductor.getCurrentVolume(),
                    conductor.getSheetMusic().toString()));
        }

        for (Player player : exitPlayers) {
            PacketDistributor.sendToPlayer((ServerPlayer) player, new StopOrchestraMusicS2CPayload(
                    conductor.getOrchestra().stream().map(Entity::getUUID).toList()
            ));
        }

        playersListening = nearbyPlayers;

    }

    private Vec3 getCentroid() {
        if (!conductor.getOrchestra().isEmpty()) {
            Set<MusicalEntity> orchestra = conductor.getOrchestra();
            double n = orchestra.size();

            return new Vec3(
                    orchestra.stream().map(Entity::getX).reduce(0.0, Double::sum)/n,
                    conductor.getY(),
                    orchestra.stream().map(Entity::getZ).reduce(0.0, Double::sum)/n);
        } else {
            return new Vec3(0.0,0.0,0.0);
        }
    }
}

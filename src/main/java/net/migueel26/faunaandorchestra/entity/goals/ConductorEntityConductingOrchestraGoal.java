package net.migueel26.faunaandorchestra.entity.goals;

import net.migueel26.faunaandorchestra.advancements.ModAdvancements;
import net.migueel26.faunaandorchestra.entity.custom.ConductorEntity;
import net.migueel26.faunaandorchestra.entity.custom.MusicalEntity;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.networking.ModNetwork;
import net.migueel26.faunaandorchestra.networking.packets.RestartOrchestraMusicS2CPacket;
import net.migueel26.faunaandorchestra.networking.packets.StopOrchestraMusicS2CPacket;
import net.migueel26.faunaandorchestra.util.MusicUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

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
        // PARROT DANCE
        conductor.level().levelEvent(null, 1011, this.conductor.blockPosition(), 0);
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

        if (waitForMoreMusicians == 0 || conductor.getTicksPlaying() == MusicUtil.getDuration(conductor.getSheetMusic())) {
            conductor.setReady(false);
            waitForMoreMusicians = -1;

            if (this.conductor.isOrchestraFull() && conductor.getOwner() != null) {
                ModAdvancements.FULL_ORCHESTRA.trigger((ServerPlayer) conductor.getOwner());
            }

            List<Player> nearbyPlayers = this.conductor.level().getEntitiesOfClass(
                    Player.class, this.conductor.getBoundingBox().inflate(32.0, 32.0, 32.0), EntitySelector.LIVING_ENTITY_STILL_ALIVE);

            conductor.setTicksPlaying(0);

            // PARROT DANCE
            int data = conductor.level().registryAccess().registryOrThrow(ModItems.ITEMS.getRegistryKey()).getId(conductor.getSheetMusic());
            conductor.level().levelEvent(null, 4005, this.conductor.blockPosition(), data);

            for (Player player : nearbyPlayers) {
                if (player instanceof  ServerPlayer serverPlayer) {
                    ModNetwork.sendToPlayer(new RestartOrchestraMusicS2CPacket(
                                conductor.getUUID(),
                                conductor.getOrchestra().stream().map(Entity::getUUID).toList(),
                                conductor.getTicksPlaying(),
                                conductor.getCurrentVolume(),
                                conductor.getSheetMusic().toString()),
                            serverPlayer);
                }
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
            if (player instanceof  ServerPlayer serverPlayer) {
                ModNetwork.sendToPlayer(new RestartOrchestraMusicS2CPacket(
                                conductor.getUUID(),
                                conductor.getOrchestra().stream().map(Entity::getUUID).toList(),
                                conductor.getTicksPlaying(),
                                conductor.getCurrentVolume(),
                                conductor.getSheetMusic().toString()),
                        serverPlayer);
            }
        }

        for (Player player : exitPlayers) {
            if (player instanceof ServerPlayer serverPlayer) {
                ModNetwork.sendToPlayer(new StopOrchestraMusicS2CPacket(
                            conductor.getOrchestra().stream().map(Entity::getUUID).toList()),
                        serverPlayer);
            }
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

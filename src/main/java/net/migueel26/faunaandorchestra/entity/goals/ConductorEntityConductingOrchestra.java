package net.migueel26.faunaandorchestra.entity.goals;

import net.migueel26.faunaandorchestra.entity.custom.ConductorEntity;
import net.migueel26.faunaandorchestra.entity.custom.MusicalEntity;
import net.migueel26.faunaandorchestra.networking.RestartOrchestraMusicS2CPayload;
import net.migueel26.faunaandorchestra.util.MusicUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConductorEntityConductingOrchestra extends Goal {
    private final ConductorEntity conductor;
    private List<Player> playersListening;
    private int lookCooldown;
    public ConductorEntityConductingOrchestra(ConductorEntity conductor) {
        this.conductor = conductor;
    }

    @Override
    public boolean canUse() {
        return !conductor.isOrchestraEmpty() && !conductor.isDeadOrDying() && conductor.isHoldingBaton()
                && conductor.isReady();
    }

    @Override
    public boolean canContinueToUse() {
        return !conductor.isOrchestraEmpty() && !conductor.isDeadOrDying() && conductor.isHoldingBaton();
    }

    @Override
    public void start() {
        System.out.println("Conductor IN!");
        MusicUtil.addNewOrchestra(conductor.getUUID(), conductor.getSheetMusic());
        this.lookCooldown = 0;
        this.playersListening = this.conductor.level().getEntitiesOfClass(
                Player.class, this.conductor.getBoundingBox().inflate(50.0, 50.0, 50.0), EntitySelector.LIVING_ENTITY_STILL_ALIVE);
        super.start();

    }

    @Override
    public void stop() {
        System.out.println("Conductor OUT!");
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

        List<Player> nearbyPlayers = this.conductor.level().getEntitiesOfClass(
                Player.class, this.conductor.getBoundingBox().inflate(45.0, 45.0, 45.0), EntitySelector.LIVING_ENTITY_STILL_ALIVE);

        // We find which players weren't nearby before and now are and send Packets to them
        List<Player> newPlayers = new ArrayList<>(nearbyPlayers);
        newPlayers.removeAll(playersListening);
        for (Player player : newPlayers) {
            PacketDistributor.sendToPlayer((ServerPlayer) player, new RestartOrchestraMusicS2CPayload(
                    conductor.getUUID(),
                    conductor.getOrchestra().stream().map(Entity::getUUID).toList(),
                    conductor.getTicksPlaying(),
                    conductor.getCurrentVolume()));
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

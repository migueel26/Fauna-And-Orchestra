package net.migueel26.faunaandorchestra.networking;

import net.migueel26.faunaandorchestra.entity.custom.ConductorEntity;
import net.migueel26.faunaandorchestra.entity.custom.MusicalEntity;
import net.migueel26.faunaandorchestra.mixins.client.accessors.ClientLevelAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ServerPayloadHandler {
    public static void handleEmpty(CustomPacketPayload payload, IPayloadContext iPayloadContext) {

    }

    public static void handleRestartOrchestraOnNetwork(RestartOrchestraMusicC2SPayload payload, IPayloadContext context) {
        Player player = context.player();
        Level level = player.level();
        UUID conductorUUID = payload.conductorUUID();
        if (level != null) {
            ConductorEntity conductor = (ConductorEntity) ((ServerLevel) level).getEntity(conductorUUID);
            if (conductor != null && !conductor.isOrchestraEmpty()) {
                conductor.setCurrentVolume(payload.volume());
                List<UUID> orchestra = conductor.getOrchestra().stream().map(Entity::getUUID).toList();
                int tickOffset = conductor.getTicksPlaying();
                PacketDistributor.sendToAllPlayers(new RestartOrchestraMusicS2CPayload(
                        conductorUUID,
                        orchestra,
                        tickOffset,
                        payload.volume()
                        ));
            }
        }
     }
}

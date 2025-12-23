package net.migueel26.faunaandorchestra.networking;

import net.migueel26.faunaandorchestra.block.ModBlocks;
import net.migueel26.faunaandorchestra.block.entity.TipCaseBlockEntity;
import net.migueel26.faunaandorchestra.entity.custom.ConductorEntity;
import net.migueel26.faunaandorchestra.networking.packets.RestartOrchestraMusicS2CPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.core.jmx.Server;

import java.util.List;
import java.util.UUID;

public class ServerPacketHandler {

    public static void handleRestartOrchestraOnNetwork(ServerPlayer player, UUID conductorUUID, float volume) {
        ServerLevel level = player.serverLevel();

        Entity entity = level.getEntity(conductorUUID);

        if (entity instanceof ConductorEntity conductor) {
            if (!conductor.isOrchestraEmpty()) {
                conductor.setCurrentVolume(volume);

                List<UUID> orchestra = conductor.getOrchestra().stream()
                        .map(Entity::getUUID)
                        .toList();

                int tickOffset = conductor.getTicksPlaying();

                RestartOrchestraMusicS2CPacket responsePacket = new RestartOrchestraMusicS2CPacket(
                        conductorUUID,
                        orchestra,
                        tickOffset,
                        volume,
                        conductor.getSheetMusic().toString()
                );

                ModNetwork.sendToPlayer(responsePacket, player);
            }
        }
    }

    public static void handleSyncTipCaseOnNetwork(Player player, UUID uuid, int x, int y, int z) {
        BlockPos blockPos = new BlockPos(x, y, z);
        ServerLevel level = (ServerLevel) player.level();

        BlockState state = level.getBlockState(blockPos);
        Entity entity = level.getEntity(uuid);
        if (state.getBlock() == ModBlocks.TIP_CASE.get() && entity != null) {
            BlockEntity blockEntity = level.getBlockEntity(blockPos);
            ((TipCaseBlockEntity) blockEntity).setOwner(uuid);
        }
    }
}

package net.migueel26.faunaandorchestra.networking;

import net.migueel26.faunaandorchestra.block.ModBlocks;
import net.migueel26.faunaandorchestra.block.entity.MailboxBlockEntity;
import net.migueel26.faunaandorchestra.block.entity.TipCaseBlockEntity;
import net.migueel26.faunaandorchestra.component.ModDataComponents;
import net.migueel26.faunaandorchestra.entity.custom.ConductorEntity;
import net.migueel26.faunaandorchestra.entity.custom.koala_workers.TailorKoalaEntity;
import net.migueel26.faunaandorchestra.networking.packets.RestartOrchestraMusicS2CPacket;
import net.migueel26.faunaandorchestra.screen.custom.LetterAndQuillMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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

    public static void handleTailorKoalaStartSewingOnNetwork(Player player, UUID uuid, boolean sewing, ItemStack choice) {
        ServerLevel level = (ServerLevel) player.level();

        Entity entity = level.getEntity(uuid);
        if (entity instanceof TailorKoalaEntity koala) {
            koala.setCatalogChoice(choice);
            if (koala.tryToSew()) {
                player.closeContainer();
            }
        }
    }

    public static void handleWriteEmailOnNetwork(Player player, String sender, String receiver, int x, int y, int z) {
        if (player.containerMenu instanceof LetterAndQuillMenu menu) {
            ItemStack serverStack = menu.getLetterItem();

            if (!serverStack.isEmpty()) {
                CompoundTag tag = serverStack.getOrCreateTag();

                if (!sender.isEmpty()) {
                    tag.putString(ModDataComponents.SENDER, sender);
                }
                if (!receiver.isEmpty()) {
                    tag.putString(ModDataComponents.RECEIVER, receiver);
                }
                if (x != -1) {
                    tag.putIntArray(ModDataComponents.POSITION, new int[]{x, y, z});
                }
            }
        }
    }

    public static void handleEraseEmailOnNetwork(Player player, ItemStack stack) {
        if (player.containerMenu instanceof LetterAndQuillMenu menu) {
            ItemStack menuStack = menu.getLetterItem();

            if (!menuStack.isEmpty() && ItemStack.isSameItem(stack, menuStack)) {
                if (menuStack.hasTag()) {
                    CompoundTag tag = menuStack.getTag();

                    tag.remove(ModDataComponents.SENDER);
                    tag.remove(ModDataComponents.RECEIVER);
                    tag.remove(ModDataComponents.POSITION);

                    if (tag.isEmpty()) {
                        menuStack.setTag(null);
                    }
                }
            }
        }
    }

    public static void handleMailbirdFlyAwayOnNetwork(Player player, BlockPos pos) {
        ServerLevel level = (ServerLevel) player.level();

        if (level.getBlockEntity(pos) instanceof MailboxBlockEntity mailbox) {
            mailbox.triggerDelivery();
        }
    }
}

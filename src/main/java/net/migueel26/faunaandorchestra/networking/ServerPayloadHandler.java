package net.migueel26.faunaandorchestra.networking;

import net.migueel26.faunaandorchestra.block.ModBlocks;
import net.migueel26.faunaandorchestra.block.entity.MailboxBlockEntity;
import net.migueel26.faunaandorchestra.block.entity.TipCaseBlockEntity;
import net.migueel26.faunaandorchestra.component.ModDataComponents;
import net.migueel26.faunaandorchestra.entity.custom.ConductorEntity;
import net.migueel26.faunaandorchestra.entity.custom.koala_workers.TailorKoalaEntity;
import net.migueel26.faunaandorchestra.screen.custom.LetterAndQuillMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

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
                PacketDistributor.sendToPlayer((ServerPlayer) player, new RestartOrchestraMusicS2CPayload(
                        conductorUUID,
                        orchestra,
                        tickOffset,
                        payload.volume(),
                        conductor.getSheetMusic().toString()
                        ));
            }
        }
     }

    public static void handleSyncTipCaseOnNetwork(SyncTipCaseOwnerPayloadC2S payload, IPayloadContext context) {
        UUID uuid = payload.owner();
        BlockPos blockPos = new BlockPos(payload.x(), payload.y(), payload.z());
        ServerLevel level = (ServerLevel) context.player().level();

        BlockState state = level.getBlockState(blockPos);
        Entity entity = level.getEntity(uuid);
        if (state.getBlock() == ModBlocks.TIP_CASE.get() && entity != null) {
            BlockEntity blockEntity = level.getBlockEntity(blockPos);
            ((TipCaseBlockEntity) blockEntity).setOwner(uuid);
        }
    }

    public static void handleTailorKoalaStartSewingOnNetwork(TailorKoalaStartSewingC2SPayload payload, IPayloadContext context) {
        UUID uuid = payload.tailorUUID();
        Player player = context.player();
        ItemStack choice = payload.catalogChoice();
        ServerLevel level = (ServerLevel) player.level();

        Entity entity = level.getEntity(uuid);
        if (entity instanceof TailorKoalaEntity koala) {
            koala.setCatalogChoice(choice);
            if (koala.tryToSew()) {
                player.closeContainer();
            }
        }
    }

    public static void handleWriteEmailOnNetwork(WriteMailC2SPayload payload, IPayloadContext context) {
        Player player = context.player();
        String sender = payload.sender();
        String receiver = payload.receiver();
        int x = payload.x();
        int y = payload.y();
        int z = payload.z();

        if (player.containerMenu instanceof LetterAndQuillMenu menu) {
            ItemStack serverStack = menu.getLetterItem();

            if (!serverStack.isEmpty()) {
                if (!sender.isEmpty()) {
                    serverStack.set(ModDataComponents.SENDER, sender);
                }
                if (!receiver.isEmpty()) {
                    serverStack.set(ModDataComponents.RECEIVER, receiver);
                }
                if (x != -1) {
                    serverStack.set(ModDataComponents.POSITION, new BlockPos(x, y, z));
                }
            }
        }
    }

    public static void handleEraseEmailOnNetwork(EraseMailC2SPayload payload, IPayloadContext context) {
        Player player = context.player();

        if (player.containerMenu instanceof LetterAndQuillMenu menu) {
            ItemStack stack = menu.getLetterItem();

            if (!stack.isEmpty() && ItemStack.isSameItem(payload.stack(), stack)) {
                stack.remove(ModDataComponents.SENDER);
                stack.remove(ModDataComponents.RECEIVER);
                stack.remove(ModDataComponents.POSITION);
            }
        }
    }

    public static void handleMailbirdFlyAwayOnNetwork(MailbirdFlyAwayC2SPayload payload, IPayloadContext context) {
        BlockPos pos = payload.pos();
        Player player = context.player();
        ServerLevel level = (ServerLevel) player.level();

        if (level.getBlockEntity(pos) instanceof MailboxBlockEntity mailbox) {
            mailbox.triggerDelivery();
        }
    }
}

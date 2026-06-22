package net.migueel26.faunaandorchestra.util;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Collection;

public class AdvancementUtil {

    public static boolean hasAdvancement(Player player, String namespace, String path) {
        if (player instanceof ServerPlayer serverPlayer) {
            ResourceLocation advancementId = ResourceLocation.fromNamespaceAndPath(namespace, path);

            var advancement = serverPlayer.getServer().getAdvancements().get(advancementId);

            if (advancement != null) {
                return serverPlayer.getAdvancements().getOrStartProgress(advancement).isDone();
            }
        }
        return false;
    }

    public static void unlock(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            PlayerAdvancements playerAdvancements = serverPlayer.getAdvancements();
            Collection<AdvancementHolder> allAdvancements = serverPlayer.getServer().getAdvancements().getAllAdvancements();

            for (AdvancementHolder advancement : allAdvancements) {
                if (advancement.id().getNamespace().equals(FaunaAndOrchestra.MOD_ID)) {
                    var progress = playerAdvancements.getOrStartProgress(advancement);

                    if (!progress.isDone()) {
                        for (String criterion : progress.getRemainingCriteria()) {
                            playerAdvancements.award(advancement, criterion);
                        }
                    }
                }
            }
        }
    }
}
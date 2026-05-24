package net.migueel26.faunaandorchestra.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

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
}
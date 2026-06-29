package net.migueel26.faunaandorchestra.event;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FaunaAndOrchestra.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CITestHandler {

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        if (System.getenv("CI") != null) {
            FaunaAndOrchestra.LOGGER.info("[Fauna & Orchestra CI] The server was started successfully. Shutting down server after CI test.");

            event.getServer().halt(false);
        }
    }
}
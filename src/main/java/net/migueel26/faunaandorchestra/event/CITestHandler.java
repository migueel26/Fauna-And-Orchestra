package net.migueel26.faunaandorchestra.event;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

@EventBusSubscriber(modid = FaunaAndOrchestra.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class CITestHandler {

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        if (System.getenv("FAUNA_IS_TEST_SERVER") != null) {
            FaunaAndOrchestra.LOGGER.info("[Fauna & Orchestra CI] The server was started successfully. Shutting down server after CI test.");

            event.getServer().halt(false);
        }
    }
}
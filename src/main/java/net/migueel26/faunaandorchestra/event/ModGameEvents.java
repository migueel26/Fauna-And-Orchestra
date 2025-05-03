package net.migueel26.faunaandorchestra.event;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.custom.QuirkyFrogEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@EventBusSubscriber(modid = FaunaAndOrchestra.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class ModGameEvents {
    @SubscribeEvent
    public static void quirkyFrogChoir(EntityTickEvent.Post event) {
        if (event.getEntity().tickCount % 20 == 0 &&
                event.getEntity().level().getRandom().nextFloat() <= 0.01F &&
                event.getEntity() instanceof QuirkyFrogEntity quirkyFrog
                && quirkyFrog.isAptForChoir()) {

            List<QuirkyFrogEntity> nearbyFrogs = quirkyFrog.level().getEntitiesOfClass(QuirkyFrogEntity.class, quirkyFrog.getBoundingBox().inflate(30))
                    .stream().filter(QuirkyFrogEntity::isAptForChoir).toList();

            if (nearbyFrogs.size() >= 5) {
                List<QuirkyFrogEntity> frogChoir = nearbyFrogs.stream().limit(5).toList();
                QuirkyFrogEntity choirConductor = frogChoir.getFirst();
                choirConductor.setFrogChoir(frogChoir.subList(1, frogChoir.size()));
            }
        }
    }
}

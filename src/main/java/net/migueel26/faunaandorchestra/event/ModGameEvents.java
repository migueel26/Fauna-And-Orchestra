package net.migueel26.faunaandorchestra.event;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.block.ModBlocks;
import net.migueel26.faunaandorchestra.block.custom.TipCaseBlock;
import net.migueel26.faunaandorchestra.block.entity.TipCaseBlockEntity;
import net.migueel26.faunaandorchestra.entity.custom.MusicalEntity;
import net.migueel26.faunaandorchestra.entity.custom.PenguinEntity;
import net.migueel26.faunaandorchestra.entity.custom.QuirkyFrogEntity;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import net.neoforged.neoforge.event.entity.living.MobSpawnEvent;
import net.neoforged.neoforge.event.entity.living.SpawnClusterSizeEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@EventBusSubscriber(modid = FaunaAndOrchestra.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class ModGameEvents {
    private static int musicalityIndex;

    @SubscribeEvent
    public static void quirkyFrogChoir(EntityTickEvent.Post event) {
        if (!event.getEntity().level().isClientSide() &&
                event.getEntity().tickCount % 80 == 0 &&
                event.getEntity().level().getRandom().nextFloat() <= 0.01F &&
                event.getEntity() instanceof QuirkyFrogEntity quirkyFrog
                && quirkyFrog.isAptForChoir()) {

            List<QuirkyFrogEntity> nearbyFrogs = quirkyFrog.level().getEntitiesOfClass(QuirkyFrogEntity.class, quirkyFrog.getBoundingBox().inflate(30))
                    .stream().filter(QuirkyFrogEntity::isAptForChoir).toList();

            if (nearbyFrogs.size() >= 5) {
                List<QuirkyFrogEntity> frogChoir = nearbyFrogs.stream().limit(5).toList();
                QuirkyFrogEntity choirConductor = frogChoir.getFirst();
                choirConductor.setFrogChoir(frogChoir.subList(1, frogChoir.size()));
                for (QuirkyFrogEntity chorister : choirConductor.getFrogChoir()) {
                    chorister.setSinging(true);
                    chorister.setReady(false);
                }
            }
        }
    }

    @SubscribeEvent
    public static void assignMusicality(FinalizeSpawnEvent event) {
        if (event.getEntity() instanceof MusicalEntity musicalEntity) {
            musicalityIndex++;
            if (musicalityIndex == 3) {
                musicalityIndex = 0;
                musicalEntity.setMusical();
            }
        }
    }

    @SubscribeEvent
    public static void placeTipCase(BlockEvent.EntityPlaceEvent event) {
        Entity entity = event.getEntity();
        BlockState block = event.getPlacedBlock();
        if (block.getBlock() == ModBlocks.TIP_CASE.get() && entity instanceof Player player
                && !player.getMainHandItem().is(ModItems.ICON.get()) && !player.getOffhandItem().is(ModItems.ICON.get())) {
            ((TipCaseBlockEntity) event.getLevel().getBlockEntity(event.getPos())).setOwner(player.getUUID());
        }
    }
}

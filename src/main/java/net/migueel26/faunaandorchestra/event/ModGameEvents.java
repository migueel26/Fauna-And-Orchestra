package net.migueel26.faunaandorchestra.event;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.block.ModBlocks;
import net.migueel26.faunaandorchestra.block.entity.TipCaseBlockEntity;
import net.migueel26.faunaandorchestra.effect.ModEffects;
import net.migueel26.faunaandorchestra.entity.custom.MusicalEntity;
import net.migueel26.faunaandorchestra.entity.custom.QuirkyFrogEntity;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.List;

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
    public static void whistleClickOnEntity(PlayerInteractEvent.EntityInteractSpecific event) {
        ItemStack stack = event.getItemStack();

        if (stack.is(ModItems.WHISTLE) && event.getTarget() instanceof TamableAnimal animal) {
            stack.use(event.getLevel(), event.getEntity(), event.getHand());
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

    @SubscribeEvent
    public static void onBoogieEnd(MobEffectEvent.Expired event) {
        if (event.getEffectInstance().is(ModEffects.BOOGIE)) {
            if (event.getEntity() instanceof PathfinderMob mob) {
                mob.setNoAi(false);
            }
        }
    }
}

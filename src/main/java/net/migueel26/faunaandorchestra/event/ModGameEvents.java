package net.migueel26.faunaandorchestra.event;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.block.ModBlocks;
import net.migueel26.faunaandorchestra.block.custom.TipCaseBlock;
import net.migueel26.faunaandorchestra.block.entity.TipCaseBlockEntity;
import net.migueel26.faunaandorchestra.effect.ModEffects;
import net.migueel26.faunaandorchestra.entity.custom.Faust;
import net.migueel26.faunaandorchestra.entity.custom.MusicalEntity;
import net.migueel26.faunaandorchestra.entity.custom.Orion;
import net.migueel26.faunaandorchestra.entity.custom.QuirkyFrogEntity;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.item.custom.RingtailsPosterItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.decoration.BlockAttachedEntity;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.living.MobSpawnEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.List;
import java.util.Optional;

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

    @SubscribeEvent
    public static void onFaustTick(EntityTickEvent.Post event) {
        if (event.getEntity() instanceof Faust faust && faust.getTipCasePos() == null && faust.getOrion() != null) {
            Vec3 vec = faust.position().subtract(faust.getOrion().position());
            Direction facing = Direction.getNearest(vec);
            Direction direction = Direction.getNearest(vec.yRot((float) (Math.PI/2)));

            BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos(faust.getX(), faust.getY(), faust.getZ());
            blockPos = blockPos.move(direction);
            event.getEntity().level().setBlock(blockPos,
                    ModBlocks.TIP_CASE.get().defaultBlockState().setValue(TipCaseBlock.FACING, facing), 3);
            ((TipCaseBlockEntity) event.getEntity().level().getBlockEntity(blockPos)).setOwner(faust.getUUID());

            faust.setTipCasePos(blockPos);

            BlockPos headPos = blockPos.move(facing.getOpposite());
            event.getEntity().level().setBlock(headPos,
                    ModBlocks.TIP_CASE.get().defaultBlockState().setValue(TipCaseBlock.FACING, facing).setValue(TipCaseBlock.PART, BedPart.HEAD), 3);
            ((TipCaseBlockEntity) event.getEntity().level().getBlockEntity(headPos)).setOwner(faust.getUUID());
        }
    }
}

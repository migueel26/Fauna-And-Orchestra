package net.migueel26.faunaandorchestra.event;

import net.migueel26.faunaandorchestra.Config;
import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.block.ModBlocks;
import net.migueel26.faunaandorchestra.block.custom.TipCaseBlock;
import net.migueel26.faunaandorchestra.block.entity.TipCaseBlockEntity;
import net.migueel26.faunaandorchestra.component.ModDataComponents;
import net.migueel26.faunaandorchestra.effect.ModEffects;
import net.migueel26.faunaandorchestra.entity.ModEntities;
import net.migueel26.faunaandorchestra.entity.custom.*;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.item.custom.RingtailsPosterItem;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
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
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import vazkii.patchouli.api.PatchouliAPI;

import java.util.List;
import java.util.Optional;

@Mod.EventBusSubscriber(modid = FaunaAndOrchestra.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModGameEvents {
    private static int musicalityIndex;
    public static final float BUG_CHANCE = 0.1f;

    @SubscribeEvent
    public static void quirkyFrogChoir(LivingEvent.LivingTickEvent event) {
        if (!event.getEntity().level().isClientSide() &&
                event.getEntity().tickCount % 60 == 0 &&
                event.getEntity().level().getRandom().nextFloat() <= 0.01F &&
                event.getEntity() instanceof QuirkyFrogEntity quirkyFrog
                && quirkyFrog.isAptForChoir()) {

            List<QuirkyFrogEntity> nearbyFrogs = quirkyFrog.level().getEntitiesOfClass(QuirkyFrogEntity.class, quirkyFrog.getBoundingBox().inflate(30))
                    .stream().filter(QuirkyFrogEntity::isAptForChoir).toList();

            if (nearbyFrogs.size() >= 5) {
                List<QuirkyFrogEntity> frogChoir = nearbyFrogs.stream().limit(5).toList();
                QuirkyFrogEntity choirConductor = frogChoir.get(0);
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

        if (stack.is(ModItems.WHISTLE.get()) && event.getTarget() instanceof TamableAnimal animal) {
            stack.use(event.getLevel(), event.getEntity(), event.getHand());
        }
    }

    @SubscribeEvent
    public static void assignMusicality(MobSpawnEvent.FinalizeSpawn event) {
        if (event.getEntity() instanceof MusicalEntity musicalEntity) {
            musicalityIndex++;
            if (musicalityIndex == 3) {
                musicalityIndex = 0;
                musicalEntity.setMusical(true);
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
        if (event.getEffectInstance().getEffect().equals(ModEffects.BOOGIE.get())) {
            if (event.getEntity() instanceof PathfinderMob mob) {
                mob.setNoAi(false);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        CompoundTag persistentData = player.getPersistentData();
        CompoundTag data = persistentData.getCompound(ServerPlayer.PERSISTED_NBT_TAG);

        if (!data.getBoolean("hasJoinedBefore")) {
            data.putBoolean("hasJoinedBefore", true);
            persistentData.put(ServerPlayer.PERSISTED_NBT_TAG, data);

            if (Config.anyaSpawn) {
                // Anya SHOULD spawn
                Vec3 look = player.getLookAngle(); // The player's look direction (normalized)
                double distance = 2.0;

                AnyaGhost anyaGhost = new AnyaGhost(ModEntities.ANYA_GHOST.get(), event.getEntity().level());
                anyaGhost.setPos(player.position().add(
                        look.x * distance,
                        look.y * distance,   // Slightly above ground if needed
                        look.z * distance
                ));

                anyaGhost.lookAt(EntityAnchorArgument.Anchor.FEET, player.position().add(0, 1, 0));
                anyaGhost.lookAt(EntityAnchorArgument.Anchor.EYES, player.position().add(0, 1, 0));
                anyaGhost.setYRot(anyaGhost.getYHeadRot());
                anyaGhost.setYBodyRot(anyaGhost.getYRot());
                anyaGhost.setPlayerUUID(player.getUUID());

                player.level().addFreshEntity(anyaGhost);
            } else {
                // Anya SHOULD NOT spawn
                if (Config.giveBook) {
                    // The book SHOULD BE given
                    player.addItem(PatchouliAPI.get().getBookStack(ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "symphonia")));
                }
            }
        }
    }

    @SubscribeEvent
    public static void onFaustTick(LivingEvent.LivingTickEvent event) {
        if (event.getEntity() instanceof Faust faust && faust.getTipCasePos() == null && faust.getOrion() != null) {
            Vec3 vec = faust.position().subtract(faust.getOrion().position());
            Direction facing = Direction.getNearest(vec.x, vec.y, vec.z);
            Vec3 rotatedVec = vec.yRot((float) (Math.PI/2));
            Direction direction = Direction.getNearest(rotatedVec.x, rotatedVec.y, rotatedVec.z);

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

    @SubscribeEvent
    public static void onToolModification(BlockEvent.BlockToolModificationEvent event) {
        if (event.isSimulated()) return;

        if (event.getLevel() instanceof ServerLevel level) {
            BlockState originalState = event.getState();
            RandomSource random = level.getRandom();
            float chance = random.nextFloat();

            if (chance <= BUG_CHANCE) {
                ItemStack bug = ItemStack.EMPTY;
                BlockPos pos = event.getPos();
                int quantity = random.nextIntBetweenInclusive(1, 3);

                if (originalState.getToolModifiedState(event.getContext(), ToolActions.SHOVEL_FLATTEN, true) != null) {
                    bug = new ItemStack(ModItems.WORM.get(), quantity);
                }

                else if (originalState.is(BlockTags.LOGS) && originalState.getToolModifiedState(event.getContext(), ToolActions.AXE_STRIP, true) != null) {
                    bug = new ItemStack(ModItems.INSECT.get(), quantity);
                }

                if (!bug.isEmpty()) {
                    Containers.dropItemStack(level, pos.getX(), pos.getY() + 1, pos.getZ(), bug);
                    level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, event.getState()),
                            pos.getCenter().x, pos.getY()+1, pos.getCenter().z, 15, 0.15, 0.05, 0.15, 0.05);
                }
            }
        }
    }

    @SubscribeEvent
    public static void registerTooltips(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();

        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag();

            if (tag.contains(ModDataComponents.SENDER)) {
                event.getToolTip().add(
                        Component.translatable("tooltip.faunaandorchestra.sender").append(tag.getString(ModDataComponents.SENDER))
                                .withStyle(ChatFormatting.GRAY));
            }

            if (tag.contains(ModDataComponents.RECEIVER)) {
                event.getToolTip().add(
                        Component.translatable("tooltip.faunaandorchestra.receiver").append(tag.getString(ModDataComponents.RECEIVER))
                                .withStyle(ChatFormatting.GRAY));
            }

            if (tag.contains(ModDataComponents.POSITION)) {
                int[] posArray = tag.getIntArray(ModDataComponents.POSITION);

                if (posArray.length == 3) {
                    event.getToolTip().add(
                            Component.translatable("tooltip.faunaandorchestra.mailbox").append(posArray[0] + " " + posArray[1] + " " + posArray[2])
                                    .withStyle(ChatFormatting.GRAY)
                    );
                }
            }
        }
    }
}

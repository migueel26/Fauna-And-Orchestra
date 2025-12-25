package net.migueel26.faunaandorchestra.item.custom;

import net.migueel26.faunaandorchestra.mixins.client.accessors.ClientLevelAccessor;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.ForgeMod;

import java.util.List;
import java.util.UUID;

public class WhistleItem extends Item {
    public WhistleItem(Properties properties) {
        super(properties);
    }

    public void setMusicianUUID(ItemStack stack, UUID uuid) {
        stack.getOrCreateTag().putUUID("MusicianUUID", uuid);
    }

    public static UUID getMusicianUUID(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains("MusicianUUID")) {
            return stack.getTag().getUUID("MusicianUUID");
        }
        return null;
    }

    public void setMusicianCustomName(ItemStack stack, String name) {
        stack.getOrCreateTag().putString("MusicianCustomName", name);
    }

    public static String getMusicianCustomName(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains("MusicianCustomName")) {
            return stack.getTag().getString("MusicianCustomName");
        }
        return null;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack whistle = player.getItemInHand(usedHand);
        HitResult hitResult = calculateHitResult(player);
        if (hitResult instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() instanceof TamableAnimal animal && animal.isTame()) {
            setMusicianUUID(whistle, animal.getUUID());
            setMusicianCustomName(whistle, animal.hasCustomName() ? animal.getCustomName().getString() : animal.getName().getString());
            level.playSound(player, player.blockPosition(), ModSounds.SUCCESSFUL_TAME.get(), SoundSource.NEUTRAL, 1.0f, 1.0f);
            return InteractionResultHolder.consume(whistle);
        }
        return super.use(level, player, usedHand);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack whistle = context.getItemInHand();
        Player player = context.getPlayer();
        HitResult hitResult = calculateHitResult(player);
        Level level = context.getLevel();
        if (hitResult instanceof BlockHitResult blockHitResult) {
            UUID uuid = getMusicianUUID(whistle);
            if (uuid != null && player != null) {
                if (!level.isClientSide()) {
                    Entity entity = ((ServerLevel) level).getEntity(uuid);
                    if (entity != null && entity.isAlive()) {
                        entity.moveTo(blockHitResult.getBlockPos().above().getCenter());
                        whistle.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(context.getHand()));
                        player.getCooldowns().addCooldown(whistle.getItem(), 400);
                    } else {
                        player.displayClientMessage(Component.translatable("item.faunaandorchestra.whistle.far_message"), true);
                    }
                } else {
                    Entity entity = ((ClientLevelAccessor) level).callGetEntities().get(uuid);
                    if (entity != null && entity.isAlive()) {
                        entity.moveTo(blockHitResult.getBlockPos().above().getCenter());
                    }
                }
            }

            level.playSound(player, player.blockPosition(), ModSounds.WHISTLE_CALL.get(), SoundSource.NEUTRAL, 1.0f, 1.0f);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        UUID uuid = getMusicianUUID(stack);
        if (Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("item.faunaandorchestra.whistle.tooltip"));
        } else {
            tooltipComponents.add(Component.translatable("tooltip.faunaandorchestra.shift"));
        }

        if (uuid != null) {
            tooltipComponents.add(Component.translatable("item.faunaandorchestra.whistle.desc").append(getMusicianCustomName(stack))
                    .withStyle(ChatFormatting.GRAY));
        }
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }

    private HitResult calculateHitResult(Player player) {
        return ProjectileUtil.getHitResultOnViewVector(
                player, entity -> !entity.isSpectator() && entity.isPickable(), player.getAttributeValue(ForgeMod.ENTITY_REACH.get())
        );
    }
}

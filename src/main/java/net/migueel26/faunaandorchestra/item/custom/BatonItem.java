package net.migueel26.faunaandorchestra.item.custom;

import net.migueel26.faunaandorchestra.component.ModDataComponents;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.UUID;

public class BatonItem extends Item {

    public BatonItem(Properties properties) {
        super(properties);
    }

    public static void setMusicianUUID(ItemStack stack, UUID uuid) {
        if (uuid == null) {
            if (stack.hasTag()) {
                stack.getTag().remove(ModDataComponents.MUSICIAN_UUID);
            }
        } else {
            stack.getOrCreateTag().putUUID(ModDataComponents.MUSICIAN_UUID, uuid);
        }
    }

    public static UUID getMusicianUUID(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains(ModDataComponents.MUSICIAN_UUID)) {
            return stack.getTag().getUUID(ModDataComponents.MUSICIAN_UUID);
        }
        return null;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        // TODO: SHINING SQUARE? CUSTOM PARTICLE AND SOUND
        ItemStack stack = context.getItemInHand();
        UUID uuid = getMusicianUUID(stack);
        if (!context.getLevel().isClientSide() && uuid != null) {
            ServerLevel level = (ServerLevel) context.getLevel();
            BlockPos block = context.getClickedPos();
            Mob mob = (Mob) level.getEntity(uuid);
            setMusicianUUID(stack, null);
            if (mob != null && mob.distanceToSqr(block.getCenter()) < 150) {
                mob.getNavigation().moveTo(block.getX(), block.getY(), block.getZ(), 1F);
                level.addParticle(ParticleTypes.NOTE, block.getX(), block.getY() + 2.5, block.getZ(), 0F, 0.5F, 0F);
                return InteractionResult.SUCCESS;
            } else {
                return InteractionResult.PASS;
            }
        } else {
            return  InteractionResult.PASS;
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (Screen.hasShiftDown()) {
            if (stack.is(ModItems.LEGENDARY_BATON.get())) {
                tooltipComponents.add(Component.translatable("tooltip.faunaandorchestra:legendary_baton"));
            } else {
                tooltipComponents.add(Component.translatable("tooltip.faunaandorchestra:baton"));
            }
        } else {
            tooltipComponents.add(Component.translatable("tooltip.faunaandorchestra.shift"));
        }
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }
}

package net.migueel26.faunaandorchestra.item.custom;

import net.migueel26.faunaandorchestra.component.ModDataComponents;
import net.migueel26.faunaandorchestra.entity.custom.koala_workers.TailorKoalaEntity;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;

import java.util.List;
import java.util.UUID;

public class BatonItem extends Item {
    public BatonItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        // TODO: SHINING SQUARE? CUSTOM PARTICLE AND SOUND
        UUID uuid = context.getItemInHand().get(ModDataComponents.MUSICIAN_UUID);
        if (!context.getLevel().isClientSide() && uuid != null) {
            ServerLevel level = (ServerLevel) context.getLevel();
            BlockPos block = context.getClickedPos();
            Mob mob = (Mob) level.getEntity(uuid);
            context.getItemInHand().set(ModDataComponents.MUSICIAN_UUID, null);
            if (mob != null && !(mob instanceof TailorKoalaEntity) && mob.distanceToSqr(block.getCenter()) < 150) {
                mob.getNavigation().moveTo(block.getX(), block.getY(), block.getZ(), 1F);
                return InteractionResult.SUCCESS;
            } else {
                return InteractionResult.PASS;
            }
        } else {
            return  InteractionResult.PASS;
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (Screen.hasShiftDown()) {
            if (stack.is(ModItems.LEGENDARY_BATON)) {
                tooltipComponents.add(Component.translatable("tooltip.faunaandorchestra:legendary_baton"));
            } else {
                tooltipComponents.add(Component.translatable("tooltip.faunaandorchestra:baton"));
            }
        } else {
            tooltipComponents.add(Component.translatable("tooltip.faunaandorchestra.shift"));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}

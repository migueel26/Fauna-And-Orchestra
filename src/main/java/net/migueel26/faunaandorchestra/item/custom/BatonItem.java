package net.migueel26.faunaandorchestra.item.custom;

import net.migueel26.faunaandorchestra.component.ModDataComponents;
import net.migueel26.faunaandorchestra.entity.custom.koala_workers.AbstractKoalaWorker;
import net.migueel26.faunaandorchestra.entity.custom.koala_workers.MelomancerKoalaEntity;
import net.migueel26.faunaandorchestra.entity.custom.koala_workers.TailorKoalaEntity;
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
import net.neoforged.neoforge.common.Tags;

import java.util.List;
import java.util.UUID;

public class BatonItem extends Item {
    public BatonItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        UUID uuid = stack.get(ModDataComponents.MUSICIAN_UUID);

        if (uuid != null) {
            Level level = context.getLevel();
            BlockPos block = context.getClickedPos();

            if (!level.isClientSide()) {
                ServerLevel serverLevel = (ServerLevel) level;
                Mob mob = (Mob) serverLevel.getEntity(uuid);

                stack.set(ModDataComponents.MUSICIAN_UUID, null);

                if (mob instanceof MelomancerKoalaEntity melomancer && level.getBlockState(block).is(Tags.Blocks.CHESTS)) {
                    melomancer.setWorkingStation(block);
                    ((ServerLevel) level).sendParticles(ParticleTypes.WAX_OFF, block.getCenter().x(), block.getY() + 0.5f, block.getCenter().z(), 20, 0.2, 0.2, 0.2, 0.05);
                } else if (mob != null && !(mob instanceof AbstractKoalaWorker) && mob.distanceToSqr(block.getCenter()) < 150) {
                    mob.getNavigation().moveTo(block.getX(), block.getY(), block.getZ(), 1F);
                    ((ServerLevel) level).sendParticles(ParticleTypes.WAX_OFF, block.getCenter().x(), block.getY() + 0.5f, block.getCenter().z(), 20, 0.2, 0.2, 0.2, 0.05);
                }
            }

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
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

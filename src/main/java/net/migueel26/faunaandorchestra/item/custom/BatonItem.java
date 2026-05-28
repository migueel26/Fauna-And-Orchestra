package net.migueel26.faunaandorchestra.item.custom;

import net.migueel26.faunaandorchestra.block.custom.SewingMachineBlock;
import net.migueel26.faunaandorchestra.component.ModDataComponents;
import net.migueel26.faunaandorchestra.entity.custom.MusicalEntity;
import net.migueel26.faunaandorchestra.entity.custom.QuirkyFrogEntity;
import net.migueel26.faunaandorchestra.entity.custom.koala_workers.AbstractKoalaWorker;
import net.migueel26.faunaandorchestra.entity.custom.koala_workers.FarmerKoalaEntity;
import net.migueel26.faunaandorchestra.entity.custom.koala_workers.MelomancerKoalaEntity;
import net.migueel26.faunaandorchestra.entity.custom.koala_workers.TailorKoalaEntity;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
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
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand hand) {
        if (interactionTarget instanceof MusicalEntity mob && !player.level().isClientSide()) {
            stack.set(ModDataComponents.MUSICIAN_UUID, mob.getUUID());

            player.displayClientMessage(mob.getName(), true);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
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

                if (mob instanceof TailorKoalaEntity worker && worker.isWorkingStation(level.getBlockState(block))) {
                    mob.stopInPlace();
                    return InteractionResult.PASS;
                }

                stack.set(ModDataComponents.MUSICIAN_UUID, null);

                if ((mob instanceof MelomancerKoalaEntity || mob instanceof FarmerKoalaEntity) && level.getBlockState(block).is(Tags.Blocks.CHESTS)) {
                    mob.stopInPlace();
                    ((AbstractKoalaWorker) mob).setWorkingStation(block);
                    serverLevel.sendParticles(ParticleTypes.WAX_OFF, block.getCenter().x(), block.getY() + 0.5f, block.getCenter().z(), 20, 0.2, 0.2, 0.2, 0.05);
                } else if (mob instanceof MusicalEntity && mob.distanceToSqr(block.getCenter()) < 150) {
                    mob.getNavigation().moveTo(block.getX(), block.getY(), block.getZ(), 1F);
                    serverLevel.sendParticles(ParticleTypes.WAX_OFF, block.getCenter().x(), block.getY() + 0.5f, block.getCenter().z(), 20, 0.2, 0.2, 0.2, 0.05);
                } else {
                    return InteractionResult.PASS;
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

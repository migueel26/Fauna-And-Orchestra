package net.migueel26.faunaandorchestra.item.custom;

import net.migueel26.faunaandorchestra.block.ModBlocks;
import net.migueel26.faunaandorchestra.block.custom.FlowerGrowerDiscordBlock;
import net.migueel26.faunaandorchestra.particles.ModParticleTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.List;

public class ActivatorClefItem extends Item {
    public ActivatorClefItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        ItemStack stack = context.getItemInHand();
        if (FlowerGrowerDiscordBlock.isNotProhibited(level.getBlockState(pos)) && !level.getBlockState(pos).isAir()) {
            level.setBlock(pos, ModBlocks.FLOWER_DISCORD_BLOCK.get().defaultBlockState(), 3);
            level.playSound(null, pos, SoundEvents.WARDEN_LISTENING, SoundSource.BLOCKS);
            if (!level.isClientSide()) {
                ((ServerLevel) level).sendParticles(ParticleTypes.SCULK_SOUL, pos.getCenter().x, pos.above().getY(), pos.getCenter().z, 20, 0.2, 0.2, 0.2, 0.01);
            }

            if (FlowerGrowerDiscordBlock.isNotProhibited(level.getBlockState(pos.above())) && level.getBlockState(pos.above()).isAir() &&
                    level.getRandom().nextFloat() <= 0.25 && !level.isClientSide()) {
                level.setBlock(pos.above(), ModBlocks.DISCORD_NUCLEI.get().defaultBlockState(), 3);
                level.playSound(null, pos, SoundEvents.WARDEN_SONIC_CHARGE, SoundSource.BLOCKS);
                ((ServerLevel) level).sendParticles(ParticleTypes.SOUL_FIRE_FLAME, pos.above().getCenter().x, pos.above().above().getY(), pos.above().getCenter().z, 20, 0.2, 0.2, 0.2, 0.01);
                ((ServerLevel) level).sendParticles(ModParticleTypes.BASS_CLEF.get(), pos.above().getCenter().x, pos.above().above().getY(), pos.above().getCenter().z, 1, 0, 0, 0, 0);
            }

            stack.consume(1, context.getPlayer());

            return InteractionResult.SUCCESS;
        }
        return super.useOn(context);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("item.faunaandorchestra.activator_clef.desc")
                .withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}

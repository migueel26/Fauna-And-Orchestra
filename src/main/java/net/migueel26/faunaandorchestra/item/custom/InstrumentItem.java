package net.migueel26.faunaandorchestra.item.custom;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

import java.util.List;


public class InstrumentItem extends Item {
    private SoundEvent sound;
    public InstrumentItem(Properties properties, SoundEvent sound) {
        super(properties);
        this.sound = sound;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        //TODO: THE SOUND ONLY GETS PLAYED WHEN THE PLAYER DOESNT CLICK A MUSICAL ENTITY
            if (!level.isClientSide()) {
                level.playSound(
                        null,
                        player.getX(), player.getY(), player.getZ(),
                        sound, SoundSource.NEUTRAL,
                        0.5F,
                        0.4F / (0.5F + level.getRandom().nextFloat())
                );
                player.getCooldowns().addCooldown(this, 20);
            }

        return InteractionResultHolder.success(itemStack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip." + stack.getItem() + ".tooltip"));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    public SoundEvent getSound() {
        return sound;
    }
}

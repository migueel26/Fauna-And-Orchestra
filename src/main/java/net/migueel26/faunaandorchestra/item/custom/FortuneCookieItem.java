package net.migueel26.faunaandorchestra.item.custom;

import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class FortuneCookieItem extends Item {
    public FortuneCookieItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        if (level.isClientSide()) {
            int random = level.random.nextIntBetweenInclusive(0, 18);
            Component phrase = Component.translatable("item.faunaandorchestra.fortune_cookie_phrase" + random)
                    .withStyle(random == 0 ? ChatFormatting.GOLD : ChatFormatting.AQUA);

            livingEntity.sendSystemMessage(phrase);
            if (random == 0) {
                livingEntity.playSound(ModSounds.GONG.get());
                if (livingEntity instanceof Player player) {
                    player.addItem(new ItemStack(ModItems.PROPELLER_HAT.get()));
                }
            } else {
                livingEntity.playSound(SoundEvents.ITEM_PICKUP, 1.5f, 1.25f + level.random.nextFloat()/2);
            }
        }

        return super.finishUsingItem(stack, level, livingEntity);
    }
}

package net.migueel26.faunaandorchestra.item.custom;

import net.migueel26.faunaandorchestra.component.ModDataComponents;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class PanFluteItem extends Item {
    public final List<String> powersString = new ArrayList<>(List.of(
            "notes",
            "push",
            "health",
            "wind",
            "nature"
    ));
    public PanFluteItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack flute = player.getItemInHand(usedHand);
        List<Integer> powers = flute.get(ModDataComponents.PAN_FLUTE_LIST);
        Integer currentSound = flute.get(ModDataComponents.PAN_FLUTE_SOUND);

        if (Screen.hasShiftDown()) {
            if (powers != null && currentSound != null) {
                if (currentSound == 0) currentSound = 1;
                else currentSound = powers.get(currentSound) % powers.size();

                player.displayClientMessage(
                        Component.translatable("item.faunaandorchestra.pan_flute." + powersString.get(currentSound)), true);
            }

            flute.set(ModDataComponents.PAN_FLUTE_SOUND, currentSound);
            level.playSound(player, player.blockPosition(), ModSounds.PAN_FLUTE_CHANGE.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
            player.getCooldowns().addCooldown(ModItems.PAN_FLUTE.get(), 20);

            return InteractionResultHolder.consume(flute);

        } else if (currentSound != null && powers != null) {
            SoundEvent fluteSound = ModSounds.PAN_FLUTE_USE.get();

            // If it doesn't have powers, we just add the default cooldown with default sound
            if (powers.isEmpty()) player.getCooldowns().addCooldown(ModItems.PAN_FLUTE.get(), 60);
            else fluteSound = getFluteSound(player, powers, currentSound, fluteSound);

            level.playSound(player, player.blockPosition(), fluteSound, SoundSource.PLAYERS, 1.0f, 1.0f);

            return InteractionResultHolder.consume(flute);
        }

        return InteractionResultHolder.fail(flute);
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        super.onUseTick(level, livingEntity, stack, remainingUseDuration);
    }

    private static SoundEvent getFluteSound(Player player, List<Integer> powers, Integer currentSound, SoundEvent fluteSound) {
        return switch (powers.get(currentSound)) {
            case 1 -> ModSounds.PAN_FLUTE_NOTES.get();
            case 2 -> ModSounds.PAN_FLUTE_PUSH.get();
            case 3 -> ModSounds.PAN_FLUTE_HEALTH.get();
            case 4 -> ModSounds.PAN_FLUTE_WIND.get();
            case 5 -> ModSounds.PAN_FLUTE_NATURE.get();
            default -> ModSounds.PAN_FLUTE_USE.get();
        };
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        List<Integer> powers = stack.get(ModDataComponents.PAN_FLUTE_LIST);
        Integer sound = stack.get(ModDataComponents.PAN_FLUTE_SOUND);

        if (powers != null && sound != null) {
            for (int i = 0; i < powers.size(); i++) {
                ChatFormatting color = ChatFormatting.GRAY;
                if (sound == i) {
                    color = ChatFormatting.GOLD;
                }

                tooltipComponents.add(
                        Component.translatable("item.faunaandorchestra.pan_flute." + powersString.get(i))
                        .withStyle(color));
            }
        }

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}

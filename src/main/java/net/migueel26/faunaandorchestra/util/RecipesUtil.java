package net.migueel26.faunaandorchestra.util;

import net.migueel26.faunaandorchestra.block.entity.MelomancyCauldronBlockEntity;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.*;

public class RecipesUtil {
    public static final int NUMBER_RECIPES = 1;
    public static HashSet<ItemStack> MUSICAL_INK = new HashSet<>(List.of(
            new ItemStack(Items.FEATHER, 1),
            new ItemStack(Items.GLOW_INK_SAC, 1)
    ));

    public static String isRecipe(NonNullList<ItemStack> ingredients) {
        String result = "discord";
        int i = 1;

        while (result.equalsIgnoreCase("discord") && i <= NUMBER_RECIPES) {
            switch (i) {
                case 1 -> result = sameIngredients(ingredients, MUSICAL_INK) ? "musical_ink" : "discord";
            }

            i++;
        }

        return result;
    }

    public static boolean isCorrectItem(ItemStack stack, MelomancyCauldronBlockEntity cauldronBlock) {
        return stack.is(switch (cauldronBlock.getMixResult()) {
                    case "musical_ink" -> Items.GLASS_BOTTLE;
                    default -> stack.getItem();
                }
        );
    }

    private static boolean sameIngredients(Collection<ItemStack> ingredients, Collection<ItemStack> recipe) {
        ArrayList<ItemStack> copy2 = new ArrayList<>(recipe);

        for (ItemStack item1 : ingredients) {
            boolean found = false;
            for (ItemStack item2 : recipe) {
                if ((ItemStack.isSameItemSameComponents(item1, item2) && item1.getCount() == item2.getCount()) || item1.isEmpty()) {
                    found = true;
                    if (!item1.isEmpty()) copy2.remove(item2);
                    break;
                }
            }
            if (!found) return false;
        }

        return copy2.isEmpty();
    }

    public static ItemStack getMixResult(String mixResult) {
        return switch (mixResult) {
            case "musical_ink" -> new ItemStack(ModItems.MUSICAL_INK.get(), 3);
            default -> new ItemStack(ModItems.DISCORD_ESSENCE.get(), 1);
        };
    }
}

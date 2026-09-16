package net.migueel26.faunaandorchestra.util;

import net.migueel26.faunaandorchestra.component.ModDataComponents;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

public class RecipesUtil {
    public static List<Item> ITEMS_RECIPE = List.of(
            ModItems.FLORAL_BOOTS.get()
    );

    public static ItemStack recipeOfItem(Item item) {
        ItemStack stack = new ItemStack(ModItems.SEWING_RECIPE.get());

        stack.setHoverName(Component.translatable(item.getDescriptionId())
                .append(Component.translatable("item.faunaandorchestra.sewing_recipe")));

        stack.getOrCreateTag().putString(
                ModDataComponents.FAUNA_CUSTOM_NAME,
                ForgeRegistries.ITEMS.getKey(item).toString()
        );

        return stack;
    }

    public static List<ItemStack> getAllRecipeItems() {
        return ITEMS_RECIPE.stream().map(RecipesUtil::recipeOfItem).toList();
    }

    public static List<ItemStack> toList(ItemStackHandler inventory) {
        List<ItemStack> list = new ArrayList<>(inventory.getSlots());

        for (int i = 0; i < inventory.getSlots(); i++) {
            list.add(inventory.getStackInSlot(i));
        }

        return list;
    }

    public static void listToInventory(List<ItemStack> list, ItemStackHandler inventory) {
        int maxItems = Math.min(list.size(), inventory.getSlots());

        for (int i = 0; i < maxItems; i++) {
            inventory.setStackInSlot(i, list.get(i));
        }
    }

    public static void clearContents(ItemStackHandler inventory) {
        for (int i = 0; i < inventory.getSlots(); i++) {
            inventory.setStackInSlot(i, ItemStack.EMPTY);
        }
    }
}

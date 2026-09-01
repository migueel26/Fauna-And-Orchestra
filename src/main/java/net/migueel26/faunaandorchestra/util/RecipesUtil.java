package net.migueel26.faunaandorchestra.util;

import net.migueel26.faunaandorchestra.block.ModBlocks;
import net.migueel26.faunaandorchestra.block.entity.MelomancyCauldronBlockEntity;
import net.migueel26.faunaandorchestra.component.ModDataComponents;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.effect.potion.ModPotions;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.neoforged.neoforge.items.ItemStackHandler;
import oshi.util.tuples.Pair;

import javax.annotation.Nullable;
import java.util.*;

public class RecipesUtil {
    public static List<Item> ITEMS_RECIPE = List.of(
            ModItems.FLORAL_BOOTS.asItem()
    );

    public static ItemStack recipeOfItem(Item item) {
        ItemStack stack = new ItemStack(ModItems.SEWING_RECIPE.get());

        stack.applyComponents(DataComponentPatch.builder()
                .set(ModDataComponents.FAUNA_NAME.get(), BuiltInRegistries.ITEM.getKey(item).toString())
                .set(DataComponents.ITEM_NAME, Component.translatable(item.getDescriptionId())
                        .append(Component.translatable("item.faunaandorchestra.sewing_recipe")))
                .build());

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

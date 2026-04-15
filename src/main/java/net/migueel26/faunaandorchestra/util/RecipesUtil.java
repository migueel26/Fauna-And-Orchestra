package net.migueel26.faunaandorchestra.util;

import net.migueel26.faunaandorchestra.block.ModBlocks;
import net.migueel26.faunaandorchestra.block.entity.MelomancyCauldronBlockEntity;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.effect.potion.ModPotions;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.neoforged.neoforge.items.ItemStackHandler;
import oshi.util.tuples.Pair;

import javax.annotation.Nullable;
import java.util.*;

public class RecipesUtil {
    // DISCORD NUCLEI
    public static Map<Item, Pair<Integer, Item>> DISCORD_NUCLEI = Map.of(
            Items.OXEYE_DAISY, new Pair<>(50, ModBlocks.DISCORDED_FLOWER.asItem()),
            ModItems.BOOGIE_FRUIT.get(), new Pair<>(30, ModItems.DISCORD_BOMB.asItem()),
            ModItems.VOICE.get(), new Pair<>(30, ModItems.TRANSMUTED_VOICE.asItem())
    );

    // ( BASE UNSTABILITY, EXTRA PROPORTION )
    public static Map<Item, Pair<Integer, Float>> UNSTABILITY_INDEXES = Map.of(
            Items.OXEYE_DAISY, new Pair<>(15, 3.0f),
            ModItems.BOOGIE_FRUIT.get(), new Pair<>(10, 3.5f),
            ModItems.VOICE.get(), new Pair<>(10, 3.5f)
    );

    public static boolean isDiscordNucleiIngredient(ItemStack stack) {
        return DISCORD_NUCLEI.containsKey(stack.getItem());
    }

    public static Pair<Integer, Item> getDiscordNucleiResult(ItemStack stack) {
        return DISCORD_NUCLEI.get(stack.getItem());
    }

    public static Pair<Integer, Float> getDiscordNucleiIndexes(ItemStack stack) {
        return UNSTABILITY_INDEXES.get(stack.getItem());
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

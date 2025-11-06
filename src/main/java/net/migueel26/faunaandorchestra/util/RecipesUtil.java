package net.migueel26.faunaandorchestra.util;

import net.migueel26.faunaandorchestra.block.ModBlocks;
import net.migueel26.faunaandorchestra.block.entity.MelomancyCauldronBlockEntity;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.potion.ModPotions;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import oshi.util.tuples.Pair;

import java.util.*;

public class RecipesUtil {
    public static final int NUMBER_RECIPES = 6;
    // MELOMANCY CAULDRON
    public static HashSet<ItemStack> MUSICAL_INK = new HashSet<>(List.of(
            new ItemStack(Items.FEATHER, 1),
            new ItemStack(Items.GLOW_INK_SAC, 1)
    ));

    public static HashSet<ItemStack> OFFERING = new HashSet<>(List.of(
            new ItemStack(Items.ALLIUM, 3),
            new ItemStack(Items.GLOW_BERRIES, 5),
            new ItemStack(ModItems.GINKGO_BILOBA.get(), 5),
            new ItemStack(Items.SWEET_BERRIES, 10)
    ));

    public static HashSet<ItemStack> ABSOLUTE_HEARING_POTION = new HashSet<>(List.of(
            new ItemStack(Items.BLAZE_POWDER, 1),
            new ItemStack(Items.NETHER_WART, 1),
            new ItemStack(ModItems.GINKGO_BILOBA.get(), 3)
    ));

    public static HashSet<ItemStack> STEELSONIC = new HashSet<>(List.of(
            new ItemStack(Items.IRON_INGOT, 5),
            new ItemStack(Items.LAVA_BUCKET, 1),
            new ItemStack(ModItems.WANDERING_NOTE.get(), 3)
    ));

    public static HashSet<ItemStack> SINGING_SEED = new HashSet<>(List.of(
            new ItemStack(Items.BEETROOT_SEEDS, 3),
            new ItemStack(Items.HONEYCOMB, 2),
            new ItemStack(Items.CARROT, 5),
            new ItemStack(ModItems.WANDERING_NOTE.get(), 3)
    ));

    public static HashSet<ItemStack> BOOGIE_BOMB = new HashSet<>(List.of(
            new ItemStack(Items.GUNPOWDER, 3),
            new ItemStack(Items.SAND, 2)
    ));

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

    public static String isRecipe(NonNullList<ItemStack> ingredients) {
        String result = "discord";
        int i = 1;

        while (result.equalsIgnoreCase("discord") && i <= NUMBER_RECIPES) {
            switch (i) {
                case 1 -> result = sameIngredients(ingredients, MUSICAL_INK) ? "musical_ink" : "discord";
                case 2 -> result = sameIngredients(ingredients, OFFERING) ? "offering" : "discord";
                case 3 -> result = sameIngredients(ingredients, ABSOLUTE_HEARING_POTION) ? "absolute_hearing" : "discord";
                case 4 -> result = sameIngredients(ingredients, STEELSONIC) ? "steelsonic" : "discord";
                case 5 -> result = sameIngredients(ingredients, SINGING_SEED) ? "singing_seed" : "discord";
                case 6 -> result = sameIngredients(ingredients, BOOGIE_BOMB) ? "boogie_bomb" : "discord";
            }

            i++;
        }

        if (result.equalsIgnoreCase("discord")) {
            result = "discord:" + (ingredients.stream().map(ItemStack::getCount).mapToInt(Integer::intValue).sum() / 2);
        }

        return result;
    }

    public static boolean isCorrectItem(ItemStack stack, MelomancyCauldronBlockEntity cauldronBlock) {
        return stack.is(switch (cauldronBlock.getMixResult()) {
                    case "musical_ink", "absolute_hearing" -> Items.GLASS_BOTTLE;
                    case "offering" -> Items.STRING;
                    case "singing_seed" -> Items.WHEAT_SEEDS;
                    case "boogie_bomb" -> ModItems.BOOGIE_FRUIT.get();
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
            case "offering" -> new ItemStack(ModItems.OFFERING.get());
            case "absolute_hearing" -> PotionContents.createItemStack(Items.POTION, ModPotions.ABSOLUTE_HEARING_POTION);
            case "steelsonic" -> new ItemStack(ModItems.STEELSONIC_INGOT.get(), 2);
            case "singing_seed" -> new ItemStack(ModItems.SINGING_SEED.get(), 1);
            case "boogie_bomb" -> new ItemStack(ModItems.BOOGIE_BOMB.get(), 1);
            default -> new ItemStack(ModItems.DISCORD_ESSENCE.get(), Integer.parseInt(mixResult.split(":")[1]));
        };
    }

    public static boolean isDiscordNucleiIngredient(ItemStack stack) {
        return DISCORD_NUCLEI.containsKey(stack.getItem());
    }

    public static Pair<Integer, Item> getDiscordNucleiResult(ItemStack stack) {
        return DISCORD_NUCLEI.get(stack.getItem());
    }

    public static Pair<Integer, Float> getDiscordNucleiIndexes(ItemStack stack) {
        return UNSTABILITY_INDEXES.get(stack.getItem());
    }
}

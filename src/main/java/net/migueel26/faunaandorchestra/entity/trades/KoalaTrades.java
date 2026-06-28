package net.migueel26.faunaandorchestra.entity.trades;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.migueel26.faunaandorchestra.block.ModBlocks;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.Optional;

public class KoalaTrades {
    public static final Int2ObjectMap<VillagerTrades.ItemListing[]> WANDERING_KOALA_TRADES = toIntMap(
            ImmutableMap.of(
                    1,
                    new VillagerTrades.ItemListing[]{
                            new ItemsForEmeralds(ModItems.BACH_AIR_SHEET_MUSIC.get(), 5, 1, 1, 1),
                            new ItemsForEmeralds(ModItems.BLUES_SHEET_MUSIC.get(), 5, 1, 1, 1),
                            new ItemsForEmeralds(ModItems.GREENSLEEVES_SHEET_MUSIC.get(), 5, 1, 1,1),
                            new ItemsForEmeralds(ModItems.JAZZY_FUR_ELISE_SHEET_MUSIC.get(), 5, 1, 1, 1),
                            new ItemsForEmeralds(ModItems.DANCE_OF_THE_LITTLE_SWANS.get(), 5, 1, 1, 1),
                            new ItemsForEmeralds(ModItems.LA_BAMBA_SHEET_MUSIC.get(), 5, 1, 1, 1),
                            new ItemsForEmeralds(ModItems.SAINTS_SHEET_MUSIC.get(), 5, 1, 1, 1),
                            new ItemsForEmeralds(ModItems.OH_SUSANNA_SHEET_MUSIC.get(), 5, 1, 1, 1),
                            new ItemsForEmeralds(ModItems.THE_ENTERTAINER_SHEET_MUSIC.get(), 5, 1, 1, 1),
                    },
                    2,
                    new VillagerTrades.ItemListing[]{
                        new ItemsForEmeralds(ModItems.BATON.get(), 3, 1, 3, 1),
                        new ItemsForEmeralds(ModItems.FLUTE.get(), 3, 1, 3, 1),
                        new ItemsForEmeralds(ModItems.DOUBLE_BASS.get(), 3, 1, 3, 1),
                        new ItemsForEmeralds(ModItems.SAXOPHONE.get(), 3, 1, 3, 1),
                        new ItemsForEmeralds(ModItems.VIOLIN.get(), 3, 1, 3, 1),
                        new ItemsForEmeralds(ModItems.KEYTAR.get(), 3, 1, 3, 1),
                        new ItemsForEmeralds(ModItems.OBOE.get(), 3, 1, 3, 1),
                        new ItemsForEmeralds(ModItems.CELLO.get(), 3, 1, 3, 1)
                    },
                    3,
                    new VillagerTrades.ItemListing[]{
                        new ItemsForEmeralds(ModItems.PAN_FLUTE.get(), 5, 1, 1, 1)
                    },
                    4,
                    new VillagerTrades.ItemListing[]{
                        new ItemsForEmeralds(ModBlocks.GINGKO_BILOBA_SAPLING.get(), 5, 1, 5, 1)
                    },
                    5,
                    new VillagerTrades.ItemListing[]{
                            new ItemsForEmeralds(ModItems.GLOVE.get().getDefaultInstance(), 3, 1, 5, 1, 0.05f, ModItems.BOOGIE_FRUIT.get())
                    },
                    6,
                    new VillagerTrades.ItemListing[]{
                            new ItemsForEmeralds(ModItems.BUSINESS_CARD.get().getDefaultInstance(), 1, 1, 1, 1, 1, ModItems.MUSIC_JAM.get()),
                    }
            )
    );

    public static final Int2ObjectMap<VillagerTrades.ItemListing[]> BUTLER_KOALA_TRADES = toIntMap(
            ImmutableMap.of(
                    1,
                    new VillagerTrades.ItemListing[]{
                            new ItemsForEmeralds(Items.BREAD.getDefaultInstance(), 3, 2, 5, 1, 0.05f, Items.WHEAT_SEEDS),
                            new ItemsForEmeralds(Items.CAKE.getDefaultInstance(), 5, 1, 5, 1, 0.05f, Items.BEEF),
                            new ItemsForEmeralds(Items.RABBIT_STEW.getDefaultInstance(), 1, 8, 5, 1, 0.05f, Items.BEETROOT),
                            new ItemsForEmeralds(Items.ENCHANTED_GOLDEN_APPLE.getDefaultInstance(), 10, 1, 1, 1, 0.05f, ModItems.BOOGIE_FRUIT.get()),
                            new ItemsForEmeralds(Items.HONEY_BOTTLE.getDefaultInstance(), 3, 1, 10, 5, 0.05f),
                            new ItemsForEmeralds(Items.GLOW_BERRIES.getDefaultInstance(), 2, 4, 10, 5, 0.05f, Items.SWEET_BERRIES),
                            new ItemsForEmeralds(Items.SUSPICIOUS_STEW.getDefaultInstance(), 4, 1, 8, 5, 0.05f, Items.BROWN_MUSHROOM),
                            new ItemsForEmeralds(Items.GOLDEN_CARROT.getDefaultInstance(), 3, 3, 12, 10, 0.05f),
                            new ItemsForEmeralds(Items.GOLDEN_APPLE.getDefaultInstance(), 5, 1, 12, 10, 0.05f),
                            new ItemsForEmeralds(Items.COOKIE.getDefaultInstance(), 1, 12, 20, 10, 0.05f)
                    },
                    2,
                    new VillagerTrades.ItemListing[]{
                            new ItemsForEmeralds(ModItems.FORTUNE_COOKIE.get().getDefaultInstance(), 3, 1, 8, 5, 1),
                    },
                    3,
                    new VillagerTrades.ItemListing[]{
                            new ItemsForEmeralds(ModItems.BUSINESS_CARD.get().getDefaultInstance(), 1, 1, 1, 1, 1, ModItems.MUSIC_JAM.get()),
                    }
            )
    );

    private static Int2ObjectMap<VillagerTrades.ItemListing[]> toIntMap(ImmutableMap<Integer, VillagerTrades.ItemListing[]> map) {
        return new Int2ObjectOpenHashMap<>(map);
    }

    static class ItemsForEmeralds implements VillagerTrades.ItemListing {
        private Item coin = Items.EMERALD; // Moneda por defecto
        private final ItemStack itemStack;
        private final int emeraldCost;
        private final int maxUses;
        private final int villagerXp;
        private final float priceMultiplier;

        public ItemsForEmeralds(Block block, int emeraldCost, int numberOfItems, int maxUses, int villagerXp) {
            this(new ItemStack(block), emeraldCost, numberOfItems, maxUses, villagerXp);
        }

        public ItemsForEmeralds(Item item, int emeraldCost, int numberOfItems, int villagerXp) {
            this(new ItemStack(item), emeraldCost, numberOfItems, 12, villagerXp);
        }

        public ItemsForEmeralds(Item item, int emeraldCost, int numberOfItems, int maxUses, int villagerXp) {
            this(new ItemStack(item), emeraldCost, numberOfItems, maxUses, villagerXp);
        }

        public ItemsForEmeralds(ItemStack itemStack, int emeraldCost, int numberOfItems, int maxUses, int villagerXp) {
            this(itemStack, emeraldCost, numberOfItems, maxUses, villagerXp, 0.05F);
        }

        public ItemsForEmeralds(Item item, int emeraldCost, int numberOfItems, int maxUses, int villagerXp, float priceMultiplier) {
            this(new ItemStack(item), emeraldCost, numberOfItems, maxUses, villagerXp, priceMultiplier);
        }

        public ItemsForEmeralds(
                ItemStack itemStack,
                int emeraldCost,
                int numberOfItems,
                int maxUses,
                int villagerXp,
                float priceMultiplier
        ) {
            this.itemStack = itemStack;
            this.emeraldCost = emeraldCost;
            this.itemStack.setCount(numberOfItems);
            this.maxUses = maxUses;
            this.villagerXp = villagerXp;
            this.priceMultiplier = priceMultiplier;
        }

        public ItemsForEmeralds(
                ItemStack itemStack,
                int emeraldCost,
                int numberOfItems,
                int maxUses,
                int villagerXp,
                float priceMultiplier,
                Item coin
        ) {
            this.itemStack = itemStack;
            this.emeraldCost = emeraldCost;
            this.itemStack.setCount(numberOfItems);
            this.maxUses = maxUses;
            this.villagerXp = villagerXp;
            this.priceMultiplier = priceMultiplier;
            this.coin = coin;
        }

        @Override
        public MerchantOffer getOffer(Entity trader, RandomSource random) {
            return new MerchantOffer(
                    new ItemStack(this.coin, this.emeraldCost),
                    this.itemStack.copy(),
                    this.maxUses,
                    this.villagerXp,
                    this.priceMultiplier
            );
        }
    }
}
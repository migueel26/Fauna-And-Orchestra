package net.migueel26.faunaandorchestra.util;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static class Items {
        public static final TagKey<Item> SHEET_MUSIC = registerTag("sheet_music");
        public static final TagKey<Item> IS_BATON = registerTag("is_baton");

        private static TagKey<Item> registerTag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, name));
        }
    }

    public static class Blocks {
        public static final TagKey<Block> REPLACEABLE_BY_DISCORD = registerTag("replaceable_by_discord");
        private static TagKey<Block> registerTag(String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, name));
        }
    }
}

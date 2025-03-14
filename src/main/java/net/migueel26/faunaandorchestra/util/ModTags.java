package net.migueel26.faunaandorchestra.util;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModTags {
    public static class Items {
        public static final TagKey<Item> SHEET_MUSIC = registerTag("sheet_music");

        private static TagKey<Item> registerTag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, name));
        }
    }
}

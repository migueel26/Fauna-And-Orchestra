package net.migueel26.faunaandorchestra.util;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static class Items {
        public static final TagKey<Item> SHEET_MUSIC = registerTag("sheet_music");
        public static final TagKey<Item> IS_BATON = registerTag("is_baton");
        public static final TagKey<Item> IS_INSTRUMENT = registerTag("is_instrument");
        public static final TagKey<Item> IS_HAT = registerTag("is_hat");
        public static final TagKey<Item> IS_COSTUME = registerTag("is_costume");
        public static final TagKey<Item> IS_LEGENDARY_CLOTHING = registerTag("is_legendary_clothing");
        public static final TagKey<Item> BUGS = registerTag("bugs");

        private static TagKey<Item> registerTag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, name));
        }
    }

    public static class Blocks {
        public static final TagKey<Block> REPLACEABLE_BY_DISCORD = registerTag("replaceable_by_discord");
        public static final TagKey<Block> JAR_FUEL = registerTag("jar_fuel");
        private static TagKey<Block> registerTag(String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, name));
        }
    }

    public static class Biomes {
        public static final TagKey<Biome> SPAWNS_PENGUIN = registerTag("spawns_penguin");
        public static final TagKey<Biome> SPAWNS_BEAVER = registerTag("spawns_beaver");
        public static final TagKey<Biome> SPAWNS_MANTIS = registerTag("spawns_mantis");
        public static final TagKey<Biome> SPAWNS_MACAW = registerTag("spawns_macaw");
        public static final TagKey<Biome> SPAWNS_RED_PANDA = registerTag("spawns_red_panda");
        public static final TagKey<Biome> SPAWNS_BUTTERFLY = registerTag("spawns_butterfly");
        public static final TagKey<Biome> SPAWNS_LEMUR = registerTag("spawns_lemur");
        public static final TagKey<Biome> SPAWNS_QUIRKY_FROG = registerTag("spawns_quirky_frog");

        private static TagKey<Biome> registerTag(String name) {
            return TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, name));
        }
    }

    public static class EntityTypes {
        // CLOTHING
        public static final TagKey<EntityType<?>> WEARS_TUXEDO = registerTag("wears_tuxedo");
        public static final TagKey<EntityType<?>> WEARS_TAILCOAT = registerTag("wears_tailcoat");
        public static final TagKey<EntityType<?>> WEARS_SANTA_COSTUME = registerTag("wears_santa_costume");
        public static final TagKey<EntityType<?>> WEARS_BASEBALL_JACKET = registerTag("wears_baseball_jacket");
        // HEADWEAR
        public static final TagKey<EntityType<?>> WEARS_RIGHT_MONOCLE = registerTag("wears_right_monocle");
        public static final TagKey<EntityType<?>> WEARS_LEFT_MONOCLE = registerTag("wears_left_monocle");
        public static final TagKey<EntityType<?>> WEARS_FAKE_MOUSTACHE = registerTag("wears_fake_moustache");
        public static final TagKey<EntityType<?>> WEARS_ROSE = registerTag("wears_rose");
        public static final TagKey<EntityType<?>> WEARS_IMAGINAL_DISK = registerTag("wears_imaginal_disk");
        public static final TagKey<EntityType<?>> WEARS_MASK_OF_THE_ENLIGHTENED = registerTag("wears_mask_of_the_enlightened");
        public static final TagKey<EntityType<?>> WEARS_PROPELLER_HAT = registerTag("wears_propeller_hat");
        public static final TagKey<EntityType<?>> WEARS_TOP_HAT = registerTag("wears_top_hat");
        public static final TagKey<EntityType<?>> WEARS_SANTA_HAT = registerTag("wears_santa_hat");
        public static final TagKey<EntityType<?>> WEARS_BASEBALL_CAP = registerTag("wears_baseball_cap");
        // TINTS
        public static final TagKey<EntityType<?>> WEARS_SILVER_TINT = registerTag("wears_silver_tint");
        public static final TagKey<EntityType<?>> WEARS_GOLDEN_TINT = registerTag("wears_golden_tint");

        private static TagKey<EntityType<?>> registerTag(String name) {
            return TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, name));
        }
    }
}

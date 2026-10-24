package net.migueel26.faunaandorchestra;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
@EventBusSubscriber(modid = FaunaAndOrchestra.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.BooleanValue ANYA_SPAWN = BUILDER
            .comment("Should the mysterious character appear at the beginning?")
            .define("character_spawn", true);
    private static final ModConfigSpec.BooleanValue GIVE_BOOK = BUILDER
            .comment("Should the book be dropped at the beginning?")
            .define("give_book", true);
    private static final ModConfigSpec.BooleanValue HALLOWEEN_PUMPKINS = BUILDER
            .comment("Should the Halloween pumpkins be enabled?")
            .define("halloween_hats", true);

    public record AnimalSpawn(ModConfigSpec.IntValue weight, ModConfigSpec.IntValue minCount, ModConfigSpec.IntValue maxCount,
                              ModConfigSpec.ConfigValue<List<? extends String>> addBiomes) {}

    // Keyed by entity id path (e.g. "lemur")
    private static final Map<String, AnimalSpawn> ANIMAL_SPAWNS = new LinkedHashMap<>();

    static {
        BUILDER.comment("Animal spawn settings. Changes require restarting the world/server.",
                        "Set weight to 0 to stop an animal from spawning naturally.",
                        "Use add_biomes to let an animal spawn in extra biomes.")
                .push("animal_spawns");
        defineAnimalSpawn("beaver", 12, 3, 4);
        defineAnimalSpawn("butterfly", 8, 3, 6);
        defineAnimalSpawn("emperor_penguin", 8, 2, 3);
        defineAnimalSpawn("lemur", 8, 3, 4);
        defineAnimalSpawn("macaw", 15, 4, 6);
        defineAnimalSpawn("mantis", 14, 2, 4);
        defineAnimalSpawn("quirky_frog", 20, 5, 6);
        defineAnimalSpawn("red_panda", 14, 3, 4);
        defineAnimalSpawn("sea_lion", 8, 3, 4);
        BUILDER.pop();
    }

    static final ModConfigSpec SPEC = BUILDER.build();
    public static boolean anyaSpawn;
    public static boolean giveBook;
    public static boolean halloweenPumpkins;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        anyaSpawn = ANYA_SPAWN.get();
        giveBook = GIVE_BOOK.get();
        halloweenPumpkins = HALLOWEEN_PUMPKINS.get();
    }

    private static void defineAnimalSpawn(String name, int weight, int minCount, int maxCount) {
        BUILDER.push(name);
        ModConfigSpec.IntValue weightValue = BUILDER
                .comment("Spawn weight (higher = more common)")
                .defineInRange("weight", weight, 0, 1000);
        ModConfigSpec.IntValue minValue = BUILDER
                .comment("Minimum group size")
                .defineInRange("min_count", minCount, 1, 64);
        ModConfigSpec.IntValue maxValue = BUILDER
                .comment("Maximum group size")
                .defineInRange("max_count", maxCount, 1, 64);
        ModConfigSpec.ConfigValue<List<? extends String>> addBiomesValue = BUILDER
                .comment("Extra biomes this animal can spawn in, on top of its default ones.",
                        "Accepts biome ids (\"minecraft:plains\") and biome tags (\"#minecraft:is_forest\").")
                .defineList("add_biomes", List.of(), () -> "minecraft:plains", Config::isBiomeEntry);
        BUILDER.pop();
        ANIMAL_SPAWNS.put(name, new AnimalSpawn(weightValue, minValue, maxValue, addBiomesValue));
    }

    private static boolean isBiomeEntry(Object entry) {
        if (!(entry instanceof String string) || string.isBlank()) return false;
        return ResourceLocation.tryParse(string.startsWith("#") ? string.substring(1) : string) != null;
    }

    public static AnimalSpawn getAnimalSpawn(EntityType<?> entityType) {
        ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
        if (!id.getNamespace().equals(FaunaAndOrchestra.MOD_ID)) return null;
        return ANIMAL_SPAWNS.get(id.getPath());
    }
}

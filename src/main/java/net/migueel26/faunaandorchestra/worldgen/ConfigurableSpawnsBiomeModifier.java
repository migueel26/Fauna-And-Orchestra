package net.migueel26.faunaandorchestra.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.migueel26.faunaandorchestra.Config;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;

import java.util.List;

// Like forge:add_spawns, but the weight, group size and extra biomes come from the common config
public record ConfigurableSpawnsBiomeModifier(HolderSet<Biome> biomes, EntityType<?> entityType) implements BiomeModifier {
    public static final Codec<ConfigurableSpawnsBiomeModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Biome.LIST_CODEC.fieldOf("biomes").forGetter(ConfigurableSpawnsBiomeModifier::biomes),
            BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("entity").forGetter(ConfigurableSpawnsBiomeModifier::entityType)
    ).apply(instance, ConfigurableSpawnsBiomeModifier::new));

    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase != Phase.ADD) return;

        Config.AnimalSpawn spawn = Config.getAnimalSpawn(entityType);
        if (spawn == null) return;

        if (!biomes.contains(biome) && !matchesAddedBiome(biome, spawn.addBiomes().get())) return;

        int weight = spawn.weight().get();
        if (weight <= 0) return;

        int minCount = spawn.minCount().get();
        int maxCount = Math.max(minCount, spawn.maxCount().get());
        builder.getMobSpawnSettings().addSpawn(entityType.getCategory(),
                new MobSpawnSettings.SpawnerData(entityType, weight, minCount, maxCount));
    }

    // Matches the biome ids and "#biome_tag" entries listed in add_biomes
    private static boolean matchesAddedBiome(Holder<Biome> biome, List<? extends String> addBiomes) {
        for (String entry : addBiomes) {
            if (entry == null || entry.isBlank()) continue;
            boolean isTag = entry.startsWith("#");
            ResourceLocation id = ResourceLocation.tryParse(isTag ? entry.substring(1) : entry);
            if (id == null) continue;
            if (isTag ? biome.is(TagKey.create(Registries.BIOME, id)) : biome.is(id)) return true;
        }
        return false;
    }

    @Override
    public Codec<? extends BiomeModifier> codec() {
        return ModBiomeModifiers.CONFIGURABLE_SPAWNS.get();
    }
}

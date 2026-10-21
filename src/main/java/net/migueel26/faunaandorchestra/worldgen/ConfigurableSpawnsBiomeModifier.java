package net.migueel26.faunaandorchestra.worldgen;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.migueel26.faunaandorchestra.Config;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;

// Like neoforge:add_spawns, but the weight and group size come from the common config
public record ConfigurableSpawnsBiomeModifier(HolderSet<Biome> biomes, EntityType<?> entityType) implements BiomeModifier {
    public static final MapCodec<ConfigurableSpawnsBiomeModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Biome.LIST_CODEC.fieldOf("biomes").forGetter(ConfigurableSpawnsBiomeModifier::biomes),
            BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("entity").forGetter(ConfigurableSpawnsBiomeModifier::entityType)
    ).apply(instance, ConfigurableSpawnsBiomeModifier::new));

    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase != Phase.ADD || !biomes.contains(biome)) return;

        Config.AnimalSpawn spawn = Config.getAnimalSpawn(entityType);
        if (spawn == null) return;

        int weight = spawn.weight().get();
        if (weight <= 0) return;

        int minCount = spawn.minCount().get();
        int maxCount = Math.max(minCount, spawn.maxCount().get());
        builder.getMobSpawnSettings().addSpawn(entityType.getCategory(),
                new MobSpawnSettings.SpawnerData(entityType, weight, minCount, maxCount));
    }

    @Override
    public MapCodec<? extends BiomeModifier> codec() {
        return ModBiomeModifiers.CONFIGURABLE_SPAWNS.get();
    }
}

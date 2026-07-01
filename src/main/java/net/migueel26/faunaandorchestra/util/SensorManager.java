package net.migueel26.faunaandorchestra.util;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class SensorManager {
    public static final TagKey<EntityType<?>> SCANNABLE_TAG = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.parse("faunaandorchestra:scannable"));
    private static List<EntityType<?>> sortedCache = null;

    public static List<EntityType<?>> getScannableEntities(Level level) {
        var registry = level.registryAccess().registryOrThrow(Registries.ENTITY_TYPE);

        var entities = registry.getTag(SCANNABLE_TAG)
                .map(namedTag -> namedTag.stream().map(Holder::value).toList())
                .orElse(List.of());

        List<EntityType<?>> sortedList = new ArrayList<>(entities);
        sortedList.sort((e1, e2) -> {
            ResourceLocation id1 = ForgeRegistries.ENTITY_TYPES.getKey(e1);
            ResourceLocation id2 = ForgeRegistries.ENTITY_TYPES.getKey(e2);
            return id1.compareTo(id2);
        });

        return sortedList;
    }

    public static TagKey<Biome> getBiomeTagForEntity(EntityType<?> entity) {
        ResourceLocation entityId = BuiltInRegistries.ENTITY_TYPE.getKey(entity);

        ResourceLocation tagId = ResourceLocation.fromNamespaceAndPath(
                entityId.getNamespace(),
                "spawns_" + entityId.getPath()
        );

        return TagKey.create(Registries.BIOME, tagId);
    }
}

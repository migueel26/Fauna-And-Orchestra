package net.migueel26.faunaandorchestra.worldgen;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;

public class ModConfiguredFeatures {

    public static final ResourceKey<ConfiguredFeature<?, ?>> GINKGO_BILOBA_KEY = registerKey("gingko_biloba");

    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(FaunaAndOrchestra.MOD_ID, name));
    }
}

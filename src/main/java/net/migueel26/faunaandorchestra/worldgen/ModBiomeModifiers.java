package net.migueel26.faunaandorchestra.worldgen;

import com.mojang.serialization.MapCodec;
import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModBiomeModifiers {
    public static final DeferredRegister<MapCodec<? extends BiomeModifier>> BIOME_MODIFIER_SERIALIZERS = DeferredRegister.create(
            NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, FaunaAndOrchestra.MOD_ID
    );

    public static final DeferredHolder<MapCodec<? extends BiomeModifier>, MapCodec<ConfigurableSpawnsBiomeModifier>> CONFIGURABLE_SPAWNS =
            BIOME_MODIFIER_SERIALIZERS.register("configurable_spawns", () -> ConfigurableSpawnsBiomeModifier.CODEC);

    public static void register(IEventBus eventBus) {
        BIOME_MODIFIER_SERIALIZERS.register(eventBus);
    }
}

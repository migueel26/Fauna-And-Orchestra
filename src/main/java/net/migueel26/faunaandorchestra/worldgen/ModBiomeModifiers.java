package net.migueel26.faunaandorchestra.worldgen;

import com.mojang.serialization.Codec;
import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBiomeModifiers {
    public static final DeferredRegister<Codec<? extends BiomeModifier>> BIOME_MODIFIER_SERIALIZERS = DeferredRegister.create(
            ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, FaunaAndOrchestra.MOD_ID
    );

    public static final RegistryObject<Codec<ConfigurableSpawnsBiomeModifier>> CONFIGURABLE_SPAWNS =
            BIOME_MODIFIER_SERIALIZERS.register("configurable_spawns", () -> ConfigurableSpawnsBiomeModifier.CODEC);

    public static void register(IEventBus eventBus) {
        BIOME_MODIFIER_SERIALIZERS.register(eventBus);
    }
}

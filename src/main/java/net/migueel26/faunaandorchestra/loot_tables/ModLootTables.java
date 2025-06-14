package net.migueel26.faunaandorchestra.loot_tables;

import com.mojang.serialization.MapCodec;
import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModLootTables {
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> GLOBAL_LOOT_MODIFIER_SERIALIZERS =
            DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, FaunaAndOrchestra.MOD_ID);

    public static final Supplier<MapCodec<AddDungeonItemModifier>> ADD_DUNGEON =
            GLOBAL_LOOT_MODIFIER_SERIALIZERS.register("add_dungeon", () -> AddDungeonItemModifier.CODEC);

    public static void register(IEventBus eventBus) {
        GLOBAL_LOOT_MODIFIER_SERIALIZERS.register(eventBus);
    }
}

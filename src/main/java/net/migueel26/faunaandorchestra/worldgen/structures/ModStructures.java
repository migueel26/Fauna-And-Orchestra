package net.migueel26.faunaandorchestra.worldgen.structures;

import com.mojang.serialization.MapCodec;
import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModStructures {
    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES = DeferredRegister.create(
            Registries.STRUCTURE_TYPE, FaunaAndOrchestra.MOD_ID
    );

    public static final DeferredHolder<StructureType<?>, StructureType<TerrainAdjustedStructure>> TERRAIN_ADJUSTED_STRUCTURE = STRUCTURE_TYPES.register("terrain_adjusted_structure", () -> explicitStructureTypeTyping(TerrainAdjustedStructure.CODEC));

    private static <T extends Structure> StructureType<T> explicitStructureTypeTyping(MapCodec<T> structureCodec) {
        return () -> structureCodec;
    }

    public static void register(IEventBus eventBus) {
        STRUCTURE_TYPES.register(eventBus);
    }
}

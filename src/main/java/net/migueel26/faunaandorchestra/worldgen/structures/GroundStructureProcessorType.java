package net.migueel26.faunaandorchestra.worldgen.structures;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;

public class GroundStructureProcessorType implements StructureProcessorType<GroundedProcessor> {
    @Override
    public MapCodec<GroundedProcessor> codec() {
        return GroundedProcessor.MAP_CODEC;
    }
}

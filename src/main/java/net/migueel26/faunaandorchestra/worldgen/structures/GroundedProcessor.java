package net.migueel26.faunaandorchestra.worldgen.structures;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.Nullable;

public class GroundedProcessor extends StructureProcessor {
    public static final MapCodec<GroundedProcessor> MAP_CODEC = MapCodec.unit(GroundedProcessor::new);

    @Nullable
    @Override
    public StructureTemplate.StructureBlockInfo process(
            LevelReader world,
            BlockPos pos,
            BlockPos rotationOffset,
            StructureTemplate.StructureBlockInfo blockInfo,
            StructureTemplate.StructureBlockInfo relativeBlockInfo,
            StructurePlaceSettings settings,
            @Nullable StructureTemplate template) {

        BlockPos placementPos = blockInfo.pos();

        // Solo actúa sobre bloques sólidos de la estructura
        if (!blockInfo.state().isAir()) {
            BlockPos below = placementPos.below();

            // Rellena cualquier hueco debajo con tierra hasta llegar a un bloque sólido
            while (world.isEmptyBlock(below) && below.getY() >= 0) {
                ((net.minecraft.world.level.LevelAccessor) world).setBlock(below, Blocks.DIRT.defaultBlockState(), 2);
                below = below.below();
            }
        }

        return blockInfo;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return ModStructuredProcessors.GROUNDED.get();
    }
}
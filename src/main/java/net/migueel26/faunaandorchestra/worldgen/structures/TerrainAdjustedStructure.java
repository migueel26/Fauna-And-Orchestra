package net.migueel26.faunaandorchestra.worldgen.structures;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.Optional;

class TerrainAdjustedStructure extends Structure {
    public static final Codec<TerrainAdjustedStructure> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    TerrainAdjustedStructure.settingsCodec(instance),
                    StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(structure -> structure.startPool),
                    Codec.intRange(0, 30).fieldOf("size").forGetter(structure -> structure.size),
                    HeightProvider.CODEC.fieldOf("start_height").forGetter(structure -> structure.startHeight),
                    Heightmap.Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(structure -> structure.projectStartToHeightmap),
                    Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter(structure -> structure.maxDistanceFromCenter)
            ).apply(instance, TerrainAdjustedStructure::new));

    // ---- FIELDS ----
    private final Holder<StructureTemplatePool> startPool;
    private final int size;
    private final HeightProvider startHeight;
    private final Optional<Heightmap.Types> projectStartToHeightmap;
    private final int maxDistanceFromCenter;

    public TerrainAdjustedStructure(Structure.StructureSettings config,
                         Holder<StructureTemplatePool> startPool,
                         int size,
                         HeightProvider startHeight,
                         Optional<Heightmap.Types> projectStartToHeightmap,
                         int maxDistanceFromCenter)
    {
        super(config);
        this.startPool = startPool;
        this.size = size;
        this.startHeight = startHeight;
        this.projectStartToHeightmap = projectStartToHeightmap;
        this.maxDistanceFromCenter = maxDistanceFromCenter;
    }

    @Override
    public StructureType<?> type() {
        return ModStructures.TERRAIN_ADJUSTED_STRUCTURE.get();
    }

    private static boolean extraSpawningChecks(Structure.GenerationContext context) {
        BlockPos center = context.chunkPos().getMiddleBlockPosition(0);
        int waterSurfaceY = context.chunkGenerator().getFirstOccupiedHeight(
                center.getX(), center.getZ(), Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState()
        );

        int groundY = context.chunkGenerator().getFirstOccupiedHeight(
                center.getX(), center.getZ(), Heightmap.Types.OCEAN_FLOOR_WG, context.heightAccessor(), context.randomState()
        );

        boolean isUnderwater = (waterSurfaceY - groundY) > 0; // means there's water

        return !isUnderwater;
    }

    @Override
    public Optional<GenerationStub> findGenerationPoint(Structure.GenerationContext context) {
        if (!extraSpawningChecks(context)) {
            return Optional.empty();
        }

        ChunkPos chunkPos = context.chunkPos();
        BlockPos center = context.chunkPos().getMiddleBlockPosition(0);
        // Determine surface Y at that XZ
        int surfaceY = context.chunkGenerator().getFirstOccupiedHeight(
                center.getX(), center.getZ(),
                Heightmap.Types.WORLD_SURFACE_WG,
                context.heightAccessor(),
                context.randomState()
        );

        // Modify origin to be on surface
        BlockPos spawnPos = new BlockPos(center.getX(), surfaceY + 2, center.getZ());

        return generatePieces(context, spawnPos);
    }

    public Optional<GenerationStub> generatePieces(GenerationContext context, BlockPos spawnPos) {
        // Use JigsawPlacement to build starting from your pool
        return JigsawPlacement.addPieces(
                context,
                this.startPool,
                Optional.empty(),
                this.size,
                spawnPos,
                false,
                this.projectStartToHeightmap,
                this.maxDistanceFromCenter
        );
    }
}
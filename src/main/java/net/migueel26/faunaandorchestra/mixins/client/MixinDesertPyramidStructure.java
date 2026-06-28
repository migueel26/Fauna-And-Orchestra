package net.migueel26.faunaandorchestra.mixins.client;

import net.migueel26.faunaandorchestra.entity.ModEntities;
import net.migueel26.faunaandorchestra.mixins.client.accessors.StructurePieceAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import net.minecraft.world.level.levelgen.structure.structures.DesertPyramidPiece;
import net.minecraft.world.level.levelgen.structure.structures.DesertPyramidStructure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;

@Mixin(DesertPyramidStructure.class)
public abstract class MixinDesertPyramidStructure {
    private static final Set<Long> SPAWNED_PYRAMIDS = new HashSet<>();

    @Inject(
            method = "afterPlace",
            at = @At("TAIL")
    )
    private void spawnEntityInPyramid(
            WorldGenLevel level,
            StructureManager structureManager,
            ChunkGenerator chunkGenerator,
            RandomSource random,
            BoundingBox boundingBox,
            ChunkPos chunkPos,
            PiecesContainer pieces,
            CallbackInfo ci
    ) {
        long pyramidId = pieces.calculateBoundingBox().getCenter().asLong();

        if (!SPAWNED_PYRAMIDS.add(pyramidId)) {
            // Already spawned
            return;
        }

        for (StructurePiece piece : pieces.pieces()) {
            if (piece instanceof DesertPyramidPiece pyramidPiece) {

                StructurePieceAccessor access = (StructurePieceAccessor) piece;
                int wx = access.invokeGetWorldX(10, 10);
                int wy = access.invokeGetWorldY(1);
                int wz = access.invokeGetWorldZ(10, 10);
                BlockPos pos = new BlockPos.MutableBlockPos(wx, wy, wz);

                //level.setBlock(pos, ModBlocks.DAM_BLOCK.get().defaultBlockState(), 3);

                if (!level.isClientSide()) {
                    level.getServer().execute(() -> {
                        ModEntities.WANDERING_KOALA.get().spawn(level.getLevel(), pos, MobSpawnType.STRUCTURE);
                    });
                }

                return;
            }
        }
    }
}

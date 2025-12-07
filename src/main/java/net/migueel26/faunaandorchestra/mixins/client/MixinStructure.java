package net.migueel26.faunaandorchestra.mixins.client;

import net.migueel26.faunaandorchestra.entity.ModEntities;
import net.migueel26.faunaandorchestra.mixins.client.accessors.StructurePieceAccessor;
import net.migueel26.faunaandorchestra.util.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import net.minecraft.world.level.levelgen.structure.structures.IglooPieces;
import net.minecraft.world.level.levelgen.structure.structures.IglooStructure;
import net.minecraft.world.level.levelgen.structure.structures.JungleTemplePiece;
import net.minecraft.world.level.levelgen.structure.structures.JungleTempleStructure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Mixin(Structure.class)
public class MixinStructure {
    private static final Set<Long> SPAWNED_TEMPLES = new HashSet<>();
    private static final Set<Long> SPAWNED_IGLOOS = new HashSet<>();
    @Inject(method = "afterPlace", at = @At("RETURN"))
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
        if ((Object) this instanceof JungleTempleStructure jungleTempleStructure) {
            long pyramidId = pieces.calculateBoundingBox().getCenter().asLong();

            if (!SPAWNED_TEMPLES.add(pyramidId)) {
                // Already spawned
                return;
            }

            for (StructurePiece piece : pieces.pieces()) {
                if (piece instanceof JungleTemplePiece templePiece) {

                    StructurePieceAccessor access = (StructurePieceAccessor) piece;
                    int wx = access.invokeGetWorldX(8, 7);
                    int wy = access.invokeGetWorldY(1);
                    int wz = access.invokeGetWorldZ(7, 8);
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
        } else if ((Object) this instanceof IglooStructure iglooStructure) {
            long pyramidId = pieces.calculateBoundingBox().getCenter().asLong();

            if (!SPAWNED_IGLOOS.add(pyramidId)) {
                // Already spawned
                return;
            }

            for (StructurePiece piece : pieces.pieces()) {
                if (piece instanceof IglooPieces.IglooPiece iglooPiece) {

                    StructurePieceAccessor access = (StructurePieceAccessor) piece;
                    int wx = access.invokeGetWorldX(0, 0);
                    int wy = access.invokeGetWorldY(5);
                    int wz = access.invokeGetWorldZ(0, 0);
                    BlockPos pos = new BlockPos.MutableBlockPos(wx, wy, wz);

                    //level.setBlock(pos, ModBlocks.DAM_BLOCK.get().defaultBlockvState(), 3);

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
}

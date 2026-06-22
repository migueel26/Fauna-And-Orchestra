package net.migueel26.faunaandorchestra.block.entity.spawners;

import net.migueel26.faunaandorchestra.block.ModBlockEntities;
import net.migueel26.faunaandorchestra.block.custom.spawners.PaintingSpawnerBlock;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PaintingSpawnerBlockEntity extends BlockEntity {
    private String selectedVariantId = "minecraft:kebab";
    private int spawnDelayTicks;

    public PaintingSpawnerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PAINTING_SPAWNER_BE.get(), pos, state);
        this.spawnDelayTicks = 60;
    }


    public String cycleVariant(RegistryAccess registryAccess) {
        var registry = registryAccess.lookupOrThrow(Registries.PAINTING_VARIANT);

        // CORRECCIÓN: Buscamos las claves (keys) directas de los cuadros, ignorando los tags de grupo
        List<ResourceLocation> allVariants = new ArrayList<>();
        registry.listElementIds().forEach(key -> allVariants.add(key.location()));

        if (allVariants.isEmpty()) return "Ninguno";

        // Buscamos el índice del cuadro actual en la lista
        ResourceLocation currentRL = ResourceLocation.parse(this.selectedVariantId);
        int currentIndex = allVariants.indexOf(currentRL);

        // Si por algún motivo el cuadro por defecto no se encuentra, reiniciamos a 0
        if (currentIndex == -1) currentIndex = 0;

        // Pasamos al siguiente cuadro de la lista (vuelve a 0 si llega al final)
        int nextIndex = (currentIndex + 1) % allVariants.size();
        ResourceLocation nextRL = allVariants.get(nextIndex);

        this.selectedVariantId = nextRL.toString();
        this.setChanged(); // Guardamos el cambio en el bloque

        // Retorna el ID completo o solo el nombre (nextRL.getPath() para "alban", "kebab"...)
        return nextRL.toString();
    }


    public static void tick(Level level, BlockPos pos, BlockState state, PaintingSpawnerBlockEntity blockEntity) {

        if (level instanceof ServerLevel serverLevel) {

            if (blockEntity.spawnDelayTicks > 0) {
                blockEntity.spawnDelayTicks--;
                return;
            }

            // 1. Obtenemos la dirección exacta. El Jigsaw YA ha rotado este valor en el BlockState.
            BlockState realState = serverLevel.getBlockState(pos);

            // Verificación por seguridad de que sigue siendo nuestro bloque antes de leer la propiedad
            if (!realState.hasProperty(PaintingSpawnerBlock.FACING)) {
                return;
            }

            Direction facing = realState.getValue(PaintingSpawnerBlock.FACING);
            ResourceLocation variantRL = ResourceLocation.parse(blockEntity.selectedVariantId);

            Optional<Holder.Reference<PaintingVariant>> variantHolder =
                    serverLevel.registryAccess()
                            .lookupOrThrow(Registries.PAINTING_VARIANT)
                            .get(ResourceKey.create(Registries.PAINTING_VARIANT, variantRL));

            if (variantHolder.isPresent()) {
                // 2. IMPORTANTE: Dejamos que el constructor de Minecraft calcule la posición exacta.
                // Pasarle el 'pos' crudo (BlockPos) y el 'facing' correcto inicializa el BoundingBox
                // de forma perfecta según el tamaño del cuadro (1x1, 2x2, 4x4...) sin importar la rotación del Jigsaw.
                Painting painting = new Painting(serverLevel, pos, facing, variantHolder.get());

                // 3. Obtenemos los decimales exactos ya calculados por el motor de Minecraft
                double finalX = painting.getX();
                double finalY = painting.getY() - 1.0f;
                double finalZ = painting.getZ();

                switch (facing) {
                    case NORTH: break; // Si mira al Norte, su derecha es el Este
                    case SOUTH: finalX -= 1.0D; break; // Si mira al Sur, su derecha es el Oeste
                    case WEST:  finalZ -= 1.0D; break; // Si mira al Oeste, su derecha es el Norte
                    case EAST:  break; // Si mira al Este, su derecha es el Sur
                }

                // 4. Forzamos el teletransporte a su propio centro calculado y aplicamos la rotación horizontal
                painting.moveTo(finalX, finalY, finalZ, facing.get2DDataValue() * 90.0F, 0.0F);

                // 5. Registramos la entidad en el mundo de forma síncrona
                serverLevel.addFreshEntity(painting);
            }

            // 6. El spawner se vuelve aire de forma limpia
            serverLevel.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        }

    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString("SelectedVariant", this.selectedVariantId);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("SelectedVariant")) {
            this.selectedVariantId = tag.getString("SelectedVariant");
        }
    }
}
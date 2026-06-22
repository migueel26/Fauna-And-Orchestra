package net.migueel26.faunaandorchestra.block.entity.spawners;

import net.migueel26.faunaandorchestra.block.ModBlockEntities;
import net.migueel26.faunaandorchestra.entity.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.function.Supplier;

public class TavernSpawnerBlockEntity extends BlockEntity {

    private static final List<Supplier<? extends EntityType<? extends Mob>>> POSSIBLE_CITIZENS = List.of(
            ModEntities.EMPEROR_PENGUIN,
            ModEntities.PENGUIN,
            ModEntities.RED_PANDA,
            ModEntities.LEMUR
    );

    public TavernSpawnerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TAVERN_SPAWNER_BE.get(), pos, state);
    }


    public static void tick(Level level, BlockPos pos, BlockState state, TavernSpawnerBlockEntity blockEntity) {
        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            serverLevel.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);

            RandomSource random = serverLevel.getRandom();

            EntityType<? extends Mob> chosenType = POSSIBLE_CITIZENS.get(random.nextInt(POSSIBLE_CITIZENS.size())).get();
            Mob entity = chosenType.create(serverLevel);

            if (entity != null) {
                double offsetX = 0.5D;
                double offsetY = 0.0D;
                double offsetZ = 0.5D;

                entity.moveTo(
                        pos.getX() + offsetX,
                        pos.getY() + offsetY,
                        pos.getZ() + offsetZ,
                        random.nextFloat() * 360.0F,
                        0.0F
                );

                serverLevel.addFreshEntity(entity);
            }
        }
    }
}

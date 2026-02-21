package net.migueel26.faunaandorchestra.item.custom;

import net.migueel26.faunaandorchestra.block.ModBlocks; // Asegúrate de importar tus bloques
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class BadgrassMealItem extends Item {

    public BadgrassMealItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        ItemStack stack = context.getItemInHand();

        if (level.getBlockState(pos).is(Blocks.GRASS_BLOCK)) {
            if (!level.isClientSide && level instanceof ServerLevel serverLevel) {

                growBadgrass(serverLevel, pos);

                if (!context.getPlayer().getAbilities().instabuild) {
                    stack.shrink(1);
                }

                context.getPlayer().gameEvent(GameEvent.ITEM_INTERACT_FINISH);
                level.levelEvent(1505, pos, 0);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.PASS;
    }

    private void growBadgrass(ServerLevel level, BlockPos pos) {
        BlockPos posAbove = pos.above();
        RandomSource random = level.getRandom();

        loopAttempts:
        for (int i = 0; i < 128; ++i) {
            BlockPos currentPos = posAbove;

            for (int j = 0; j < i / 16; ++j) {
                currentPos = currentPos.offset(
                        random.nextInt(3) - 1,
                        (random.nextInt(3) - 1) * random.nextInt(3) / 2,
                        random.nextInt(3) - 1
                );

                if (!level.getBlockState(currentPos.below()).is(Blocks.GRASS_BLOCK) || level.getBlockState(currentPos).isCollisionShapeFullBlock(level, currentPos)) {
                    continue loopAttempts;
                }
            }

            BlockState stateAtPos = level.getBlockState(currentPos);

            if (stateAtPos.isAir()) {
                BlockState stateToPlace;

                // --- SISTEMA DE PROBABILIDADES ---
                int roll = random.nextInt(100); // Tira un dado del 0 al 99

                if (roll < 10) {
                    stateToPlace = ModBlocks.CATCHWEED.get().defaultBlockState();
                } else if (roll < 30) {
                    stateToPlace = Blocks.TALL_GRASS.defaultBlockState();
                } else {
                    stateToPlace = Blocks.SHORT_GRASS.defaultBlockState();
                }

                if (stateToPlace.is(Blocks.TALL_GRASS) || stateToPlace.is(ModBlocks.CATCHWEED)) {
                    if (level.isEmptyBlock(currentPos.above())) {
                        DoublePlantBlock.placeAt(level, stateToPlace, currentPos, 3);
                    }
                }
                else {
                    if (stateToPlace.canSurvive(level, currentPos)) {
                        level.setBlock(currentPos, stateToPlace, 3);
                    }
                }
            }
        }
    }
}

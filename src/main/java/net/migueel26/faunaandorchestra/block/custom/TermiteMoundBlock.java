package net.migueel26.faunaandorchestra.block.custom;

import net.migueel26.faunaandorchestra.entity.custom.TermiteQueen;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.Optional;

public class TermiteMoundBlock extends Block {
    public TermiteMoundBlock(Properties properties) {
        super(properties);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState nextState, boolean isMoving) {
        if (!level.isClientSide()) {
            Optional<TermiteQueen> termiteQueenOpt = level.getEntitiesOfClass(TermiteQueen.class, AABB.ofSize(pos.getCenter(), 10, 10, 10)).stream().findAny();
            termiteQueenOpt.ifPresent(TermiteQueen::increaseTermiteMoundsDestroyed);
        }
        super.onRemove(state, level, pos, nextState, isMoving);
    }
}
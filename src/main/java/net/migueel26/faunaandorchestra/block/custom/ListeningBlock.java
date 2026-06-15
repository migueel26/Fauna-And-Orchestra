package net.migueel26.faunaandorchestra.block.custom;

import net.migueel26.faunaandorchestra.block.entity.FloraEnhancerBlockEntity;
import net.migueel26.faunaandorchestra.entity.custom.ConductorEntity;
import net.migueel26.faunaandorchestra.entity.custom.ListeningBlockEntity;
import net.migueel26.faunaandorchestra.util.MusicUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.AABB;

public interface ListeningBlock {
    default void tryToStartListening(ServerLevel level, BlockPos pos, ListeningBlockEntity blockEntity) {
        ConductorEntity conductor = MusicUtil.lookForConductor(level, AABB.ofSize(pos.getCenter(), 0.5f, 0.5f, 0.5f));

        if (conductor != null) {
            blockEntity.onStartListening(conductor);
        }
    }
}

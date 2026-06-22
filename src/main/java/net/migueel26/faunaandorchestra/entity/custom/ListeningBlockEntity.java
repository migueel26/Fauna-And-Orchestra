package net.migueel26.faunaandorchestra.entity.custom;

import net.migueel26.faunaandorchestra.block.entity.FloraEnhancerBlockEntity;
import net.migueel26.faunaandorchestra.util.MusicUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.AABB;

public interface ListeningBlockEntity {
    // These blocks are waiting for an orchestra to start or end
    void onStartListening(ConductorEntity conductor);
    void onStopListening();
    boolean isListening();
    default void tryToStartListening(ServerLevel level, BlockPos pos) {
        ConductorEntity conductor = MusicUtil.lookForConductor(level, AABB.ofSize(pos.getCenter(), 0.5f, 0.5f, 0.5f));

        if (conductor != null) {
            onStartListening(conductor);
        }
    }
}

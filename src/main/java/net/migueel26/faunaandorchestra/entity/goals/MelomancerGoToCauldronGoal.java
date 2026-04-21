package net.migueel26.faunaandorchestra.entity.goals;

import net.migueel26.faunaandorchestra.block.ModBlocks;
import net.migueel26.faunaandorchestra.block.custom.MelomancyCauldronBlock;
import net.migueel26.faunaandorchestra.entity.custom.koala_workers.MelomancerKoalaEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class MelomancerGoToCauldronGoal extends Goal {
    private final MelomancerKoalaEntity melomancer;
    private BlockPos targetCauldron = null;
    private int searchCooldown = 0;
    private int pathUpdateCountdown = 0;
    private final double speedModifier = 1.0D;

    public MelomancerGoToCauldronGoal(MelomancerKoalaEntity melomancer) {
        this.melomancer = melomancer;
        this.setFlags(java.util.EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.melomancer.isKoalaSleeping()) {
            return false;
        }

        if (this.melomancer.getState() != MelomancerKoalaEntity.MelomancerState.GOING_TO_MIX) {
            return false;
        }

        if (this.targetCauldron != null && isCauldronValid(this.targetCauldron)) {
            return true;
        }

        if (this.searchCooldown > 0) {
            this.searchCooldown--;
            return false;
        }

        Optional<BlockPos> optionalPos = BlockPos.findClosestMatch(
                this.melomancer.blockPosition(),
                16, 4,
                this::isCauldronValid
        );

        if (optionalPos.isPresent()) {
            this.targetCauldron = optionalPos.get();
            return true;
        } else {
            this.searchCooldown = 40;
            return false;
        }
    }

    @Override
    public boolean canContinueToUse() {
        return this.melomancer.getState() == MelomancerKoalaEntity.MelomancerState.GOING_TO_MIX
                && this.targetCauldron != null
                && isCauldronValid(this.targetCauldron);
    }

    @Override
    public void start() {
        this.pathUpdateCountdown = 0;
    }

    @Override
    public void stop() {
        this.targetCauldron = null;
        this.melomancer.getNavigation().stop();
    }

    @Override
    public void tick() {
        double dist = melomancer.distanceToSqr(targetCauldron.getCenter());

        if (dist < 1.0D) {
            this.melomancer.getNavigation().stop();

            this.melomancer.getLookControl().setLookAt(this.targetCauldron.getX() + 0.5D, this.targetCauldron.getY() + 0.5D, this.targetCauldron.getZ() + 0.5D, 30.0F, 30.0F);
            this.melomancer.setCauldronPos(this.targetCauldron);
            this.melomancer.startToMix();
        } else {
            this.melomancer.getLookControl().setLookAt(this.targetCauldron.getX() + 0.5D, this.targetCauldron.getY() + 0.5D, this.targetCauldron.getZ() + 0.5D, 10.0F, (float)this.melomancer.getMaxHeadXRot());

            if (--this.pathUpdateCountdown <= 0) {
                this.pathUpdateCountdown = 10;

                this.melomancer.getNavigation().moveTo(this.targetCauldron.getCenter().x(), this.targetCauldron.getY(), this.targetCauldron.getCenter().z(), this.speedModifier);
            }
        }
    }

    private boolean isCauldronValid(BlockPos pos) {
        BlockState state = this.melomancer.level().getBlockState(pos);
        return state.is(ModBlocks.MELOMANCY_CAULDRON.get())
                && !state.getValue(MelomancyCauldronBlock.COOKING)
                && state.getValue(MelomancyCauldronBlock.LIQUID) == 0;
    }
}

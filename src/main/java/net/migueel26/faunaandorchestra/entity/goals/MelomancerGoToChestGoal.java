package net.migueel26.faunaandorchestra.entity.goals;

import net.migueel26.faunaandorchestra.entity.custom.koala_workers.MelomancerKoalaEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import static net.migueel26.faunaandorchestra.entity.custom.koala_workers.MelomancerKoalaEntity.OUTPUT_SLOT;

public class MelomancerGoToChestGoal extends Goal {
    private final MelomancerKoalaEntity melomancer;
    private int pathUpdateCountdown = 0;
    private final double speedModifier = 1.0D;
    private int depositTimer = -1;

    public MelomancerGoToChestGoal(MelomancerKoalaEntity melomancer) {
        this.melomancer = melomancer;
        this.setFlags(java.util.EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.melomancer.isKoalaSleeping()) {
            return false;
        }

        if (this.melomancer.getState() != MelomancerKoalaEntity.MelomancerState.GOING_TO_CHEST) {
            return false;
        }

        BlockPos targetChest = this.melomancer.getWorkingStation();

        // If the workingStation is no longer a chest
        if (targetChest == null || !this.melomancer.isWorkingStation(this.melomancer.level().getBlockState(targetChest))) {
            this.melomancer.setWorkingStation(BlockPos.ZERO);
            this.melomancer.setState(MelomancerKoalaEntity.MelomancerState.NOTHING);
            return false;
        }

        return true;
    }

    @Override
    public boolean canContinueToUse() {
        BlockPos targetChest = this.melomancer.getWorkingStation();
        return this.melomancer.getState() == MelomancerKoalaEntity.MelomancerState.GOING_TO_CHEST
                && targetChest != null
                && this.melomancer.isWorkingStation(this.melomancer.level().getBlockState(targetChest));
    }

    @Override
    public void start() {
        this.pathUpdateCountdown = 0;
    }

    @Override
    public void stop() {
        this.melomancer.getNavigation().stop();
    }

    @Override
    public void tick() {
        BlockPos targetChest = this.melomancer.getWorkingStation();
        if (targetChest == null) return;

        if (this.melomancer.distanceToSqr(targetChest.getCenter()) < 4.0D) {
            if (this.depositTimer == -1) {
                this.melomancer.getNavigation().stop();
                this.setChestState(targetChest, true);
                this.depositTimer = 15;
            }

            if (this.depositTimer > 0) {
                this.depositTimer--;
                this.melomancer.getLookControl().setLookAt(targetChest.getX() + 0.5D, targetChest.getY(), targetChest.getZ() + 0.5D, 10.0F, (float)this.melomancer.getMaxHeadXRot());
            }

            if (this.depositTimer == 0) {
                depositItemsAndRestart(targetChest);
                this.setChestState(targetChest, false);
                this.depositTimer = -1;
            }
        } else {
            this.melomancer.getLookControl().setLookAt(targetChest.getX() + 0.5D, targetChest.getY(), targetChest.getZ() + 0.5D, 10.0F, (float)this.melomancer.getMaxHeadXRot());

            if (--this.pathUpdateCountdown <= 0) {
                this.pathUpdateCountdown = 20;
                this.melomancer.getNavigation().moveTo(targetChest.getX(), targetChest.getY(), targetChest.getZ(), this.speedModifier);
            }
        }
    }

    private void setChestState(BlockPos pos, boolean open) {
        Level level = this.melomancer.level();
        BlockState state = level.getBlockState(pos);

        if (this.melomancer.isWorkingStation(state)) {
            int param = open ? 1 : 0;
            level.blockEvent(pos, state.getBlock(), 1, param);
        }
    }

    private void depositItemsAndRestart(BlockPos targetChest) {
        Level level = this.melomancer.level();

        // Items to insert inside the chest
        ItemStack outputItem = this.melomancer.inventory.getStackInSlot(OUTPUT_SLOT).copy();
        ItemStack emptyBottles = new ItemStack(net.minecraft.world.item.Items.GLASS_BOTTLE, 3);

        this.melomancer.inventory.setStackInSlot(OUTPUT_SLOT, ItemStack.EMPTY);

        // The chest container
        Container chestContainer = HopperBlockEntity.getContainerAt(level, targetChest);

        if (chestContainer != null) {
            outputItem = HopperBlockEntity.addItem(null, chestContainer, outputItem, null);
            emptyBottles = HopperBlockEntity.addItem(null, chestContainer, emptyBottles, null);
        }

        // If the chest is full
        if (!outputItem.isEmpty()) {
            ItemEntity drop = this.melomancer.spawnAtLocation(outputItem);
            if (drop != null) drop.setDeltaMovement(0, 0.2, 0); // Pequeño salto visual
        }
        if (!emptyBottles.isEmpty()) {
            ItemEntity drop = this.melomancer.spawnAtLocation(emptyBottles);
            if (drop != null) drop.setDeltaMovement(0, 0.2, 0);
        }

        this.melomancer.setState(MelomancerKoalaEntity.MelomancerState.NOTHING);
        this.melomancer.tryToMix();
    }
}

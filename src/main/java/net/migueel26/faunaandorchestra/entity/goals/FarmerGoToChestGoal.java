package net.migueel26.faunaandorchestra.entity.goals;

import net.migueel26.faunaandorchestra.entity.custom.koala_workers.FarmerKoalaEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class FarmerGoToChestGoal extends Goal {
    private final FarmerKoalaEntity farmer;
    private int pathUpdateCountdown = 0;
    private final double speedModifier = 1.15D;
    private int depositTimer = -1;

    public FarmerGoToChestGoal(FarmerKoalaEntity farmer) {
        this.farmer = farmer;
        this.setFlags(java.util.EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.farmer.isKoalaSleeping() || this.farmer.isInLunchBreak()) {
            return false;
        }

        // Only go to the chest if the inventory is full
        if (!isInventoryFull()) {
            return false;
        }

        BlockPos targetChest = this.farmer.getWorkingStation();

        // If the working station no longer exists or isn't a chest, return false
        if (targetChest == null || !this.farmer.isWorkingStation(this.farmer.level().getBlockState(targetChest))) {
            if (targetChest != null) this.farmer.setWorkingStation(BlockPos.ZERO);
            return false;
        }

        return true;
    }

    @Override
    public boolean canContinueToUse() {
        BlockPos targetChest = this.farmer.getWorkingStation();

        if (targetChest == null || !this.farmer.isWorkingStation(this.farmer.level().getBlockState(targetChest))) {
            return false;
        }

        return hasItems() || this.depositTimer != -1;
    }

    @Override
    public void start() {
        this.pathUpdateCountdown = 0;
        this.depositTimer = -1;
    }

    @Override
    public void stop() {
        this.farmer.getNavigation().stop();
        this.depositTimer = -1;
    }

    @Override
    public void tick() {
        BlockPos targetChest = this.farmer.getWorkingStation();
        if (targetChest == null) return;

        if (this.farmer.distanceToSqr(targetChest.getCenter()) < 3.0D) {
            if (this.depositTimer == -1) {
                this.farmer.getNavigation().stop();
                this.setChestState(targetChest, true);
                this.depositTimer = 15;

                if (this.farmer.level() instanceof ServerLevel level) {
                    level.playSound(null, targetChest, SoundEvents.CHEST_OPEN, SoundSource.BLOCKS, 1.0F, 1.0F);
                }
            }

            if (this.depositTimer > 0) {
                this.depositTimer--;
                this.farmer.getLookControl().setLookAt(targetChest.getX() + 0.5D, targetChest.getY(), targetChest.getZ() + 0.5D, 10.0F, (float)this.farmer.getMaxHeadXRot());
            }

            if (this.depositTimer == 0) {
                depositAllItems(targetChest);
                this.setChestState(targetChest, false);
                this.depositTimer = -1;

                if (this.farmer.level() instanceof ServerLevel level) {
                    level.playSound(null, targetChest, SoundEvents.CHEST_CLOSE, SoundSource.BLOCKS, 1.0F, 1.0F);
                }
            }
        } else {
            this.farmer.getLookControl().setLookAt(targetChest.getX() + 0.5D, targetChest.getY(), targetChest.getZ() + 0.5D, 10.0F, (float)this.farmer.getMaxHeadXRot());

            if (--this.pathUpdateCountdown <= 0) {
                this.pathUpdateCountdown = 20;
                this.farmer.getNavigation().moveTo(targetChest.getX(), targetChest.getY(), targetChest.getZ(), this.speedModifier);
            }
        }
    }

    private void setChestState(BlockPos pos, boolean open) {
        Level level = this.farmer.level();
        BlockState state = level.getBlockState(pos);

        if (this.farmer.isWorkingStation(state)) {
            int param = open ? 1 : 0;
            level.blockEvent(pos, state.getBlock(), 1, param);
        }
    }

    private void depositAllItems(BlockPos targetChest) {
        Level level = this.farmer.level();
        Container chestContainer = HopperBlockEntity.getContainerAt(level, targetChest);

        for (int i = 0; i < this.farmer.inventory.getSlots(); i++) {
            ItemStack stackInSlot = this.farmer.inventory.getStackInSlot(i);

            if (!stackInSlot.isEmpty()) {
                ItemStack toInsert = stackInSlot.copy();
                this.farmer.inventory.setStackInSlot(i, ItemStack.EMPTY);

                if (chestContainer != null) {
                    toInsert = HopperBlockEntity.addItem(null, chestContainer, toInsert, null);
                }

                // If the chest is full, drop the items
                if (!toInsert.isEmpty()) {
                    ItemEntity drop = this.farmer.spawnAtLocation(toInsert);
                    if (drop != null) drop.setDeltaMovement(0, 0.2, 0);
                }
            }
        }
    }

    private boolean isInventoryFull() {
        for (int i = 0; i < this.farmer.inventory.getSlots(); i++) {
            if (this.farmer.inventory.getStackInSlot(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private boolean hasItems() {
        for (int i = 0; i < this.farmer.inventory.getSlots(); i++) {
            if (!this.farmer.inventory.getStackInSlot(i).isEmpty()) {
                return true;
            }
        }
        return false;
    }
}
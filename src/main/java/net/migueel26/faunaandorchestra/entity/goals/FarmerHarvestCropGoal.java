package net.migueel26.faunaandorchestra.entity.goals;

import net.migueel26.faunaandorchestra.entity.custom.koala_workers.FarmerKoalaEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.List;
import java.util.Optional;

public class FarmerHarvestCropGoal extends Goal {
    public static final int HARVEST_TIMER = 15;
    private final FarmerKoalaEntity farmer;
    private BlockPos targetCrop = null;
    private int searchCooldown = 0;
    private int pathUpdateCountdown = 0;
    private int harvestTimer = -1;
    private final double speedModifier = 1.0D;
    private double offset = 0.0f; // In case of bug

    public FarmerHarvestCropGoal(FarmerKoalaEntity farmer) {
        this.farmer = farmer;
        this.setFlags(java.util.EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.farmer.isKoalaSleeping() || !this.farmer.hasWorkingStation() || this.farmer.isInLunchBreak()) {
            return false;
        }

        if (this.targetCrop != null && isCropValid(this.targetCrop)) {
            this.farmer.resetBoredom();
            return true;
        }

        if (this.searchCooldown > 0) {
            this.searchCooldown--;
            return false;
        }

        Optional<BlockPos> optionalPos = BlockPos.findClosestMatch(
                this.farmer.blockPosition(), 16, 4,
                this::isCropValid
        );

        if (optionalPos.isPresent()) {
            this.farmer.resetBoredom();
            this.targetCrop = optionalPos.get();
            return true;
        } else {
            this.searchCooldown = 40;
            return false;
        }
    }

    @Override
    public boolean canContinueToUse() {
        return !this.farmer.isKoalaSleeping()
                && !this.farmer.isInLunchBreak()
                && this.farmer.hasWorkingStation()
                && this.targetCrop != null
                && isCropValid(this.targetCrop);
    }

    @Override
    public void start() {
        this.pathUpdateCountdown = 0;
        this.harvestTimer = -1;
    }

    @Override
    public void stop() {
        this.targetCrop = null;
        this.harvestTimer = -1;
        this.offset = 0;
        this.farmer.getNavigation().stop();
    }

    @Override
    public void tick() {
        if (this.targetCrop == null) return;

        double dX = this.farmer.getX() - (this.targetCrop.getX() + 0.5D);
        double dZ = this.farmer.getZ() - (this.targetCrop.getZ() + 0.5D);
        double distSq2D = (dX * dX) + (dZ * dZ);

        // If the koala is nearby
        if (distSq2D < 2.1D + offset) {
            // Do the animation
            if (this.harvestTimer == -1) {
                this.farmer.getNavigation().stop();
                this.farmer.getLookControl().setLookAt(this.targetCrop.getX() + 0.5D, this.targetCrop.getY(), this.targetCrop.getZ() + 0.5D, 30.0F, 30.0F);
                this.farmer.harvest();
                this.harvestTimer = HARVEST_TIMER;
            }

            // Wait for the animation to end
            if (this.harvestTimer > 0) {
                this.harvestTimer--;
                this.farmer.getLookControl().setLookAt(this.targetCrop.getX() + 0.5D, this.targetCrop.getY(), this.targetCrop.getZ() + 0.5D, 10.0F, (float) this.farmer.getMaxHeadXRot());
            }

            // Do the harvest logic
            if (this.harvestTimer == 0) {
                if (farmer.level() instanceof ServerLevel level) {
                    level.playSound(null, this.targetCrop, SoundEvents.CROP_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
                    level.sendParticles(
                            new BlockParticleOption(ParticleTypes.BLOCK, level.getBlockState(this.targetCrop)),
                            this.targetCrop.getX() + 0.5D,
                            this.targetCrop.getY() + 0.5D,
                            this.targetCrop.getZ() + 0.5D,
                            30, 0.3, 0.3, 0.3, 0.05
                    );
                }

                doHarvestLogic();

                this.harvestTimer = -1;
                this.targetCrop = null; // Finish
            }
        } else {
            // Move to the crop
            this.farmer.getLookControl().setLookAt(this.targetCrop.getX() + 0.5D, this.targetCrop.getY(), this.targetCrop.getZ() + 0.5D, 10.0F, (float) this.farmer.getMaxHeadXRot());

            offset += 0.005f;

            if (--this.pathUpdateCountdown <= 0) {
                this.pathUpdateCountdown = 15;
                this.farmer.getNavigation().moveTo(this.targetCrop.getX() + 0.5D, this.targetCrop.getY(), this.targetCrop.getZ() + 0.5D, this.speedModifier);
            }
        }
    }

    private boolean isCropValid(BlockPos pos) {
        BlockState state = this.farmer.level().getBlockState(pos);
        return state.getBlock() instanceof CropBlock crop && crop.isMaxAge(state);
    }

    private void doHarvestLogic() {
        Level level = this.farmer.level();
        BlockState state = level.getBlockState(this.targetCrop);

        if (state.getBlock() instanceof CropBlock crop && level instanceof ServerLevel serverLevel) {
            // We get what the crop would drop
            List<ItemStack> drops = Block.getDrops(state, serverLevel, this.targetCrop, null, this.farmer, ItemStack.EMPTY);

            // We get the seed of the crop
            ItemStack seedItem = crop.getCloneItemStack(level, this.targetCrop, state);

            // We replace the crop
            level.setBlock(this.targetCrop, crop.getStateForAge(0), 3);

            boolean seedDeducted = false;

            // We save the drops
            for (ItemStack drop : drops) {
                if (drop.isEmpty()) continue;

                // We use one of the seeds
                if (!seedDeducted && drop.is(seedItem.getItem()) && drop.getCount() > 0) {
                    drop.shrink(1);
                    seedDeducted = true;
                }

                if (drop.isEmpty()) continue;

                // We insert the remainder into the inventory
                ItemStack remainder = ItemHandlerHelper.insertItemStacked(this.farmer.inventory, drop, false);

                // If the inventory is full, we leave the result on the ground
                if (!remainder.isEmpty()) {
                    this.farmer.spawnAtLocation(remainder);
                }
            }

            // Start break
            this.farmer.increaseConsecutiveCrops();
            if (this.farmer.getConsecutiveCrops() >= FarmerKoalaEntity.CROPS_UNTIL_BREAK) {
                this.farmer.startLunchBreak();
            }
        }
    }
}
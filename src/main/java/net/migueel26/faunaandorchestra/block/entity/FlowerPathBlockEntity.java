package net.migueel26.faunaandorchestra.block.entity;

import net.migueel26.faunaandorchestra.block.ModBlockEntities;
import net.migueel26.faunaandorchestra.block.custom.FlowerPathBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

public class FlowerPathBlockEntity extends BlockEntity {
    private int generation = 0;
    private boolean father = false;
    private int maxGeneration = FlowerPathBlock.DEFAULT_MAX_GENERATION;

    private int tickCount = FlowerPathBlock.SPREAD_DELAY;

    public FlowerPathBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FLOWER_PATH_BE.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, FlowerPathBlockEntity entity) {
        if (level.isClientSide()) return;

        if (entity.tickCount > 0) {
            entity.tickCount--;
        } else if (entity.tickCount == 0) {
            if (!entity.father) {
                entity.performSpreadLogic(level, pos, state);
            } else {
                level.removeBlock(pos, false);
            }
        }
    }

    private void performSpreadLogic(Level level, BlockPos pos, BlockState state) {
        if (this.generation < this.maxGeneration) {
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                BlockPos neighborPos = pos.relative(dir);
                BlockState neighborState = level.getBlockState(neighborPos);

                if (neighborState.canBeReplaced()) {
                    BlockState newState = state.getBlock().defaultBlockState()
                            .setValue(FlowerPathBlock.FACING, Direction.Plane.HORIZONTAL.getRandomDirection(level.random))
                            .setValue(FlowerPathBlock.WATERLOGGED, neighborState.getFluidState().is(Fluids.WATER));

                    level.setBlock(neighborPos, newState, 3);

                    if (level.getBlockEntity(neighborPos) instanceof FlowerPathBlockEntity newFlowerBE) {
                        newFlowerBE.setGeneration(this.generation + 1);
                        newFlowerBE.setMaxGeneration(this.maxGeneration);
                        newFlowerBE.setFather(false);
                        newFlowerBE.setTickCount(FlowerPathBlock.SPREAD_DELAY);
                    }
                }
            }
        }

        this.father = true;
        this.tickCount = FlowerPathBlock.STAY_TIME + (this.maxGeneration - this.generation) * (FlowerPathBlock.SHRINK_DELAY + FlowerPathBlock.SPREAD_DELAY);
        this.setChanged();
    }

    // Getters y Setters
    public int getGeneration() { return generation; }
    public boolean isFather() { return father; }
    public int getMaxGeneration() { return maxGeneration; }

    public void setGeneration(int generation) {
        this.generation = generation;
        this.setChanged();
    }

    public void setFather(boolean father) {
        this.father = father;
        this.setChanged();
    }

    public void setMaxGeneration(int maxGeneration) {
        this.maxGeneration = maxGeneration;
        this.setChanged();
    }

    public void setTickCount(int tickCount) {
        this.tickCount = tickCount;
        this.setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("Generation", this.generation);
        tag.putBoolean("Father", this.father);
        tag.putInt("MaxGeneration", this.maxGeneration);
        tag.putInt("TickCount", this.tickCount);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.generation = tag.getInt("Generation");
        this.father = tag.getBoolean("Father");
        this.maxGeneration = tag.getInt("MaxGeneration");
        this.tickCount = tag.getInt("TickCount");
    }
}
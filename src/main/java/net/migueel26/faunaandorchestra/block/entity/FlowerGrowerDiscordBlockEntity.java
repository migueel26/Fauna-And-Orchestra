package net.migueel26.faunaandorchestra.block.entity;

import net.migueel26.faunaandorchestra.block.ModBlockEntities;
import net.migueel26.faunaandorchestra.block.ModBlocks;
import net.migueel26.faunaandorchestra.block.custom.FlowerGrowerDiscordBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Iterator;

public class FlowerGrowerDiscordBlockEntity extends BlockEntity {
    private int generation = 0;
    private int maxGeneration = 3;
    private int tickCount = -1;

    public FlowerGrowerDiscordBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FLOWER_DISCORD_BE.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, FlowerGrowerDiscordBlockEntity entity) {
        if (level.isClientSide()) return;

        if (entity.tickCount == -1) {
            entity.tickCount = entity.getNewChildTime(level);
            entity.setChanged();
        }

        if (entity.tickCount > 0) {
            entity.tickCount--;
        } else if (entity.tickCount == 0) {
            entity.tickCount = -2;
            entity.performSpreadLogic(level, pos, state);
        }
    }

    private void performSpreadLogic(Level level, BlockPos pos, BlockState state) {
        if (this.generation < this.maxGeneration) {
            Iterator<BlockPos> iterator = BlockPos.betweenClosed(
                    new BlockPos(pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1),
                    new BlockPos(pos.getX() - 1, pos.getY() - 1, pos.getZ() - 1)).iterator();

            if (this.generation <= 3) {
                level.playSound(null, pos, SoundEvents.SCULK_CLICKING, SoundSource.BLOCKS, 1.0f, 1.0f + (level.random.nextFloat() - 0.5f));
            }

            int time = 0;
            while (iterator.hasNext()) {
                BlockPos nextPos = iterator.next();
                BlockState nextState = level.getBlockState(nextPos);

                if (time % 2 == 0) {
                    if (!nextState.isAir() && FlowerGrowerDiscordBlock.isNotProhibited(nextState) && canHaveChild(this.generation, this.maxGeneration, level)) {

                        level.setBlock(nextPos, ModBlocks.FLOWER_DISCORD_BLOCK.get().defaultBlockState(), 3);
                        if (level.getBlockEntity(nextPos) instanceof FlowerGrowerDiscordBlockEntity childBe) {
                            childBe.setGeneration(this.generation + 1);
                            childBe.setMaxGeneration(this.maxGeneration);
                        }
                    }
                }
                time++;
            }
        }
    }

    private boolean canHaveChild(int myGeneration, int maxGeneration, Level level) {
        int diff = maxGeneration - myGeneration;
        if (diff > 3) {
            return true;
        } else {
            float chance = level.random.nextFloat();
            if (diff == 3) {
                return chance < 0.5f;
            } else if (diff > 0) {
                return chance < 0.25f;
            } else {
                return false;
            }
        }
    }

    private int getNewChildTime(Level level) {
        return level.random.nextInt(5, 10);
    }

    public void setGeneration(int generation) {
        this.generation = generation;
        this.setChanged();
    }

    public void setMaxGeneration(int maxGeneration) {
        if (this.maxGeneration != maxGeneration) {
            this.maxGeneration = maxGeneration;
            this.tickCount = -1;
            this.setChanged();
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("Generation", this.generation);
        tag.putInt("MaxGeneration", this.maxGeneration);
        tag.putInt("TickCount", this.tickCount);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.generation = tag.getInt("Generation");
        this.maxGeneration = tag.getInt("MaxGeneration");
        this.tickCount = tag.getInt("TickCount");
    }
}
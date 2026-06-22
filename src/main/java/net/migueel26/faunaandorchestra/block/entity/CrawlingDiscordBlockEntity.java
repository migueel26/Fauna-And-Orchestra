package net.migueel26.faunaandorchestra.block.entity;

import net.migueel26.faunaandorchestra.block.ModBlockEntities;
import net.migueel26.faunaandorchestra.block.ModBlocks;
import net.migueel26.faunaandorchestra.block.custom.CrawlingDiscordBlock;
import net.migueel26.faunaandorchestra.util.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Iterator;

public class CrawlingDiscordBlockEntity extends BlockEntity {
    private int generation = 0;
    private int maxGeneration = CrawlingDiscordBlock.DEFAULT_MAX_GENERATION;
    private boolean father = false;
    private boolean difficult = false;

    private int actionTimer = -1;

    public CrawlingDiscordBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CRAWLING_DISCORD_BE.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CrawlingDiscordBlockEntity entity) {
        if (level.isClientSide()) return;

        // START THE TIMER
        if (entity.actionTimer == -1) {
            if (entity.father || entity.generation >= entity.maxGeneration) {
                entity.actionTimer = (entity.maxGeneration == CrawlingDiscordBlock.DEFAULT_MAX_GENERATION) ? CrawlingDiscordBlock.DIE_TIME : 100;
            } else {
                entity.actionTimer = entity.getNewChildTime(level);
            }
            entity.setChanged();
        }

        // TIMER TICK
        if (entity.actionTimer > 0) {
            entity.actionTimer--;
        } else if (entity.actionTimer == 0) {
            entity.actionTimer = -2; // PROCESSED
            entity.performSpreadLogic(level, pos, state);
        }
    }

    private void performSpreadLogic(Level level, BlockPos pos, BlockState state) {
        boolean climber = state.getValue(CrawlingDiscordBlock.CLIMBER);

        if (!this.father && this.generation < this.maxGeneration) {
            if (!climber) {
                boolean transformToClimber = false;
                Iterator<BlockPos> iteratorUp = BlockPos.betweenClosed(pos.north().west(), pos.south().east()).iterator();
                Iterator<BlockPos> iteratorDown = BlockPos.betweenClosed(pos.below().north().west(), pos.below().south().east()).iterator();
                int time = 0;

                while (iteratorUp.hasNext()) {
                    BlockPos nextPos = iteratorUp.next();
                    BlockPos downPos = iteratorDown.next();
                    if (time % 2 != 0) {
                        if (level.getBlockState(nextPos).is(ModTags.Blocks.REPLACEABLE_BY_DISCORD) &&
                                !level.getBlockState(nextPos.below()).isAir() &&
                                !level.getBlockState(nextPos.below()).is(ModBlocks.CRAWLING_DISCORD)) {

                            spawnChild(level, nextPos, state.setValue(CrawlingDiscordBlock.CLIMBER, false));
                        }

                        if (level.getBlockState(downPos).is(ModTags.Blocks.REPLACEABLE_BY_DISCORD) &&
                                canGrab(downPos, level) &&
                                level.getBlockState(downPos.above()).isAir()) {

                            spawnChild(level, downPos, state.setValue(CrawlingDiscordBlock.CLIMBER, true));
                        }
                    }
                    time++;
                }

                if (canGrab(pos, level)) transformToClimber = true;

                if (!transformToClimber) {
                    this.father = true;
                    this.actionTimer = (this.maxGeneration == CrawlingDiscordBlock.DEFAULT_MAX_GENERATION) ? CrawlingDiscordBlock.DIE_TIME : 100;
                    this.setChanged();
                } else {
                    level.setBlock(pos, state.setValue(CrawlingDiscordBlock.CLIMBER, true), 3);
                    this.father = false;
                    this.actionTimer = getNewChildTime(level);
                    this.setChanged();
                }

            } else {
                Iterator<BlockPos> iterator = BlockPos.betweenClosed(
                        new BlockPos(pos.getX()+1,pos.getY()+1,pos.getZ()+1),
                        new BlockPos(pos.getX()-1, pos.getY()-1, pos.getZ()-1)).iterator();

                int time = 0;
                while (iterator.hasNext()) {
                    BlockPos nextPos = iterator.next();
                    if (time % 2 == 0) {
                        if (level.getBlockState(nextPos).is(ModTags.Blocks.REPLACEABLE_BY_DISCORD) && canGrab(nextPos, level)) {
                            spawnChild(level, nextPos, state.setValue(CrawlingDiscordBlock.CLIMBER, true));
                        }
                    }
                    time++;
                }

                this.father = true;
                this.actionTimer = (this.maxGeneration == CrawlingDiscordBlock.DEFAULT_MAX_GENERATION) ? CrawlingDiscordBlock.DIE_TIME : 100;
                this.setChanged();

                if (level.getBlockState(pos.above()).is(ModTags.Blocks.REPLACEABLE_BY_DISCORD)) {
                    spawnChild(level, pos.above(), state.setValue(CrawlingDiscordBlock.CLIMBER, false));
                }
            }

        } else {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        }
    }

    private void spawnChild(Level level, BlockPos childPos, BlockState stateToPlace) {
        level.setBlock(childPos, stateToPlace, 3);
        if (level.getBlockEntity(childPos) instanceof CrawlingDiscordBlockEntity childBe) {
            childBe.generation = this.generation + 1;
            childBe.maxGeneration = this.maxGeneration;
            childBe.difficult = this.difficult;
            childBe.actionTimer = childBe.getNewChildTime(level);
            childBe.setChanged();
        }
    }

    private boolean canGrab(BlockPos newPos, Level level) {
        return !level.getBlockState(newPos.east()).isAir() && !level.getBlockState(newPos.east()).is(ModBlocks.CRAWLING_DISCORD) ||
                !level.getBlockState(newPos.west()).isAir() && !level.getBlockState(newPos.west()).is(ModBlocks.CRAWLING_DISCORD) ||
                !level.getBlockState(newPos.north()).isAir() && !level.getBlockState(newPos.north()).is(ModBlocks.CRAWLING_DISCORD) ||
                !level.getBlockState(newPos.south()).isAir() && !level.getBlockState(newPos.south()).is(ModBlocks.CRAWLING_DISCORD);
    }

    public int getNewChildTime(Level level) {
        return difficult ? level.random.nextInt(CrawlingDiscordBlock.DIFFICULT_CHILD_TIME, CrawlingDiscordBlock.DIFFICULT_CHILD_TIME + 7)
                : level.random.nextInt(CrawlingDiscordBlock.NEW_CHILD_TIME, CrawlingDiscordBlock.NEW_CHILD_TIME + 10);
    }

    public void setDifficult(boolean difficult) {
        this.difficult = difficult;
        this.setChanged();
    }

    public void setActionTimer(int time) {
        this.actionTimer = time;
        this.setChanged();
    }

    public void setMaxGeneration(int maxGeneration) {
        this.maxGeneration = maxGeneration;
        this.setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("Generation", this.generation);
        tag.putInt("MaxGeneration", this.maxGeneration);
        tag.putBoolean("Father", this.father);
        tag.putBoolean("Difficult", this.difficult);
        tag.putInt("ActionTimer", this.actionTimer);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.generation = tag.getInt("Generation");
        this.maxGeneration = tag.getInt("MaxGeneration");
        this.father = tag.getBoolean("Father");
        this.difficult = tag.getBoolean("Difficult");
        this.actionTimer = tag.getInt("ActionTimer");
    }
}
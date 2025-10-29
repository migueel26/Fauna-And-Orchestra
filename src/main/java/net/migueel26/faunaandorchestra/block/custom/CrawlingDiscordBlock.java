package net.migueel26.faunaandorchestra.block.custom;

import net.migueel26.faunaandorchestra.block.ModBlocks;
import net.migueel26.faunaandorchestra.util.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Iterator;

public class CrawlingDiscordBlock extends Block {
    public static int MAX_GENERATION = 40;
    public static int NEW_CHILD_TIME = 5;
    public static int DIE_TIME = 200;
    protected static int  DIFFICULT_CHILD_TIME = 2;
    private boolean difficult = false;
    public static final IntegerProperty GENERATION = IntegerProperty.create("generation", 0, MAX_GENERATION);
    public static final BooleanProperty FATHER = BooleanProperty.create("father");
    public static final BooleanProperty CLIMBER = BooleanProperty.create("climber");
    private static final VoxelShape CRAWLER_SHAPE = Block.box(0, 0, 0, 16, 2, 16);
    private static final VoxelShape CLIMBER_SHAPE = Shapes.block();

    public CrawlingDiscordBlock(Properties properties) {
        super(properties);

        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(GENERATION, 0)
                .setValue(FATHER, false)
                .setValue(CLIMBER, false));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(CLIMBER) ? CLIMBER_SHAPE : CRAWLER_SHAPE;
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (entity instanceof LivingEntity) entity.hurt(level.damageSources().magic(), 2.0F);
        if (!level.isClientSide()) {
            ((ServerLevel) level).sendParticles(ParticleTypes.SCULK_SOUL,
                    entity.getX(), entity.getY(), entity.getZ(),
                    3, 0.1f, 0.1f, 0.1f, 0.01f);
        }
        super.stepOn(level, pos, state, entity);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (entity instanceof LivingEntity) entity.hurt(level.damageSources().magic(), 2.0F);
        if (!level.isClientSide()) {
            ((ServerLevel) level).sendParticles(ParticleTypes.SCULK_SOUL,
                    entity.getX(), entity.getY(), entity.getZ(),
                    1, 0.1f, 0.1f, 0.1f, 0.05f);
        }
        super.entityInside(state, level, pos, entity);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (!oldState.is(ModBlocks.CRAWLING_DISCORD)) level.scheduleTick(pos, this, getNewChildTime(level));
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        boolean climber = state.getValue(CLIMBER);
        if (!state.getValue(FATHER) && state.getValue(GENERATION) < MAX_GENERATION) {
            int myGeneration = state.getValue(GENERATION);
            if (!climber) {
                // If it's not a climber
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

                            level.setBlock(nextPos, ModBlocks.CRAWLING_DISCORD.get().defaultBlockState()
                                    .setValue(GENERATION, myGeneration + 1), 3);
                        }

                        if (level.getBlockState(downPos).is(ModTags.Blocks.REPLACEABLE_BY_DISCORD) &&
                                canGrab(downPos, level) &&
                                level.getBlockState(downPos.above()).isAir()) {

                            level.setBlock(downPos, ModBlocks.CRAWLING_DISCORD.get().defaultBlockState()
                                    .setValue(GENERATION, myGeneration + 1).setValue(CLIMBER, true), 3);
                        }

                    }
                    time++;
                }

                if (canGrab(pos, level)) transformToClimber = true;

                if (!transformToClimber) {
                    // We schedule death
                    level.setBlock(pos, state.setValue(FATHER, true).setValue(GENERATION, myGeneration), 3);
                    level.scheduleTick(pos, this, DIE_TIME);
                } else {
                    // We transform it
                    level.setBlock(pos, state.setValue(FATHER, false).setValue(GENERATION, myGeneration).setValue(CLIMBER, true), 3);
                    level.scheduleTick(pos, this, getNewChildTime(level));
                }

            } else {
                // It's a climber
                Iterator<BlockPos> iterator = BlockPos.betweenClosed(
                        new BlockPos(pos.getX()+1,pos.getY()+1,pos.getZ()+1),
                        new BlockPos(pos.getX()-1, pos.getY()-1, pos.getZ()-1)).iterator();

                int time = 0;
                while (iterator.hasNext()) {
                    BlockPos nextPos = iterator.next();
                    if (time % 2 == 0) {
                        if (level.getBlockState(nextPos).is(ModTags.Blocks.REPLACEABLE_BY_DISCORD) && canGrab(nextPos, level)) {
                            level.setBlock(nextPos, ModBlocks.CRAWLING_DISCORD.get().defaultBlockState()
                                    .setValue(GENERATION, myGeneration + 1).setValue(CLIMBER, true), 3);
                        }
                    }

                    time++;
                }

                level.setBlock(pos, state.setValue(FATHER, true).setValue(GENERATION, myGeneration).setValue(CLIMBER, true), 3);
                level.scheduleTick(pos, this, DIE_TIME);
                if (level.getBlockState(pos.above()).is(ModTags.Blocks.REPLACEABLE_BY_DISCORD)) {
                    level.setBlock(pos.above(), ModBlocks.CRAWLING_DISCORD.get().defaultBlockState()
                            .setValue(GENERATION, myGeneration + 1), 3);
                }
            }


        } else {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        }
    }

    private boolean canGrab(BlockPos newPos, ServerLevel level) {
        return !level.getBlockState(newPos.east()).isAir() && !level.getBlockState(newPos.east()).is(ModBlocks.CRAWLING_DISCORD) ||
                !level.getBlockState(newPos.west()).isAir() && !level.getBlockState(newPos.west()).is(ModBlocks.CRAWLING_DISCORD) ||
                !level.getBlockState(newPos.north()).isAir() && !level.getBlockState(newPos.north()).is(ModBlocks.CRAWLING_DISCORD) ||
                !level.getBlockState(newPos.south()).isAir() && !level.getBlockState(newPos.south()).is(ModBlocks.CRAWLING_DISCORD);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(GENERATION, FATHER, CLIMBER);
    }

    private int getNewChildTime(Level level) {
        return difficult ? level.random.nextInt(DIFFICULT_CHILD_TIME, DIFFICULT_CHILD_TIME + 7)
                : level.random.nextInt(NEW_CHILD_TIME, NEW_CHILD_TIME + 10);
    }

    public void setDifficult(boolean difficult) {
        this.difficult = difficult;
    }
}

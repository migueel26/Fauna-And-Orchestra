package net.migueel26.faunaandorchestra.block.custom;

import net.migueel26.faunaandorchestra.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import java.util.Iterator;

public class FlowerGrowerDiscordBlock extends Block {
    public static final IntegerProperty GENERATION = IntegerProperty.create("generation", 0, 100);
    public static final IntegerProperty MAX_GENERATION = IntegerProperty.create("max_generation", 0, 100);

    public FlowerGrowerDiscordBlock(Properties properties) {
        super(properties);

        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(GENERATION, 0)
                .setValue(MAX_GENERATION, 3));
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        level.scheduleTick(pos, this, getNewChildTime(level));
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
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(GENERATION) < state.getValue(MAX_GENERATION)) {
            int myGeneration = state.getValue(GENERATION);
            int maxGeneration = state.getValue(MAX_GENERATION);

            Iterator<BlockPos> iterator = BlockPos.betweenClosed(
                    new BlockPos(pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1),
                    new BlockPos(pos.getX() - 1, pos.getY() - 1, pos.getZ() - 1)).iterator();

            if (myGeneration <= 3) {
                level.playSound(null, pos, SoundEvents.SCULK_CLICKING, SoundSource.BLOCKS, 1.0f, 1.0f + (level.random.nextFloat() - 0.5f) );
            }

            int time = 0;
            while (iterator.hasNext()) {
                BlockPos nextPos = iterator.next();
                BlockState nextState = level.getBlockState(nextPos);
                if (time % 2 == 0) {
                    if (!nextState.isAir() && isNotProhibited(nextState) && canHaveChild(myGeneration, maxGeneration, level)) {
                        level.setBlock(nextPos, ModBlocks.FLOWER_DISCORD_BLOCK.get().defaultBlockState().setValue(GENERATION, myGeneration+1).setValue(MAX_GENERATION, maxGeneration), 3);
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

    public static boolean isNotProhibited(BlockState nextState) {
        return !nextState.is(ModBlocks.COMPOSER_GRAVESTONE) && !nextState.is(ModBlocks.DISCORDED_FLOWER)
                && !nextState.is(Blocks.END_PORTAL) && !nextState.is(Blocks.BEDROCK) && !nextState.is(Blocks.CHEST)
                && !nextState.is(ModBlocks.DISCORD_NUCLEI);
    }

    private int getNewChildTime(Level level) {
        return level.random.nextInt(5, 10);
    }

    private boolean canGrab(BlockPos newPos, ServerLevel level) {
        return !level.getBlockState(newPos.east()).isAir() && !level.getBlockState(newPos.east()).is(ModBlocks.CRAWLING_DISCORD) ||
                !level.getBlockState(newPos.west()).isAir() && !level.getBlockState(newPos.west()).is(ModBlocks.CRAWLING_DISCORD) ||
                !level.getBlockState(newPos.north()).isAir() && !level.getBlockState(newPos.north()).is(ModBlocks.CRAWLING_DISCORD) ||
                !level.getBlockState(newPos.south()).isAir() && !level.getBlockState(newPos.south()).is(ModBlocks.CRAWLING_DISCORD);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(GENERATION, MAX_GENERATION);
    }
}

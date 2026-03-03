package net.migueel26.faunaandorchestra.entity.goals;

import net.migueel26.faunaandorchestra.block.ModBlocks;
import net.migueel26.faunaandorchestra.block.custom.DamBlock;
import net.migueel26.faunaandorchestra.entity.custom.BeaverEntity;
import net.migueel26.faunaandorchestra.particles.ModParticleTypes;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import oshi.util.tuples.Pair;

import java.util.Iterator;
import java.util.Optional;

public class BeaverBuildsDamGoal extends Goal {
    public static final int DEFAULT_COOLDOWN = 200;
    public static final int INITIAL_DEFAULT_COOLDOWN = 200;

    protected final BeaverEntity beaver;
    protected final Level level;
    protected Vec3 wantedPathBlock;
    protected BlockPos waterPos;
    protected int tick;
    protected int cooldown;
    protected boolean finish = false;
    protected boolean isDam = false;
    public BeaverBuildsDamGoal(BeaverEntity beaver, double speedModifier) {
        this.beaver = beaver;
        this.level = beaver.level();
        this.cooldown = INITIAL_DEFAULT_COOLDOWN;
    }

    @Override
    public boolean canUse() {
        cooldown--;
        if (!beaver.isHoldingInstrument() && cooldown <= 0 && beaver.canBuild()) {
            Pair<Vec3, BlockPos> pair = getDamPosition();
            if (pair != null) {
                wantedPathBlock = pair.getA();
                waterPos = pair.getB();
                return true;
            }
        }
        return false;

    }

    @Override
    public boolean canContinueToUse() {
        return !beaver.isDeadOrDying() && !beaver.isHoldingInstrument() && !finish;
    }

    @Override
    public void start() {
        beaver.getNavigation().moveTo(wantedPathBlock.x, wantedPathBlock.y, wantedPathBlock.z, 0, 1.0D);
        this.tick = -1;
        this.finish = false;
    }

    @Override
    public void stop() {
        if (isDam) waterPos = new BlockPos(waterPos.getX(), waterPos.getY()+1, waterPos.getZ());
        if (waterPos.getZ() != wantedPathBlock.z || waterPos.getX() != wantedPathBlock.x) {
            level.setBlock(waterPos, ModBlocks.DAM_BLOCK.get().defaultBlockState().setValue(DamBlock.WATERLOGGED, true), 3);

        }
        waterPos = null;
        wantedPathBlock = null;
        this.finish = false;
        this.tick = -1;
        this.cooldown = DEFAULT_COOLDOWN;
        this.beaver.setBuilding(false);
    }

    private Pair<Vec3, BlockPos> getDamPosition() {
        BlockPos pathBlock = null;
        Optional<BlockPos> water = BlockPos.findClosestMatch(beaver.blockPosition(), 20, 3, this::isWaterApt);
        Optional<BlockPos> dam = BlockPos.findClosestMatch(beaver.blockPosition(), 20, 3, pred -> level.getBlockState(pred).getBlock() == ModBlocks.DAM_BLOCK.get());
        BlockPos waterPos = null;
    if (water.isPresent()
            //&& level.getBiome(water.get()).getKey() == Biomes.RIVER
    ) {
            int x, y, z;

            // Default to water
            waterPos = water.get().immutable();
            x = waterPos.getX();
            y = waterPos.getY();
            z = waterPos.getZ();
            isDam = false;

            if (dam.isPresent()
                    //&& level.getBiome(dam.get()).getKey() == Biomes.RIVER
            ) {
                //isDam = level.getRandom().nextFloat() <= 0.25;
                if (isDam) {
                    // WE (TRY TO) PLACE ON TOP OF DAM
                    waterPos = dam.get().immutable();
                    x = waterPos.getX();
                    y = waterPos.getY();
                    z = waterPos.getZ();
                }
            }

            Iterator<BlockPos> iterator = BlockPos.betweenClosed(new BlockPos(x + 1, y, z + 1), new BlockPos(x - 1, y, z - 1)).iterator();
            while (iterator.hasNext() && pathBlock == null) {
                BlockPos blockPos = iterator.next();
                if (blockPos != waterPos
                        && level.getBlockState(blockPos).getBlock() != Blocks.WATER
                        && level.getBlockState(blockPos.above()).getBlock() == Blocks.AIR) {
                        pathBlock = blockPos.immutable();
                }
            }
        }
        return pathBlock == null ? null : new Pair<>(pathBlock.getCenter(), waterPos);
    }

    private boolean isWaterApt(BlockPos pred) {
        return (level.getBlockState(pred).is(Blocks.WATER)) && (level.getBlockState(pred.above()).is(Blocks.AIR));
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        if (beaver.getNavigation().isDone() && tick == -1) {
            Vec3 tp = new Vec3(wantedPathBlock.x, wantedPathBlock.y + 0.5, wantedPathBlock.z);
            beaver.moveTo(tp);
            beaver.build();
            level.playSound(null, beaver.blockPosition(), ModSounds.BEAVER_TEETH.get(), SoundSource.NEUTRAL, 1.0f, 1.0f);
            tick = 0;
        }

        if (tick >= 0) {
            if (tick % 5 == 0 && level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(
                        new BlockParticleOption(ParticleTypes.BLOCK, Blocks.OAK_LOG.defaultBlockState()),
                        beaver.getX(), beaver.getY() + 0.4, beaver.getZ(), 5, 0.05, 0.05, 0.05, 0.05
                );
            }
            if (tick == 80) {
                finish = true;
            } else {
                tick++;
            }
        }
    }
}

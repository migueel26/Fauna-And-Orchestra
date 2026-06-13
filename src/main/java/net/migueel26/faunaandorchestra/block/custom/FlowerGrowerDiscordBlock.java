package net.migueel26.faunaandorchestra.block.custom;

import com.mojang.serialization.MapCodec;
import net.migueel26.faunaandorchestra.block.ModBlockEntities;
import net.migueel26.faunaandorchestra.block.ModBlocks;
import net.migueel26.faunaandorchestra.block.entity.FlowerGrowerDiscordBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

public class FlowerGrowerDiscordBlock extends BaseEntityBlock {
    private MapCodec<FlowerGrowerDiscordBlock> CODEC = simpleCodec(FlowerGrowerDiscordBlock::new);

    public FlowerGrowerDiscordBlock(Properties properties) {
        super(properties);
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

    public static boolean isNotProhibited(BlockState nextState) {
        return !nextState.is(ModBlocks.COMPOSER_GRAVESTONE) && !nextState.is(ModBlocks.DISCORDED_FLOWER)
                && !nextState.is(Blocks.END_PORTAL) && !nextState.is(Blocks.BEDROCK) && !nextState.is(Blocks.CHEST)
                && !nextState.is(ModBlocks.DISCORD_NUCLEI);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new FlowerGrowerDiscordBlockEntity(blockPos, blockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return type == ModBlockEntities.FLOWER_DISCORD_BE.get() ?
                (BlockEntityTicker<T>) (lvl, p, st, be) -> FlowerGrowerDiscordBlockEntity.tick(lvl, p, st, (FlowerGrowerDiscordBlockEntity) be) : null;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}

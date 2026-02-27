package net.migueel26.faunaandorchestra.block.custom;

import com.mojang.serialization.MapCodec;
import net.migueel26.faunaandorchestra.block.entity.BambooTrapBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BambooTrapBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final MapCodec<BambooTrapBlock> CODEC = simpleCodec(BambooTrapBlock::new);
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    private static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 2, 14);
    public BambooTrapBlock(Properties properties) {
        super(properties);

        this.registerDefaultState(this.getStateDefinition().any().setValue(OPEN, true));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (entity instanceof LivingEntity livingEntity && level.getBlockEntity(pos) instanceof BambooTrapBlockEntity bambooTrap) {
            if (state.getValue(OPEN)) {
                bambooTrap.trap();
                level.setBlock(pos, state.setValue(OPEN, false), 3);
                level.playSound(null, pos, SoundEvents.WOODEN_TRAPDOOR_CLOSE, SoundSource.BLOCKS, 1.0f, 1.0f);
                livingEntity.setDeltaMovement(Vec3.ZERO);

                livingEntity.hurt(livingEntity.damageSources().sweetBerryBush(), 4.0f);
                livingEntity.setDeltaMovement(Vec3.ZERO);
                Vec3 center = pos.getBottomCenter();
                livingEntity.teleportTo(center.x, center.y, center.z);
            }
            entity.makeStuckInBlock(state, new Vec3(0D, 0.05D, 0D));
        }

        level.scheduleTick(pos, this, 20);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (entity instanceof LivingEntity livingEntity && level.getBlockEntity(pos) instanceof BambooTrapBlockEntity bambooTrap) {
            if (state.getValue(OPEN)) {
                bambooTrap.trap();
                level.setBlock(pos, state.setValue(OPEN, false), 3);
                level.playSound(null, pos, SoundEvents.WOODEN_TRAPDOOR_CLOSE, SoundSource.BLOCKS, 1.0f, 1.0f);
                livingEntity.setDeltaMovement(Vec3.ZERO);

                livingEntity.hurt(livingEntity.damageSources().sweetBerryBush(), 4.0f);
                livingEntity.setDeltaMovement(Vec3.ZERO);
                Vec3 center = pos.getBottomCenter();
                livingEntity.teleportTo(center.x, center.y, center.z);
            }
            entity.makeStuckInBlock(state, new Vec3(0D, 0.05D, 0D));;
        }

        level.scheduleTick(pos, this, 20);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.getValue(OPEN)) {
            List<LivingEntity> entitiesInside = level.getEntitiesOfClass(LivingEntity.class, new AABB(pos));

            if (entitiesInside.isEmpty()) {
                if (level.getBlockEntity(pos) instanceof BambooTrapBlockEntity bambooTrap) {
                    bambooTrap.open();
                }
                level.setBlock(pos, state.setValue(OPEN, true), 3);

                level.playSound(null, pos, SoundEvents.WOODEN_TRAPDOOR_OPEN, SoundSource.BLOCKS, 1.0f, 1.0f);

            }
        }
        super.tick(state, level, pos, random);
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }


    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new BambooTrapBlockEntity(blockPos, blockState);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, OPEN);
    }
}

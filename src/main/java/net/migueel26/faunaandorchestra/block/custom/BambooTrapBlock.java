package net.migueel26.faunaandorchestra.block.custom;

import com.mojang.serialization.MapCodec;
import net.migueel26.faunaandorchestra.block.entity.BambooTrapBlockEntity;
import net.migueel26.faunaandorchestra.entity.custom.LivingMusicEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
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
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (entity instanceof LivingEntity livingEntity && level.getBlockEntity(pos) instanceof BambooTrapBlockEntity bambooTrap) {
            if (state.getValue(OPEN)) {
                Vec3 center = pos.getBottomCenter();

                bambooTrap.trap();
                level.setBlock(pos, state.setValue(OPEN, false), 3);
                level.playSound(null, pos, SoundEvents.WOODEN_TRAPDOOR_CLOSE, SoundSource.BLOCKS, 1.0f, 1.0f);
                if (!level.isClientSide()) {
                    ((ServerLevel) level).sendParticles(ParticleTypes.CLOUD, center.x, center.y+0.3f, center.z, 10, 0.1, 0.1, 0.1, 0.05);
                }
                livingEntity.setDeltaMovement(Vec3.ZERO);

                livingEntity.hurt(livingEntity.damageSources().sweetBerryBush(), 4.0f);
                livingEntity.setDeltaMovement(Vec3.ZERO);

                livingEntity.teleportTo(center.x, center.y, center.z);

                level.scheduleTick(pos, this, 20);

                if (livingEntity instanceof LivingMusicEntity livingMusic) {
                    // We trap it so it doesn't despawn
                    livingMusic.setTicksUntilDeath(-1);
                }
            }
            entity.makeStuckInBlock(state, new Vec3(0D, 0.05D, 0D));;
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.getValue(OPEN)) {
            List<LivingEntity> entitiesInside = level.getEntitiesOfClass(LivingEntity.class,
                    AABB.ofSize(pos.getBottomCenter(), 0.75f, 0.75f, 0.75f));

            if (entitiesInside.isEmpty()) {
                if (level.getBlockEntity(pos) instanceof BambooTrapBlockEntity bambooTrap) {
                    bambooTrap.open();
                }
                level.setBlock(pos, state.setValue(OPEN, true), 3);

                level.playSound(null, pos, SoundEvents.WOODEN_TRAPDOOR_OPEN, SoundSource.BLOCKS, 1.0f, 1.0f);

            } else {
                level.scheduleTick(pos, this, 10);
            }
        }
        super.tick(state, level, pos, random);
    }

    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        return !state.canSurvive(level, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return !state.getCollisionShape(level, pos).getFaceShape(Direction.UP).isEmpty() || state.isFaceSturdy(level, pos, Direction.UP);
    }

    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos blockpos = pos.below();
        return this.mayPlaceOn(level.getBlockState(blockpos), level, blockpos);
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

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getHorizontalDirection().getOpposite();
        return this.defaultBlockState().setValue(FACING, direction);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, OPEN);
    }
}

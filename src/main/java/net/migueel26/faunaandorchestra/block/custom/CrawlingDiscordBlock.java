package net.migueel26.faunaandorchestra.block.custom;

import net.migueel26.faunaandorchestra.block.ModBlockEntities;
import net.migueel26.faunaandorchestra.block.ModBlocks;
import net.migueel26.faunaandorchestra.block.entity.CrawlingDiscordBlockEntity;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Iterator;

public class CrawlingDiscordBlock extends BaseEntityBlock {
    public static int NEW_CHILD_TIME = 5;
    public static int DIE_TIME = 200;
    public static final int DEFAULT_MAX_GENERATION = 40;
    public static int  DIFFICULT_CHILD_TIME = 2;

    public static final BooleanProperty CLIMBER = BooleanProperty.create("climber");
    private static final VoxelShape CRAWLER_SHAPE = Block.box(0, 0, 0, 16, 2, 16);
    private static final VoxelShape CLIMBER_SHAPE = Shapes.block();

    public CrawlingDiscordBlock(Properties properties) {
        super(properties);

        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(CLIMBER, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
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
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (entity instanceof LivingEntity) entity.hurt(level.damageSources().magic(), 2.0F);
        if (!level.isClientSide()) {
            ((ServerLevel) level).sendParticles(ParticleTypes.SCULK_SOUL,
                    entity.getX(), entity.getY(), entity.getZ(),
                    1, 0.1f, 0.1f, 0.1f, 0.05f);
        }
        super.entityInside(state, level, pos, entity);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return type == ModBlockEntities.CRAWLING_DISCORD_BE.get() ?
                (BlockEntityTicker<T>) (lvl, pos, st, be) -> CrawlingDiscordBlockEntity.tick(lvl, pos, st, (CrawlingDiscordBlockEntity) be) : null;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CLIMBER);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new CrawlingDiscordBlockEntity(blockPos, blockState);
    }
}

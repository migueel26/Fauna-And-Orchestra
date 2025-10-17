package net.migueel26.faunaandorchestra.block.custom;

import com.mojang.serialization.MapCodec;
import net.migueel26.faunaandorchestra.block.ModBlocks;
import net.migueel26.faunaandorchestra.entity.ModEntities;
import net.migueel26.faunaandorchestra.entity.custom.ButterflyEntity;
import net.migueel26.faunaandorchestra.entity.custom.MadameButterflyEntity;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.particles.ModParticleTypes;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class AltarOfTheButterfliesBlock extends HorizontalDirectionalBlock {
    public static final MapCodec<AltarOfTheButterfliesBlock> CODEC = simpleCodec(AltarOfTheButterfliesBlock::new);
    public static final BooleanProperty OFFERING = BooleanProperty.create("offering");
    public static final VoxelShape SHAPE = Shapes.or(
            Block.box(1.5f, 0f, 1.5f, 14.5f, 4f, 14.5f),
            Block.box(3.75f, 4f, 3.75f, 12.25f, 12f, 12.25f),
            Block.box(2.75f, 12f, 2.75f, 13.25f, 16f, 13.25f)
    );
    public AltarOfTheButterfliesBlock(Properties properties) {
        super(properties);

        this.registerDefaultState(this.getStateDefinition().any().setValue(OFFERING, false));
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.is(ModItems.OFFERING) && !state.getValue(OFFERING) && level.getBlockState(pos.above()).isAir()) {
            stack.consume(1, player);
            level.setBlock(pos, state.setValue(OFFERING, true), 3);
            if (!level.isClientSide()) {
                ((ServerLevel) level).sendParticles(ParticleTypes.WAX_OFF, pos.getCenter().x, pos.above().getY(), pos.getCenter().z,
                        20, 0.2, 0.2, 0.2, 0.05);
            }
            level.playSound(player, pos.above(), SoundEvents.ARMOR_EQUIP_LEATHER.value(), SoundSource.NEUTRAL, 2f, 1.5f);
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(OFFERING)) {
            List<ButterflyEntity> butterflies = level.getEntitiesOfClass(ButterflyEntity.class,
                    AABB.ofSize(pos.getCenter(), 10, 10, 10));
            float size = butterflies.size();

            if (size >= 5 && level.random.nextFloat() <= size / 80.0f) {

                MadameButterflyEntity madameButterfly = new MadameButterflyEntity(ModEntities.MADAME_BUTTERFLY.get(), level);
                madameButterfly.setMusical();
                madameButterfly.setOrderedToSit(true);
                madameButterfly.moveTo(pos.above().getCenter());
                madameButterfly.setYHeadRot(getYRot(state.getValue(FACING)));
                madameButterfly.setYBodyRot(madameButterfly.getYRot());

                level.playSound(null, pos, ModSounds.SPROUTLING_TWINKLE.get(), SoundSource.NEUTRAL);
                ((ServerLevel) level).sendParticles(ModParticleTypes.STAR.get(),
                        pos.getCenter().x, pos.above().getY()+0.25f, pos.getCenter().z,
                        10, 0.1f, 0.1f, 0.1f, 0.025f);

                level.setBlock(pos, state.setValue(OFFERING, false), 3);

                level.addFreshEntity(madameButterfly);
            }
        }
        super.randomTick(state, level, pos, random);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.getValue(OFFERING) && !newState.is(ModBlocks.ALTAR_OF_THE_BUTTERFLIES)) {
            popResourceFromFace(level, pos, Direction.UP, new ItemStack(ModItems.OFFERING.get()));
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getHorizontalDirection();
        BlockPos blockpos = context.getClickedPos();
        BlockPos blockpos1 = blockpos.relative(direction);
        Level level = context.getLevel();
        return level.getBlockState(blockpos1).canBeReplaced(context) && level.getWorldBorder().isWithinBounds(blockpos1)
                ? this.defaultBlockState().setValue(FACING, direction.getOpposite())
                : null;
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    private float getYRot(Direction facing) {
        return switch (facing) {
            case NORTH -> 180f;
            case SOUTH -> 0f;
            case WEST  -> 90f;
            case EAST  -> -90f;
            default -> 0f;
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, OFFERING);
    }
}

package net.migueel26.faunaandorchestra.block.custom;

import com.mojang.serialization.MapCodec;
import net.migueel26.faunaandorchestra.block.entity.SingingCropBlockEntity;
import net.migueel26.faunaandorchestra.entity.ModEntities;
import net.migueel26.faunaandorchestra.entity.custom.SproutlingEntity;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.particles.ModParticleTypes;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SingingCropBlock extends BushBlock implements EntityBlock, BonemealableBlock {
    public static int MAX_AGE = 3;
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 3);
    public static final BooleanProperty FINAL = BooleanProperty.create("final");
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final VoxelShape[] SHAPE_BY_AGE =
            new VoxelShape[]{
                    Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0),
                    Block.box(0.0, 0.0, 0.0, 16.0, 4.0, 16.0),
                    Block.box(0.0, 0.0, 0.0, 16.0, 6.0, 16.0),
                    Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0)};

    public SingingCropBlock(Properties properties) {
        super(properties);

        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(AGE, 0)
                .setValue(FINAL, false));
    }

    public int getAge(BlockState state) {
        return state.getValue(this.getAgeProperty());
    }

    public final boolean isMaxAge(BlockState state) {
        return this.getAge(state) >= this.getMaxAge();
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return !this.isMaxAge(state);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.isAreaLoaded(pos, 1)) return;
        if (level.getRawBrightness(pos, 0) >= 9) {
            int i = this.getAge(state);
            if (i < this.getMaxAge()) {
                float f = getGrowthSpeed(state, level, pos);
                if (ForgeHooks.onCropsGrowPre(level, pos, state, random.nextInt((int)(25.0F / f) + 1) == 0)) {
                    level.setBlock(pos, this.getStateForAge(i + 1, state.getValue(FACING)), 2);
                    ForgeHooks.onCropsGrowPost(level, pos, state);
                }
            }
        }
    }

    public void growCrops(Level level, BlockPos pos, BlockState state) {
        int i = this.getAge(state) + this.getBonemealAgeIncrease(level);
        int j = this.getMaxAge();
        if (i > j) {
            i = j;
        }

        level.setBlock(pos, this.getStateForAge(i, state.getValue(FACING)), 2);
    }

    protected int getBonemealAgeIncrease(Level level) {
        return Mth.nextInt(level.random, 2, 5);
    }

    public BlockState getStateForAge(int age, Direction direction) {
        return this.defaultBlockState().setValue(this.getAgeProperty(), Integer.valueOf(age))
                .setValue(FACING, direction);
    }


    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getBlock() instanceof net.minecraft.world.level.block.FarmBlock;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE_BY_AGE[state.getValue(AGE)];
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack stack = player.getItemInHand(hand);

        if (stack.is(ModItems.GLOVE.get())
                && state.getValue(AGE) == 3
                && level.getBlockEntity(pos) instanceof SingingCropBlockEntity blockEntity) {
            blockEntity.triggerAnim("singing_crop_controller", "emerge");
            level.scheduleTick(pos, this, 30);
            stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.FAIL;
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.getValue(FINAL)) {
            if (!level.isClientSide()) {
                ((ServerLevel) level).sendParticles(ModParticleTypes.STAR.get(),
                        pos.getCenter().x, pos.getY()+0.45, pos.getCenter().z,
                        10, 0.1f, 0.1f, 0.1f, 0.025f);
                level.playSound(null, pos, ModSounds.TWINKLE.get(), SoundSource.NEUTRAL);
            }
            level.scheduleTick(pos, this, 20);
            level.setBlock(pos, state.setValue(FINAL, true), 3);
        } else {
            SproutlingEntity entity = new SproutlingEntity(ModEntities.SINGING_SPROUTLING.get(), level);

            entity.moveTo(pos, 0f, 0f);
            entity.setYHeadRot(getYRot(state.getValue(FACING)));
            entity.setYBodyRot(entity.getYRot());
            level.addFreshEntity(entity);

            level.destroyBlock(pos, false);
            super.tick(state, level, pos, random);
        }

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

    protected static float getGrowthSpeed(BlockState blockState, BlockGetter level, BlockPos pos) {
        Block cropBlock = blockState.getBlock();
        float f = 1.0F;
        BlockPos blockpos = pos.below();

        for(int i = -1; i <= 1; ++i) {
            for(int j = -1; j <= 1; ++j) {
                float f1 = 0.0F;
                BlockState soilState = level.getBlockState(blockpos.offset(i, 0, j));

                // CAMBIO 1: canSustainPlant devuelve boolean en 1.20.1
                // CAMBIO 2: El último argumento debe ser IPlantable.
                // Como 'cropBlock' es tu cultivo, debe implementar IPlantable (los cultivos lo hacen por defecto).
                boolean canSustain = false;

                if (cropBlock instanceof IPlantable plantable) {
                    canSustain = soilState.canSustainPlant(level, blockpos.offset(i, 0, j), net.minecraft.core.Direction.UP, plantable);
                }

                // Lógica simplificada: Si puede sostener la planta, calculamos fertilidad
                if (canSustain) {
                    f1 = 1.0F;
                    // isFertile existe en Forge 1.20.1
                    if (soilState.isFertile(level, blockpos.offset(i, 0, j))) {
                        f1 = 3.0F;
                    }
                }

                if (i != 0 || j != 0) {
                    f1 /= 4.0F;
                }

                f += f1;
            }
        }

        // --- El resto de la lógica de vecinos es Vanilla estándar y funciona igual ---
        BlockPos blockpos1 = pos.north();
        BlockPos blockpos2 = pos.south();
        BlockPos blockpos3 = pos.west();
        BlockPos blockpos4 = pos.east();
        boolean flag = level.getBlockState(blockpos3).is(cropBlock) || level.getBlockState(blockpos4).is(cropBlock);
        boolean flag1 = level.getBlockState(blockpos1).is(cropBlock) || level.getBlockState(blockpos2).is(cropBlock);

        if (flag && flag1) {
            f /= 2.0F;
        } else {
            boolean flag2 = level.getBlockState(blockpos3.north()).is(cropBlock)
                    || level.getBlockState(blockpos4.north()).is(cropBlock)
                    || level.getBlockState(blockpos4.south()).is(cropBlock)
                    || level.getBlockState(blockpos3.south()).is(cropBlock);
            if (flag2) {
                f /= 2.0F;
            }
        }

        return f;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getHorizontalDirection().getOpposite();
        return this.defaultBlockState().setValue(FACING, direction);
    }

    public static boolean hasSufficientLight(LevelReader level, BlockPos pos) {
        return level.getRawBrightness(pos, 0) >= 8;
    }
    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos posBelow = pos.below();
        BlockState soilState = level.getBlockState(posBelow);

        if (state.getBlock() instanceof IPlantable plantable) {
            if (soilState.canSustainPlant(level, posBelow, Direction.UP, plantable)) {
                return hasSufficientLight(level, pos);
            }
        }

        return super.canSurvive(state, level, pos);
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (entity instanceof Ravager && ForgeEventFactory.getMobGriefingEvent(level, entity)) {
            level.destroyBlock(pos, true, entity);
        }

        super.entityInside(state, level, pos, entity);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        return new ItemStack(this.getBaseSeedId());
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean b) {
        return !this.isMaxAge(state);
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        this.growCrops(level, pos, state);
    }

    /**
     * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed blockstate.
     */
    @Override
    public @NotNull BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    /**
     * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed blockstate.
     */
    @Override
    public @NotNull BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    protected ItemLike getBaseSeedId() {
        return ModItems.SINGING_SEED.get();
    }

    public IntegerProperty getAgeProperty() {
        return AGE;
    }

    public int getMaxAge() {
        return MAX_AGE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE, FINAL, FACING);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SingingCropBlockEntity(pos, state);
    }
}

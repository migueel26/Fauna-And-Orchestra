package net.migueel26.faunaandorchestra.block.custom;

import net.migueel26.faunaandorchestra.entity.ModEntities;
import net.migueel26.faunaandorchestra.entity.custom.MantisEntity;
import net.migueel26.faunaandorchestra.entity.custom.variants.MantisVariant;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nullable;

public class MantisEggBlock extends Block {
    public static final int MAX_HATCH_LEVEL = 2;
    public static final int MIN_EGGS = 1;
    public static final int MAX_EGGS = 4;
    private static final VoxelShape ONE_EGG_AABB = Block.box(3.0, 0.0, 3.0, 12.0, 7.0, 12.0);
    private static final VoxelShape MULTIPLE_EGGS_AABB = Block.box(1.0, 0.0, 1.0, 15.0, 7.0, 15.0);
    public static final IntegerProperty HATCH;
    public static final IntegerProperty EGGS;
    public static final IntegerProperty FATHER_VARIANT;
    public static final IntegerProperty MOTHER_VARIANT;
    public static final BooleanProperty FATHER_MUSICAL;
    public static final BooleanProperty MOTHER_MUSICAL;

    public MantisEggBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(HATCH, 0)
                .setValue(EGGS, 1)
                .setValue(FATHER_VARIANT, MantisVariant.NORMAL.getId())
                .setValue(MOTHER_VARIANT, MantisVariant.NORMAL.getId())
                .setValue(FATHER_MUSICAL, false)
                .setValue(MOTHER_MUSICAL, false));
    }

    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (!entity.isSteppingCarefully()) {
            this.destroyEgg(level, state, pos, entity, 100);
        }

        super.stepOn(level, pos, state, entity);
    }

    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        if (!(entity instanceof Zombie)) {
            this.destroyEgg(level, state, pos, entity, 3);
        }

        super.fallOn(level, state, pos, entity, fallDistance);
    }

    private void destroyEgg(Level level, BlockState state, BlockPos pos, Entity entity, int chance) {
        if (this.canDestroyEgg(level, entity) && !level.isClientSide && level.random.nextInt(chance) == 0 && state.is(this)) {
            this.decreaseEggs(level, pos, state);
        }

    }

    private void decreaseEggs(Level level, BlockPos pos, BlockState state) {
        level.playSound(null, pos, SoundEvents.TURTLE_EGG_BREAK, SoundSource.BLOCKS, 0.7F, 0.9F + level.random.nextFloat() * 0.2F);
        int i = state.getValue(EGGS);
        if (i <= 1) {
            level.destroyBlock(pos, false);
        } else {
            level.setBlock(pos, state.setValue(EGGS, i - 1), 2);
            level.gameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Context.of(state));
            level.levelEvent(2001, pos, Block.getId(state));
        }

    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (this.shouldUpdateHatchLevel(level) && onLand(level, pos)) {
            int i = state.getValue(HATCH);
            if (i < 2) {
                level.playSound(null, pos, SoundEvents.TURTLE_EGG_CRACK, SoundSource.BLOCKS, 0.7F, 0.9F + random.nextFloat() * 0.2F);
                level.setBlock(pos, state.setValue(HATCH, i + 1), 2);
                level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(state));
            } else {
                level.playSound(null, pos, SoundEvents.TURTLE_EGG_HATCH, SoundSource.BLOCKS, 0.7F, 0.9F + random.nextFloat() * 0.2F);
                level.removeBlock(pos, false);
                level.gameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Context.of(state));

                for(int j = 0; j < state.getValue(EGGS); ++j) {
                    level.levelEvent(2001, pos, Block.getId(state));
                    MantisEntity mantis = createMantisFromEgg(state, level);
                    if (mantis != null) {
                        mantis.setAge(-24000);
                        mantis.moveTo((double)pos.getX() + 0.3 + (double)j * 0.2, pos.getY(), (double)pos.getZ() + 0.3, 0.0F, 0.0F);
                        level.addFreshEntity(mantis);
                    }
                }
            }
        }

    }

    public MantisEntity createMantisFromEgg(BlockState state, Level level) {
        MantisVariant fatherVariant = MantisVariant.byId(state.getValue(FATHER_VARIANT));
        MantisVariant motherVariant = MantisVariant.byId(state.getValue(MOTHER_VARIANT));
        boolean fatherMusical = state.getValue(FATHER_MUSICAL);
        boolean motherMusical = state.getValue(MOTHER_MUSICAL);

        boolean isMusical;
        MantisVariant variant;

        isMusical = fatherMusical != motherMusical ? level.getRandom().nextFloat() <= 0.25f : motherMusical;
        if (fatherVariant != motherVariant) {
            variant = level.getRandom().nextFloat() <= 0.25f ? MantisVariant.ORCHID : MantisVariant.NORMAL;
        } else {
            variant = motherVariant;
        }

        MantisEntity mantis = ModEntities.MANTIS.get().create(level);
        if (mantis != null) {
            mantis.setMusical(isMusical);
            mantis.setVariant(variant);
        }

        return mantis;
    }

    public static boolean onLand(BlockGetter level, BlockPos pos) {
        return isLand(level, pos.below());
    }

    public static boolean isLand(BlockGetter reader, BlockPos pos) {
        return reader.getBlockState(pos).is(BlockTags.FROGS_SPAWNABLE_ON);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (onLand(level, pos) && !level.isClientSide) {
            level.levelEvent(2012, pos, 15);
        }

    }

    private boolean shouldUpdateHatchLevel(Level level) {
        float f = level.getTimeOfDay(1.0F);
        return (double)f < 0.69 && (double)f > 0.65 ? true : level.random.nextInt(500) == 0;
    }

    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity te, ItemStack stack) {
        super.playerDestroy(level, player, pos, state, te, stack);
        this.decreaseEggs(level, pos, state);
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext useContext) {
        return !useContext.isSecondaryUseActive() && useContext.getItemInHand().is(this.asItem()) && state.getValue(EGGS) < 4 ? true : super.canBeReplaced(state, useContext);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState blockstate = context.getLevel().getBlockState(context.getClickedPos());
        return blockstate.is(this) ? blockstate.setValue(EGGS, Math.min(4, blockstate.getValue(EGGS) + 1)) : super.getStateForPlacement(context);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(EGGS) > 1 ? MULTIPLE_EGGS_AABB : ONE_EGG_AABB;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HATCH, EGGS, FATHER_MUSICAL, MOTHER_MUSICAL, FATHER_VARIANT, MOTHER_VARIANT);
    }

    private boolean canDestroyEgg(Level level, Entity entity) {
        if (!(entity instanceof Turtle) && !(entity instanceof Bat)) {
            return !(entity instanceof LivingEntity) ? false : entity instanceof Player || ForgeEventFactory.getMobGriefingEvent(level, entity);
        } else {
            return false;
        }
    }

    static {
        HATCH = BlockStateProperties.HATCH;
        EGGS = BlockStateProperties.EGGS;
        FATHER_VARIANT = IntegerProperty.create("father_variant", 0, MantisVariant.values().length);
        MOTHER_VARIANT = IntegerProperty.create("mother_variant", 0, MantisVariant.values().length);
        FATHER_MUSICAL = BooleanProperty.create("father_musical");
        MOTHER_MUSICAL = BooleanProperty.create("mother_musical");
    }
}

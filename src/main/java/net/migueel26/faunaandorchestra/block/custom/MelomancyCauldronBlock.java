package net.migueel26.faunaandorchestra.block.custom;

import com.mojang.serialization.MapCodec;
import net.migueel26.faunaandorchestra.advancements.ModAdvancements;
import net.migueel26.faunaandorchestra.block.ModBlockEntities;
import net.migueel26.faunaandorchestra.block.entity.MelomancyCauldronBlockEntity;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.particles.ModParticleTypes;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.migueel26.faunaandorchestra.util.RecipesUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class MelomancyCauldronBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final MapCodec<MelomancyCauldronBlock> CODEC = simpleCodec(MelomancyCauldronBlock::new);
    public static final IntegerProperty LIQUID = IntegerProperty.create("liquid", 0, 3);
    public static final BooleanProperty COOKING = BooleanProperty.create("cooking");
    public VoxelShape FLOOR = Block.box(2.5, 4.0, 2.525, 13.5, 5.0, 13.25);
    public VoxelShape EAST_SIDE = Block.box(2.5, 4.0, 2.525, 3.5, 11.75, 13.25);
    public VoxelShape WEST_SIDE = Block.box(12.5, 4.0, 2.525, 13.5, 11.75, 13.25);
    public VoxelShape NORTH_SIDE = Block.box(2.5, 4.0, 2.525, 12.5, 11.75, 3.525);
    public VoxelShape NORTH_TOP_SIDE = Block.box(1.65, 11.75, 1.65, 14.35, 14.45, 2.55);
    public VoxelShape SOUTH_TOP_SIDE = Block.box(1.65, 11.75, 13.25, 14.35, 14.45, 14.25);
    public VoxelShape EAST_TOP_SIDE = Block.box(1.65, 11.75, 1.65, 2.65, 14.45, 14.25);
    public VoxelShape WEST_TOP_SIDE = Block.box(13.35, 11.75, 1.65, 14.35, 14.45, 14.25);

    public VoxelShape SOUTH_SIDE = Block.box(2.5, 4.0, 12.25, 13.5, 11.75, 13.25);
    public VoxelShape SHAPE = Shapes.or(FLOOR, EAST_SIDE, WEST_SIDE, NORTH_SIDE, SOUTH_SIDE,
            EAST_TOP_SIDE, WEST_TOP_SIDE, NORTH_TOP_SIDE, SOUTH_TOP_SIDE);

    public MelomancyCauldronBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(LIQUID, 0)
                .setValue(COOKING, false));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        int liquid = state.getValue(LIQUID);
        if (level.getBlockEntity(pos) instanceof MelomancyCauldronBlockEntity melomancyCauldronBE) {

            if (stack.is(Items.BUCKET) && liquid > 0) {
                // If the player wants to clear the cauldron
                melomancyCauldronBE.clearContent();
                level.scheduleTick(pos, this, 20);
                level.playSound(null,
                        pos.getX(), pos.getY(), pos.getZ(),
                        SoundEvents.BUCKET_FILL, SoundSource.NEUTRAL, 1.0F, 1.0F - level.random.nextFloat()/2);
                return ItemInteractionResult.SUCCESS;

            } else if (melomancyCauldronBE.hasFinishedCooking()) {
                // If the player wants to take the item
                if (RecipesUtil.isCorrectItem(stack, melomancyCauldronBE)) {
                    player.addItem(melomancyCauldronBE.getResult());
                    stack.consume(1, player);
                    if (!level.isClientSide()) {
                        ModAdvancements.USE_MELOMANCY_CAULDRON.trigger((ServerPlayer) player);
                    }

                    if (melomancyCauldronBE.getMixResult().equalsIgnoreCase("resurrection")) {
                        level.playSound(player,
                                pos.getX(), pos.getY(), pos.getZ(),
                                SoundEvents.WARDEN_EMERGE, SoundSource.BLOCKS);
                        if (!level.isClientSide()) {
                            ((ServerLevel) level).sendParticles(ParticleTypes.SCULK_SOUL, pos.getCenter().x, pos.getY()+0.75f, pos.getCenter().z,
                                    40, 0.2, 0.2, 0.2, 0.3);
                        }
                    } else {
                        level.playSound(player,
                                pos.getX(), pos.getY(), pos.getZ(),
                                ModSounds.CAULDRON_ITEM.get(), SoundSource.BLOCKS, 1.0F, 0.75F + level.random.nextFloat()/2);
                    }

                    melomancyCauldronBE.clearContent(false);
                    level.setBlock(pos, state.setValue(LIQUID, 0).setValue(COOKING, false), 3);

                    return ItemInteractionResult.SUCCESS;
                } else {
                    return ItemInteractionResult.FAIL;
                }


            } else if (!state.getValue(COOKING)) {
                // If it's NOT cooking
                if (stack.is(ModItems.MUSIC_BOTTLE) && liquid < 3) {
                    // If the player wants to fill the cauldron
                    stack.consume(1, player);
                    player.addItem(new ItemStack(Items.GLASS_BOTTLE));
                    level.setBlock(pos, state.setValue(LIQUID, liquid + 1), 3);
                    level.playSound(null,
                            pos.getX(), pos.getY(), pos.getZ(),
                            SoundEvents.BUCKET_EMPTY, SoundSource.NEUTRAL, 1.0F, 1.0F - level.random.nextFloat()/2);
                    return ItemInteractionResult.SUCCESS;

                } else if (liquid == 3) {
                    ItemStack itemstack = player.getItemInHand(hand);

                    // If it's an ingredient to add
                    if (!level.isClientSide && !stack.is(ModItems.MUSIC_BOTTLE) && !stack.isEmpty() &&
                            melomancyCauldronBE.addIngredient(player, itemstack, hand)) {
                        level.playSound(null,
                                pos.getX(), pos.getY(), pos.getZ(),
                                SoundEvents.LAVA_POP, SoundSource.NEUTRAL, 1.0F, 1.0F - level.random.nextFloat()/2);
                        ((ServerLevel) level).sendParticles(ModParticleTypes.MAGICAL_NOTE.get(), pos.getCenter().x, pos.getY() + 0.75f, pos.getCenter().z, 5, 0.4, 0.1, 0.4, 0);
                        return ItemInteractionResult.SUCCESS;

                    } else if (stack.isEmpty()) {
                        // If the hand is empty, we mix
                        if (!melomancyCauldronBE.isCooking() && melomancyCauldronBE.cook()) {
                            level.setBlock(pos, state.setValue(COOKING, true), 3);
                            return ItemInteractionResult.SUCCESS;
                        }
                    }

                    return ItemInteractionResult.CONSUME;
                }

            }

        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        level.setBlock(pos, state.setValue(LIQUID, 0).setValue(COOKING, false), 3);
        super.tick(state, level, pos, random);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(COOKING)) {
            if (random.nextInt(10) == 0) {
                level.playLocalSound(
                        (double)pos.getX() + 0.5,
                        (double)pos.getY() + 0.5,
                        (double)pos.getZ() + 0.5,
                        ModSounds.CAULDRON_BUBBLING.get(),
                        SoundSource.BLOCKS,
                        0.5F + random.nextFloat(),
                        random.nextFloat() * 0.7F + 0.6F,
                        false
                );
            }
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide) {
            return createTickerHelper(blockEntityType, ModBlockEntities.MELOMANCY_CAULDRON_BE.get(), MelomancyCauldronBlockEntity::particleTick);
        } else {
            return state.getValue(COOKING) ? createTickerHelper(blockEntityType, ModBlockEntities.MELOMANCY_CAULDRON_BE.get(), MelomancyCauldronBlockEntity::cookTick)
                    : null;
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getHorizontalDirection().getOpposite();
        return this.defaultBlockState().setValue(FACING, direction);
    }

    @javax.annotation.Nullable
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(
            BlockEntityType<A> serverType, BlockEntityType<E> clientType, BlockEntityTicker<? super E> ticker
    ) {
        return clientType == serverType ? (BlockEntityTicker<A>) ticker : null;
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MelomancyCauldronBlockEntity(pos, state);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIQUID, COOKING);
    }
}

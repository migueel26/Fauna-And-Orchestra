package net.migueel26.faunaandorchestra.block.custom;

import com.mojang.serialization.MapCodec;
import net.migueel26.faunaandorchestra.block.ModBlocks;
import net.migueel26.faunaandorchestra.block.entity.JarRackBlockEntity;
import net.migueel26.faunaandorchestra.util.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class JarRackBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final MapCodec<JarRackBlock> CODEC = simpleCodec(JarRackBlock::new);
    public static final BooleanProperty JAR = BooleanProperty.create("jar");
    protected static VoxelShape NORTH_SHAPE = Shapes.or(
            Block.box(7, 7, 5, 9, 9, 16),
            Block.box(6.25, 6.25, 15, 9.75, 9.75, 16)
    );
    protected static final VoxelShape SOUTH_SHAPE = Shapes.or(
            Block.box(7, 7, 0, 9, 9, 11),
            Block.box(6.25, 6.25, 0, 9.75, 9.75, 1)
    );
    protected static final VoxelShape EAST_SHAPE = Shapes.or(
            Block.box(0, 7, 7, 11, 9, 9),
            Block.box(0, 6.25, 6.25, 1, 9.75, 9.75)
    );
    protected static final VoxelShape WEST_SHAPE = Shapes.or(
            Block.box(5, 7, 7, 16, 9, 9),
            Block.box(15, 6.25, 6.25, 16, 9.75, 9.75)
    );
    public JarRackBlock(Properties properties) {
        super(properties);

        this.registerDefaultState(this.getStateDefinition().any().setValue(JAR, false).setValue(FACING, Direction.NORTH));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case NORTH -> NORTH_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
            case EAST -> EAST_SHAPE;
            default -> WEST_SHAPE;
        };
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.is(ModBlocks.HANGING_JAR.asItem()) && level.getBlockEntity(pos) instanceof JarRackBlockEntity blockEntity) {
            if (level.getBlockState(pos.below()).is(ModTags.Blocks.JAR_FUEL)) {
                CompoundTag tag = stack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY).copyTag();
                if (tag.contains("Inventory")) {
                    // We introduce the items
                    ItemStackHandler inventory = new ItemStackHandler(6);
                    inventory.deserializeNBT(level.registryAccess(), tag.getCompound("Inventory"));
                    blockEntity.setInventory(inventory);
                }
                // We eliminate the jar from the inventory
                stack.consume(1, player);
                player.playSound(SoundEvents.ARMOR_EQUIP_LEATHER.value(), 1.5f, 1.0f + ((level.random.nextFloat()/2)-0.25f));

                // We replace the current block
                level.setBlock(pos, state.setValue(JAR, true), 3);

                return ItemInteractionResult.SUCCESS;
            } else {
                player.displayClientMessage(Component.translatable("block.faunaandorchestra.jar_rack.not_placed"), true);
                return ItemInteractionResult.CONSUME;
            }
        } else if (stack.isEmpty() && state.getValue(JAR) && level.getBlockEntity(pos) instanceof JarRackBlockEntity blockEntity) {
            if (!level.isClientSide()) {
                // We take the jar and give it to the player
                ItemStack jar = getJar(level, blockEntity);
                player.setItemInHand(hand, jar);
            }
            // We clear the contents of the jar
            player.playSound(SoundEvents.ARMOR_EQUIP_LEATHER.value(), 1.5f, 1.0f + ((level.random.nextFloat()/2)-0.25f));
            level.setBlock(pos, state.setValue(JAR, false), 3);
            blockEntity.clearContents();

            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.FAIL;
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (level.getBlockEntity(pos) instanceof JarRackBlockEntity blockEntity && state.getValue(JAR)) {
            if (!level.isClientSide()) {
                ItemStack jar = getJar(level, blockEntity);

                ItemEntity itemEntity = new ItemEntity(level,
                        pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        jar);
                itemEntity.setDefaultPickUpDelay();
                level.addFreshEntity(itemEntity);
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    public ItemStack getJar(Level level, JarRackBlockEntity blockEntity) {
        ItemStack itemStack = new ItemStack(ModBlocks.HANGING_JAR);

        CompoundTag nbt = blockEntity.saveWithoutMetadata(level.registryAccess());
        if (!nbt.isEmpty() && nbt.contains("Inventory")) {
            ResourceLocation id = BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(blockEntity.getType());
            if (id != null) {
                nbt.putString("id", id.toString());
                itemStack.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(nbt));
            }
        }

        return itemStack;
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction clickedFace = context.getClickedFace();

        if (clickedFace.getAxis() == Direction.Axis.Y) {
            return null;
        }

        Level level = context.getLevel();
        BlockPos myPos = context.getClickedPos();
        Direction wallDirection = clickedFace.getOpposite();
        BlockPos wallPos = myPos.relative(wallDirection);
        BlockState wallState = level.getBlockState(wallPos);

        if (wallState.isFaceSturdy(level, wallPos, clickedFace)) {
            return this.defaultBlockState().setValue(FACING, clickedFace).setValue(JAR, false);
        }

        return null;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction facing = state.getValue(FACING);

        Direction wallDirection = facing.getOpposite();
        BlockPos wallPos = pos.relative(wallDirection);
        BlockState wallState = level.getBlockState(wallPos);

        return wallState.isFaceSturdy(level, wallPos, facing) && (level.getBlockState(pos.below()).is(ModTags.Blocks.JAR_FUEL) || !state.getValue(JAR));
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
        if (!state.canSurvive(level, currentPos)) {
            if (level.getBlockEntity(currentPos) instanceof JarRackBlockEntity blockEntity && state.getValue(JAR)) {
                if (!level.isClientSide()) {
                    ItemStack jar = getJar((Level) level, blockEntity);

                    ItemEntity itemEntity = new ItemEntity((Level) level,
                            currentPos.getX() + 0.5, currentPos.getY() + 0.5, currentPos.getZ() + 0.5,
                            jar);

                    itemEntity.setDefaultPickUpDelay();
                    level.addFreshEntity(itemEntity);
                }
            }
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, direction, neighborState, level, currentPos, neighborPos);
    }

    protected float getYRot(Direction facing) {
        return switch (facing) {
            case NORTH -> 180f;
            case SOUTH -> 0f;
            case WEST  -> 90f;
            case EAST  -> -90f;
            default -> 0f;
        };
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, JAR);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new JarRackBlockEntity(blockPos, blockState);
    }
}

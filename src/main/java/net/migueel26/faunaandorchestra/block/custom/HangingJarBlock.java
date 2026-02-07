package net.migueel26.faunaandorchestra.block.custom;

import com.mojang.serialization.MapCodec;
import net.migueel26.faunaandorchestra.block.entity.HangingJarBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HangingJarBlock extends BaseEntityBlock {
    public static final MapCodec<HangingJarBlock> CODEC = simpleCodec(HangingJarBlock::new);
    protected static VoxelShape SHAPE = Shapes.or(
        Block.box(4.675, 0, 4.675, 11.325, 9.31, 11.325),
        Block.box(6.337,9.31, 6.337, 9.662, 10.64, 9.662),
        Block.box(6, 10.64, 6, 10, 12, 10)
    );
    public HangingJarBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof HangingJarBlockEntity hangingJarBlockEntity) {
            if (!level.isClientSide()) {
                ((ServerPlayer) player).openMenu(new SimpleMenuProvider(hangingJarBlockEntity, hangingJarBlockEntity.getDisplayName()), pos);
            } else {
                player.playSound(SoundEvents.ARMOR_EQUIP_LEATHER.value(), 1.5f, 1.0f + ((level.random.nextFloat()/2)-0.25f));
            }
        }
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (entity instanceof ItemEntity itemEntity) {
            ItemStack toIntroduce = itemEntity.getItem();
            // TODO: TAG PARA LO Q SE PUEDA INTRODUCIR
            if (level.getBlockEntity(pos) instanceof HangingJarBlockEntity blockEntity && true) {
                ItemStackHandler inventory = blockEntity.inventory;
                boolean placed = false;

                for (int slot = 0; slot < inventory.getSlots() && !placed; slot++) {
                    ItemStack stack = inventory.getStackInSlot(slot);
                    if (stack.is(toIntroduce.getItem()) && stack.isStackable()) {
                        int difference = stack.getMaxStackSize() - stack.getCount();
                        if (toIntroduce.getCount() <= difference) {
                            // If it can be fully introduced
                            stack.setCount(toIntroduce.getCount() + stack.getCount());
                            itemEntity.remove(Entity.RemovalReason.DISCARDED);
                            placed = true;
                        } else if (difference > 0) {
                            // If it can be partially introduced
                            stack.setCount(stack.getMaxStackSize());
                            toIntroduce.shrink(toIntroduce.getCount() - difference);
                            placed = true;
                        }
                    } else if (stack.isEmpty()) {
                        // If a slot is empty
                        inventory.setStackInSlot(slot, toIntroduce);
                        itemEntity.remove(Entity.RemovalReason.DISCARDED);
                        placed = true;
                    }
                }

                if (placed && !level.isClientSide()) {
                    level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0f, 1.0f + level.random.nextFloat()/2);
                }
            }
        }

    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (blockEntity instanceof HangingJarBlockEntity jarBE) {
            if (!level.isClientSide) {
                ItemStack itemStack = new ItemStack(this);

                CompoundTag nbt = jarBE.saveWithoutMetadata(level.registryAccess());
                ResourceLocation id = BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(jarBE.getType());
                if (id != null) {
                    nbt.putString("id", id.toString());
                }

                if (!nbt.isEmpty()) {
                    itemStack.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(nbt));
                }

                ItemEntity itemEntity = new ItemEntity(level,
                        pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        itemStack);
                itemEntity.setDefaultPickUpDelay();
                level.addFreshEntity(itemEntity);
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("item.faunaandorchestra.hanging_jar.desc"));
        } else {
            tooltipComponents.add(Component.translatable("tooltip.faunaandorchestra.shift"));
        }

        CustomData customData = stack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY);

        if (!customData.isEmpty()) {
            CompoundTag nbt = customData.copyTag();

            if (nbt.contains("Inventory")) {
                ItemStackHandler inventory = new ItemStackHandler(6);
                inventory.deserializeNBT(context.registries(), nbt.getCompound("Inventory"));

                for (int i = 0; i < inventory.getSlots(); i++) {
                    ItemStack itemInSlot = inventory.getStackInSlot(i);

                    if (!itemInSlot.isEmpty()) {
                        MutableComponent name = Component.empty()
                                .append(itemInSlot.getHoverName())
                                .append(" x" + itemInSlot.getCount())
                                .withStyle(ChatFormatting.DARK_GRAY);

                        tooltipComponents.add(name);
                    }
                }
            }
        }

        super.appendHoverText(stack, context, tooltipComponents, flag);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new HangingJarBlockEntity(blockPos, blockState);
    }
}

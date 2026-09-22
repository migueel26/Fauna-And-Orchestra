package net.migueel26.faunaandorchestra.block.entity;

import net.migueel26.faunaandorchestra.block.ModBlockEntities;
import net.migueel26.faunaandorchestra.block.custom.ListenerContainerBlock;
import net.migueel26.faunaandorchestra.block.custom.TermiteChestBlock;
import net.migueel26.faunaandorchestra.entity.ModEntities;
import net.migueel26.faunaandorchestra.entity.custom.TermiteEntity;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;

public class TermiteChestBlockEntity extends BlockEntity implements MenuProvider, GeoBlockEntity {
    // GECKO
    private final static RawAnimation CLOSE = RawAnimation.begin().thenPlay("close");
    private final static RawAnimation IDLE_CLOSE = RawAnimation.begin().thenPlay("idle_close");
    private final static RawAnimation OPEN = RawAnimation.begin().thenPlay("open").thenPlay("idle");
    private final static RawAnimation INSERT = RawAnimation.begin().thenPlay("insert");
    private final AnimationController<TermiteChestBlockEntity> controller = new AnimationController<>(this, "termite_chest_controller", 0, this::animController)
            .triggerableAnim("insert", INSERT)
            .triggerableAnim("close", CLOSE);
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    // TERMITE
    private boolean hasTermite = true;
    private NonNullList<BlockPos> listeners = NonNullList.create();

    // CHEST
    public final SimpleContainer container = new SimpleContainer(27) {
        @Override
        public void setChanged() {
            TermiteChestBlockEntity.this.setChanged();

            if (level != null && !level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
                checkAndWakeTermite();
            }
        }

        @Override
        public void startOpen(Player player) {
            if (level != null && !level.isClientSide()) {
                level.playSound(null, getBlockPos(), SoundEvents.CHEST_OPEN, SoundSource.BLOCKS, 0.5f, level.random.nextFloat() * 0.1f + 0.9f);
                markUpdated();
            }
        }

        @Override
        public void stopOpen(Player player) {
            if (level != null && !level.isClientSide()) {
                level.playSound(null, getBlockPos(), SoundEvents.CHEST_CLOSE, SoundSource.BLOCKS, 0.5f, level.random.nextFloat() * 0.1f + 0.9f);
                markUpdated();
            }
        }
    };

    public TermiteChestBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.TERMITE_CHEST_BE.get(), pos, blockState);
    }

    private <E extends TermiteChestBlockEntity> PlayState animController(AnimationState<E> state) {
        if (hasTermite) {
            state.getController().setAnimation(OPEN);
        } else {
            state.getController().setAnimation(IDLE_CLOSE);
        }
        return PlayState.CONTINUE;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.put("Inventory", container.createTag(registries));
        tag.putBoolean("hasTermite", hasTermite);

        tag.putLongArray("Listeners", getListenersSerialized());

        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        this.hasTermite = tag.getBoolean("hasTermite");
        container.fromTag(tag.getList("Inventory", 10), registries);

        loadSerializedListeners(tag);
    }

    private void loadSerializedListeners(CompoundTag tag) {
        this.listeners.clear();
        long[] listenerPositions = tag.getLongArray("Listeners");
        for (long posLong : listenerPositions) {
            this.listeners.add(BlockPos.of(posLong));
        }
    }

    private long @NotNull [] getListenersSerialized() {
        long[] listenerPositions = new long[this.listeners.size()];
        for (int i = 0; i < this.listeners.size(); i++) {
            listenerPositions[i] = this.listeners.get(i).asLong();
        }
        return listenerPositions;
    }

    public void checkAndWakeTermite() {
        if (hasTermite && !this.listeners.isEmpty() && aptConditions()) {
            this.hasTermite = false;
            closeAnim();
            consumeGlassBottle();
            level.playSound(null, getBlockPos(), SoundEvents.CHEST_CLOSE, SoundSource.BLOCKS, 0.5f, level.random.nextFloat() * 0.1f + 0.9f);
            spawnTermite(this.listeners.getFirst());
        }
    }

    private void spawnTermite(BlockPos targetPos) {
        TermiteEntity termite = new TermiteEntity(ModEntities.TERMITE.get(), level);
        termite.moveTo(getBlockPos().above(), 0f, 0f);
        termite.setYHeadRot(getYRot(getBlockState().getValue(TermiteChestBlock.FACING)));
        termite.setYBodyRot(termite.getYRot());
        termite.setOriginPos(getBlockPos());
        termite.setDestinyPos(targetPos);
        termite.setHasLiquid(false);
        if (level != null) level.addFreshEntity(termite);
    }

    public void notify(BlockPos listenerPos) {
        if (!this.listeners.contains(listenerPos)) {
            this.listeners.addLast(listenerPos);
        }
        checkAndWakeTermite();
        markUpdated();
    }

    public void workDone(TermiteEntity termite) {
        // We remove the last listener
        if (!this.listeners.isEmpty()) {
            this.listeners.removeFirst();
        }

        // Save the liquid music if there is
        if (termite.hasLiquid()) {
            int index = hasSpaceForLiquidMusic();
            if (index != -1) {
                ItemStack currentStack = this.container.getItem(index);
                if (currentStack.is(ModItems.MUSIC_BOTTLE.get())) {
                    currentStack.grow(1);
                } else {
                    this.container.setItem(index, new ItemStack(ModItems.MUSIC_BOTTLE.get(), 1));
                }

                level.playSound(null, getBlockPos(), SoundEvents.CHEST_OPEN, SoundSource.BLOCKS, 1.0f, 1.0f);
            } else {
                // Drop the item in the world
                if (level != null) {
                    ItemStack musicBottle = new ItemStack(ModItems.MUSIC_BOTTLE.get(), 1);
                    Block.popResource(level, this.getBlockPos().above(), musicBottle);
                }
            }
        }

        // We check if there are more listeners to send the termite to
        BlockPos nextListener = null;

        // Get the next listener
        if (aptConditions()) {
            while (!this.listeners.isEmpty()) {
                BlockPos pos = this.listeners.getFirst();
                if (level != null && level.getBlockEntity(pos) instanceof ListenerContainerBlockEntity listenerContainer &&
                        listenerContainer.isFull()) {
                    nextListener = pos;
                    break;
                } else {
                    this.listeners.removeFirst();
                }
            }
        }

        // New route for the termite or enter the chest
        if (nextListener != null) {
            if (termite.hasLiquid()) {
                consumeGlassBottle();
            }

            insertAnim();

            termite.setOriginPos(getBlockPos());
            termite.setDestinyPos(nextListener);
            termite.setHasLiquid(false);
            termite.setGoingToListener(true);

        } else {
            this.hasTermite = true;
            level.playSound(null, getBlockPos(), SoundEvents.CHEST_OPEN, SoundSource.BLOCKS, 1.0f, 1.0f);
            termite.discard();
        }

        markUpdated();

    }

    private void consumeGlassBottle() {
        for (int i = 0; i < this.container.getContainerSize(); i++) {
            ItemStack stack = this.container.getItem(i);
            if (stack.is(Items.GLASS_BOTTLE)) {
                stack.shrink(1);
                return;
            }
        }
    }

    protected int hasSpaceForLiquidMusic() {
        for (int i = 0; i < this.container.getContainerSize(); i++) {
            ItemStack stack = this.container.getItem(i);
            if (stack.isEmpty() || (stack.is(ModItems.MUSIC_BOTTLE) && stack.getCount() < stack.getMaxStackSize())) {
                return i;
            }
        }
        return -1;
    }

    protected boolean aptConditions() {
        boolean hasBottle = false;
        for (int i = 0; i < this.container.getContainerSize(); i++) {
            if (this.container.getItem(i).is(Items.GLASS_BOTTLE)) {
                hasBottle = true;
                break;
            }
        }

        return hasBottle && hasSpaceForLiquidMusic() != -1;
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

    protected void markUpdated() {
        this.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
        }
    }

    public void insertAnim() {
        triggerAnim("termite_chest_controller", "insert");
    }

    public void closeAnim() {
        triggerAnim("termite_chest_controller", "close");
    }

    public boolean hasTermite() {
        return this.hasTermite;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        return saveWithoutMetadata(pRegistries);
    }

    @Override
    public Component getDisplayName() {
        return getBlockState().getBlock().getName();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(controller);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return ChestMenu.threeRows(i, inventory, container);
    }
}

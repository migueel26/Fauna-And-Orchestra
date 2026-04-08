package net.migueel26.faunaandorchestra.block.entity;

import net.migueel26.faunaandorchestra.block.ModBlockEntities;
import net.migueel26.faunaandorchestra.block.ModBlocks;
import net.migueel26.faunaandorchestra.block.custom.MailboxBlock;
import net.migueel26.faunaandorchestra.component.ModDataComponents;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.screen.custom.MailboxMenu;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.neoforged.neoforge.items.ItemStackHandler;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.Optional;

public class MailboxBlockEntity extends BlockEntity implements GeoBlockEntity, MenuProvider {
    protected final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    protected final RawAnimation ARRIVE = RawAnimation.begin().thenPlay("arrive");
    protected final AnimationController<MailboxBlockEntity> controller = new AnimationController<>(this, "mailbox_controller", 0, this::animController)
            .triggerableAnim("arrive", ARRIVE);
    public ItemStackHandler inventory = new ItemStackHandler(6) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return stack.has(ModDataComponents.POSITION) || stack.is(ModItems.BUSINESS_CARD);
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();

            if (level != null && !level.isClientSide) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    private PlayState animController(AnimationState<MailboxBlockEntity> state) {
        state.getController().setAnimation(IDLE);
        return PlayState.CONTINUE;
    }

    public void arrive() {
        triggerAnim("mailbox_controller", "arrive");
    }

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public MailboxBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.MAILBOX_BE.get(), pos, blockState);
    }

    public void clearContents() {
        for (int slot = 0; slot < inventory.getSlots(); slot++) {
            inventory.setStackInSlot(slot, ItemStack.EMPTY);
        }
    }

    public void deliverMail() {
        boolean allDelivered = true;
        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty()) {
                // TODO: BUSINESS CARD
                BlockPos address = stack.get(ModDataComponents.POSITION);

                Optional<BlockPos> optionalMailBoxPos = BlockPos.findClosestMatch(address,6, 6, pos -> level.getBlockState(pos).is(ModBlocks.MAILBOX));

                if (optionalMailBoxPos.isPresent()) {
                    BlockPos pos = optionalMailBoxPos.get();
                    BlockState mailBoxState = level.getBlockState(pos);

                    // We get the lower part of the mailbox (it has the inventory)
                    BlockPos bePos = mailBoxState.getValue(MailboxBlock.HALF) == DoubleBlockHalf.LOWER ? pos : pos.below();

                    if (level.getBlockEntity(bePos) instanceof MailboxBlockEntity blockEntity) {
                        boolean emptySlot = false;

                        for (int j = 0; j < blockEntity.inventory.getSlots() && !emptySlot; j++) {
                            if (blockEntity.inventory.getStackInSlot(j).isEmpty()) {
                                blockEntity.inventory.setStackInSlot(j, stack);
                                this.inventory.setStackInSlot(i, ItemStack.EMPTY);
                                emptySlot = true;
                            }
                        }

                        if (!emptySlot) {
                            allDelivered = false;
                        } else {
                            level.playSound(null, pos, ModSounds.TWINKLE.get(), SoundSource.BLOCKS);
                        }
                    }
                } else {
                    allDelivered = false;
                }
            }
        }
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new MailboxMenu(i, inventory, this);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.put("Inventory", inventory.serializeNBT(registries));
        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        super.loadAdditional(tag, registries);
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
}

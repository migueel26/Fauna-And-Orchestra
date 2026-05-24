package net.migueel26.faunaandorchestra.block.entity;

import net.migueel26.faunaandorchestra.block.ModBlockEntities;
import net.migueel26.faunaandorchestra.block.ModBlocks;
import net.migueel26.faunaandorchestra.block.custom.MailboxBlock;
import net.migueel26.faunaandorchestra.component.ModDataComponents;
import net.migueel26.faunaandorchestra.entity.ModEntities;
import net.migueel26.faunaandorchestra.entity.custom.koala_workers.WorkerKoalaEntity;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.particles.ModParticleTypes;
import net.migueel26.faunaandorchestra.screen.custom.MailboxMenu;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.neoforged.neoforge.items.ItemStackHandler;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.Optional;

public class MailboxBlockEntity extends OwnableBlockEntity implements GeoBlockEntity, MenuProvider {
    protected final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    protected final RawAnimation ARRIVE = RawAnimation.begin().thenPlay("arrive");
    protected final AnimationController<MailboxBlockEntity> controller = new AnimationController<>(this, "mailbox_controller", 0, this::animController)
            .triggerableAnim("arrive", ARRIVE);
    // If true, the menu will show a warning next time
    protected boolean showWarning = false;
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
                if (!stack.is(ModItems.BUSINESS_CARD)) {
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
                                level.playSound(null, pos, ModSounds.WOW.get(), SoundSource.BLOCKS);
                            }
                        }
                    } else {
                        allDelivered = false;
                    }
                } else {
                    allDelivered = tryToDeliverBusinessCard(i, allDelivered);
                }
            }
        }

        boolean previousWarning = this.showWarning;
        this.showWarning = !allDelivered;

        if (previousWarning != this.showWarning) {
            setChanged();
            if (level != null && !level.isClientSide) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    }

    private boolean tryToDeliverBusinessCard(int i, boolean allDelivered) {
        // We find a possible empty block
        BlockPos foundSpace = null;
        for (BlockPos pos : BlockPos.betweenClosed(getBlockPos().west().north().above(), getBlockPos().east().south().below())) {
            if (level.getBlockState(pos).isEmpty()) {
                inventory.setStackInSlot(i, ItemStack.EMPTY);
                foundSpace = pos;
                break;
            }
        }

        if (foundSpace != null) {
            // We summon the Worker Koala
            WorkerKoalaEntity koala = new WorkerKoalaEntity(ModEntities.WORKER_KOALA.get(), level);
            koala.moveTo(foundSpace.getCenter());

            level.addFreshEntity(koala);

            level.playSound(null, foundSpace, ModSounds.TWINKLE.get(), SoundSource.BLOCKS);
            ((ServerLevel) level).sendParticles(ModParticleTypes.STAR.get(),
                    foundSpace.getCenter().x, foundSpace.getY()+0.45, foundSpace.getCenter().z,
                    10, 0.1f, 0.1f, 0.1f, 0.025f);
        } else {
            allDelivered = false;
        }
        return allDelivered;
    }

    public int getEmptySlotIndex() {
        int emptySlot = -1;
        for (int slot = 0; slot < inventory.getSlots() && emptySlot == -1; slot++) {
            if (inventory.getStackInSlot(slot).isEmpty()) {
                emptySlot = slot;
            }
        }
        return emptySlot;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new MailboxMenu(i, inventory, this, showWarning);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.put("Inventory", inventory.serializeNBT(registries));
        tag.putBoolean("Warning", this.showWarning);
        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        this.showWarning = tag.getBoolean("Warning");
        super.loadAdditional(tag, registries);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        CompoundTag tag = saveWithoutMetadata(pRegistries);
        tag.putUUID("Owner", owner);
        return tag;
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

    public boolean isShowWarning() {
        return showWarning;
    }

    public void setShowWarning(boolean showWarning) {
        this.showWarning = showWarning;
    }
}

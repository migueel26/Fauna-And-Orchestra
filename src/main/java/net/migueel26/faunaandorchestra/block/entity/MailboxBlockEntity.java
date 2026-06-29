package net.migueel26.faunaandorchestra.block.entity;

import net.migueel26.faunaandorchestra.block.ModBlockEntities;
import net.migueel26.faunaandorchestra.block.ModBlocks;
import net.migueel26.faunaandorchestra.block.custom.MailboxBlock;
import net.migueel26.faunaandorchestra.component.ModDataComponents;
import net.migueel26.faunaandorchestra.entity.ModEntities;
import net.migueel26.faunaandorchestra.entity.custom.koala_workers.WorkerKoalaEntity;
import net.migueel26.faunaandorchestra.entity.custom.misc.MailbirdMacawEntity;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.particles.ModParticleTypes;
import net.migueel26.faunaandorchestra.screen.custom.MailboxMenu;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.Optional;

public class MailboxBlockEntity extends OwnableBlockEntity implements GeoBlockEntity, MenuProvider {
    protected final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    protected final RawAnimation ARRIVE = RawAnimation.begin().thenPlay("arrive");
    protected final AnimationController<MailboxBlockEntity> controller = new AnimationController<>(this, "mailbox_controller", 0, this::animController)
            .triggerableAnim("arrive", ARRIVE);

    private int timeAway = 0;

    // If true, the menu will show a warning next time
    protected boolean showWarning = false;
    public ItemStackHandler inventory = new ItemStackHandler(6) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return (stack.hasTag() && stack.getTag().contains(ModDataComponents.POSITION)) || stack.is(ModItems.BUSINESS_CARD.get());
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();

            if (level != null && !level.isClientSide) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };
    private final LazyOptional<IItemHandler> inventoryOptional = LazyOptional.of(() -> this.inventory);

    private PlayState animController(AnimationState<MailboxBlockEntity> state) {
        state.getController().setAnimation(IDLE);
        return PlayState.CONTINUE;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, MailboxBlockEntity entity) {
        if (level.isClientSide()) return;

        // Only lower half
        if (state.getValue(MailboxBlock.HALF) == DoubleBlockHalf.UPPER) return;

        // If mailbird flying
        if (!state.getValue(MailboxBlock.MAILBIRD)) {
            entity.timeAway++;

            // If its time to deliver mail
            if (entity.timeAway >= MailboxBlock.TIME_TO_SEND) {
                entity.deliverMail();
                entity.macawArriveAnimation(state, level, pos);

                // Set the mailbird back
                level.setBlock(pos, state.setValue(MailboxBlock.MAILBIRD, true), 3);

                // Update top
                BlockPos topPos = pos.above();
                if (level.getBlockState(topPos).is(ModBlocks.MAILBOX.get())) {
                    level.setBlock(topPos, level.getBlockState(topPos).setValue(MailboxBlock.MAILBIRD, true), 3);
                }

                entity.timeAway = 0;
                entity.setChanged();
            }
        }
    }

    public void triggerDelivery() {
        if (this.level == null || this.level.isClientSide) return;

        BlockState state = this.getBlockState();
        if (state.getValue(MailboxBlock.MAILBIRD) && state.getValue(MailboxBlock.HALF) == DoubleBlockHalf.LOWER) {
            BlockPos topPos = getBlockPos().above();
            MailbirdMacawEntity entity = new MailbirdMacawEntity(ModEntities.MACAW.get(), level);

            // Above to place it on top of the mailbox
            entity.moveTo(topPos.above(), 0f, 0f);
            entity.setYHeadRot(MailboxBlock.getYRot(state.getValue(MailboxBlock.FACING)));
            entity.setYBodyRot(entity.getYRot());
            level.addFreshEntity(entity);
            entity.flyAway();

            level.playSound(null, getBlockPos(), SoundEvents.PARROT_AMBIENT, SoundSource.BLOCKS);

            // Remove visual Mailbird
            level.setBlock(getBlockPos(), state.setValue(MailboxBlock.MAILBIRD, false), 3);
            if (level.getBlockState(topPos).is(ModBlocks.MAILBOX.get())) {
                level.setBlock(topPos, level.getBlockState(topPos).setValue(MailboxBlock.MAILBIRD, false), 3);
            }

            this.timeAway = 0;
        }
    }

    public void macawArriveAnimation(BlockState state, Level level, BlockPos pos) {
        this.arrive();
        if (!level.isClientSide()) {
            Direction direction = state.getValue(MailboxBlock.FACING).getOpposite();
            Vec3 particlePos = pos.above().relative(direction, 2).getCenter();
            ((ServerLevel) level).sendParticles(ParticleTypes.CLOUD, particlePos.x, particlePos.y + 1, particlePos.z,
                    20, 0.5, 0.5, 0.5, 0.15);
            level.playSound(null, pos, SoundEvents.PARROT_AMBIENT, SoundSource.BLOCKS, 2.0f, 1.0f);
        }
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

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    public void deliverMail() {
        boolean allDelivered = true;
        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty()) {
                BlockPos address = null;

                if (stack.hasTag() && stack.getTag().contains(ModDataComponents.POSITION)) {
                    int[] posArray = stack.getTag().getIntArray(ModDataComponents.POSITION);
                    if (posArray.length == 3) {
                        address = new BlockPos(posArray[0], posArray[1], posArray[2]);
                    }
                }

                if (address != null && !stack.is(ModItems.BUSINESS_CARD.get())) {
                    Optional<BlockPos> optionalMailBoxPos = BlockPos.findClosestMatch(address,6, 6, pos -> level.getBlockState(pos).is(ModBlocks.MAILBOX.get()));

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
            if (level.getBlockState(pos).isAir()) {
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
    protected void saveAdditional(CompoundTag tag) {
        tag.put("Inventory", inventory.serializeNBT());
        tag.putBoolean("Warning", this.showWarning);
        tag.putInt("TimeAway", this.timeAway);
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        inventory.deserializeNBT(tag.getCompound("Inventory"));
        this.showWarning = tag.getBoolean("Warning");
        this.timeAway = tag.getInt("TimeAway");
        super.load(tag);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return inventoryOptional.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        inventoryOptional.invalidate();
    }

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(
                this.getBlockPos().offset(-16, -16, -16).getCenter(),
                this.getBlockPos().offset(16, 16, 16).getCenter()
        );
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

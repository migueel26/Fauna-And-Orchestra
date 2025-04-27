package net.migueel26.faunaandorchestra.entity.custom;

import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.screen.custom.ConductorMenu;
import net.migueel26.faunaandorchestra.util.ModTags;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.HashSet;
import java.util.Set;

public abstract class ConductorEntity extends TamableAnimal {
    protected static final EntityDataAccessor<Boolean> HOLDING_BATON = SynchedEntityData.defineId(ConductorEntity.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Boolean> IS_MUSICAL = SynchedEntityData.defineId(ConductorEntity.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Boolean> IS_CONDUCTING = SynchedEntityData.defineId(ConductorEntity.class, EntityDataSerializers.BOOLEAN);
    protected boolean holdingBaton = false;
    protected boolean isConducting = false;
    protected Set<MusicalEntity> orchestra = new HashSet<>();
    public ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return stack.is(ModTags.Items.SHEET_MUSIC);
        }

        @Override
        protected int getStackLimit(int slot, ItemStack stack) {
            return 1;
        }
    };
    protected int ticksPlaying = 0;

    public ConductorEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(HOLDING_BATON, false);
        builder.define(IS_CONDUCTING, false);
        // PROVISIONAL
        builder.define(IS_MUSICAL, true);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);

        if (HOLDING_BATON.equals(key)) {
            this.holdingBaton = this.entityData.get(HOLDING_BATON);
        }

        if (IS_CONDUCTING.equals(key)) {
            this.isConducting = this.entityData.get(IS_CONDUCTING);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);

        this.entityData.set(HOLDING_BATON, compound.getBoolean("HoldingBaton"));

        if (compound.contains("SheetMusic")) {
            ItemStack itemstack = ItemStack.parse(this.registryAccess(), compound.getCompound("SheetMusic")).orElse(ItemStack.EMPTY);
            if (itemstack.is(ModTags.Items.SHEET_MUSIC)) {
                this.inventory.setStackInSlot(0, itemstack);
            }
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);

        compound.putBoolean("HoldingBaton", this.isHoldingBaton());

        if (!this.inventory.getStackInSlot(0).isEmpty()) {
            compound.put("SheetMusic", this.inventory.getStackInSlot(0).save(this.registryAccess()));
        }
    }

    @Override
    public void tick() {
        if (!isOrchestraEmpty()) {
            ticksPlaying++;
        } else {
            ticksPlaying = 0;
        }

        super.tick();
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (isTame()) {
            if (hand == InteractionHand.MAIN_HAND && isHoldingBaton() && !player.isSecondaryUseActive()
                && !this.level().isClientSide()) {

                this.openCustomMenu(player);
                return InteractionResult.SUCCESS;

            } else if (itemStack.isEmpty() && isHoldingBaton() && player.isSecondaryUseActive()) {
                
                player.setItemInHand(hand, new ItemStack(ModItems.BATON.get(), 1));
                setHoldingBaton(false);
                setOrderedToSit(false);
                return InteractionResult.SUCCESS;
                
            } else if (itemStack.is(ModItems.BATON) && !isHoldingBaton()) {
                
                level().addParticle(ParticleTypes.NOTE, this.getX(), this.getY() + 2.5, this.getZ(), 0F, 0.5F, 0F);
                player.setItemInHand(hand, ItemStack.EMPTY);
                setHoldingBaton(true);
                setOrderedToSit(true);
                return InteractionResult.CONSUME;
                
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!this.level().isClientSide) {
            if (isHoldingBaton() && isTame()) {
                setHoldingBaton(false);
                setInSittingPose(false);
                this.level().addFreshEntity(new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(),
                        new ItemStack((Holder<Item>) ModItems.BATON, 1)));
            }
        }
        return super.hurt(source, amount);
    }

    private void openCustomMenu(Player player) {
        if (!this.level().isClientSide()) {
            ((ServerPlayer) player).openMenu(new SimpleMenuProvider((id, playerInventory, playerEntity) ->
                    new ConductorMenu(id, playerInventory, this), this.getDisplayName()), buf -> {
                buf.writeUUID(getUUID());
            });
        }
    }

    @Override
    public boolean shouldTryTeleportToOwner() {
        return false;
    }

    public void setHoldingBaton(boolean holdingBaton) {
        this.entityData.set(HOLDING_BATON, holdingBaton);
    }

    public boolean isHoldingBaton() {
        return holdingBaton;
    }

    public boolean isConducting() {
        return isConducting;
    }

    public Set<MusicalEntity> getOrchestra() {
        return orchestra;
    }

    public Item getSheetMusic() {
        return inventory.getStackInSlot(0).getItem();
    }

    public boolean isHoldingASheetMusic() {
        return !inventory.getStackInSlot(0).isEmpty();
    }

    public int getTicksPlaying() {
        return ticksPlaying;
    }

    public void setTicksPlaying(int ticksPlaying) {
        this.ticksPlaying = ticksPlaying;
    }

    public void addMusician(MusicalEntity musicalEntity) {
        orchestra.add(musicalEntity);
        if (!isConducting) this.entityData.set(IS_CONDUCTING, true); isConducting = true;
    }

    public void removeMusician(MusicalEntity musicalEntity) {
        orchestra.remove(musicalEntity);
        if (orchestra.isEmpty()) this.entityData.set(IS_CONDUCTING, false); isConducting = false;
    }

    public boolean isOrchestraEmpty() {
        return orchestra == null || orchestra.isEmpty();
    }

    public boolean isOrchestraFull() {
        return orchestra.size() == 4;
    }

}

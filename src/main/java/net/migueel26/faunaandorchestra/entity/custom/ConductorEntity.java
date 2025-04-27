package net.migueel26.faunaandorchestra.entity.custom;

import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.screen.custom.ConductorMenu;
import net.migueel26.faunaandorchestra.util.ModTags;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.HashSet;
import java.util.Set;

public abstract class ConductorEntity extends TamableAnimal {
    protected static final EntityDataAccessor<Boolean> HOLDING_BATON = SynchedEntityData.defineId(ConductorEntity.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Boolean> IS_CONDUCTOR = SynchedEntityData.defineId(ConductorEntity.class, EntityDataSerializers.BOOLEAN);
    protected boolean holdingBaton = false;
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
        // PROVISIONAL
        builder.define(IS_CONDUCTOR, true);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);

        if (HOLDING_BATON.equals(key)) {
            this.holdingBaton = this.entityData.get(HOLDING_BATON);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);

        this.entityData.set(HOLDING_BATON, compound.getBoolean("HoldingBaton"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);

        compound.putBoolean("HoldingBaton", this.isHoldingBaton());
    }

    @Override
    public void tick() {
        if (holdingBaton) {
            double n = orchestra.size();
            this.getNavigation().stop();
            this.lookAt(EntityAnchorArgument.Anchor.EYES,  new Vec3(
                    orchestra.stream().map(Entity::getX).reduce(0.0, Double::sum)/n,
                    this.getY(),
                    orchestra.stream().map(Entity::getZ).reduce(0.0, Double::sum)/n));
        }

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

    private void openCustomMenu(Player player) {
        if (!this.level().isClientSide()) {
            ((ServerPlayer) player).openMenu(new SimpleMenuProvider((id, playerInventory, playerEntity) ->
                    new ConductorMenu(id, playerInventory, this), this.getDisplayName()), buf -> {
                buf.writeUUID(getUUID());
            });
        }
    }

    public void setHoldingBaton(boolean holdingBaton) {
        this.entityData.set(HOLDING_BATON, holdingBaton);
    }

    public boolean isHoldingBaton() {
        return holdingBaton;
    }

    public Set<MusicalEntity> getOrchestra() {
        return orchestra;
    }

    public Item getSheetMusic() {
        return inventory.getStackInSlot(0).getItem();
    }

    public int getTicksPlaying() {
        return ticksPlaying;
    }

    public void setTicksPlaying(int ticksPlaying) {
        this.ticksPlaying = ticksPlaying;
    }

    public void addMusician(MusicalEntity musicalEntity) {
        orchestra.add(musicalEntity);
    }

    public void removeMusician(MusicalEntity musicalEntity) {
        orchestra.remove(musicalEntity);
    }

    public boolean isOrchestraEmpty() {
        return orchestra == null || orchestra.isEmpty();
    }

    public boolean isOrchestraFull() {
        return orchestra.size() == 4;
    }

}

package net.migueel26.faunaandorchestra.entity.custom;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.advancements.ModAdvancements;
import net.migueel26.faunaandorchestra.component.ModDataComponents;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.mixins.client.accessors.ClientLevelAccessor;
import net.migueel26.faunaandorchestra.screen.custom.ConductorMenu;
import net.migueel26.faunaandorchestra.screen.custom.MusicianMenu;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.migueel26.faunaandorchestra.util.ModTags;
import net.migueel26.faunaandorchestra.util.MusicUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
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
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;

import java.util.*;

public abstract class MusicalEntity extends TamableAnimal implements GeoEntity {
    protected DeferredItem<Item> instrument;
    protected static final EntityDataAccessor<Boolean> HOLDING_INSTRUMENT = SynchedEntityData.defineId(MusicalEntity.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Optional<UUID>> CONDUCTOR_ID = SynchedEntityData.defineId(MusicalEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    protected static final EntityDataAccessor<Boolean> IS_MUSICAL = SynchedEntityData.defineId(MusicalEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<ItemStack> COSTUME_ITEM = SynchedEntityData.defineId(MusicalEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<ItemStack> HAT_ITEM = SynchedEntityData.defineId(MusicalEntity.class, EntityDataSerializers.ITEM_STACK);
    protected boolean isHoldingInstrument;
    protected UUID conductorUUID;
    private int ticksSinceLoaded;
    // Costumes
    public ItemStackHandler inventory = new ItemStackHandler(2) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            // Checks if this entity can wear the clothing item via tag
            if (getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "wears_" + stack.getItem().getDescriptionId().split("\\.")[2])))) {
                return stack.is(slot == 0 ? ModTags.Items.IS_HAT : ModTags.Items.IS_COSTUME);
            }
            return false;
        }

        @Override
        protected int getStackLimit(int slot, ItemStack stack) {
            return 1;
        }

        @Override
        protected void onContentsChanged(int slot) {
            playSound(SoundEvents.ARMOR_EQUIP_LEATHER.value(), 1.0F, 1.0F + ((random.nextFloat()/2)-0.25F));

            if (slot == 0) {
                entityData.set(HAT_ITEM, getStackInSlot(0));
            } else {
                entityData.set(COSTUME_ITEM, getStackInSlot(1));
            }

            super.onContentsChanged(slot);
        }
    };

    protected MusicalEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
        this.instrument = getInstrument();
        this.conductorUUID = null;
        this.ticksSinceLoaded = 0;
    }

    public abstract DeferredItem<Item> getInstrument();

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(HOLDING_INSTRUMENT, false);
        builder.define(IS_MUSICAL, false);
        builder.define(CONDUCTOR_ID, Optional.empty());
        builder.define(COSTUME_ITEM, ItemStack.EMPTY);
        builder.define(HAT_ITEM, ItemStack.EMPTY);
        super.defineSynchedData(builder);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (HOLDING_INSTRUMENT.equals(key)) {
            this.isHoldingInstrument = this.entityData.get(HOLDING_INSTRUMENT);
        }

        if (CONDUCTOR_ID.equals(key)) {
            this.conductorUUID = this.entityData.get(CONDUCTOR_ID).orElse(null);
        }

        super.onSyncedDataUpdated(key);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);

        compound.putBoolean("HoldingInstrument", this.isHoldingInstrument());
        compound.putBoolean("IsMusical", this.isMusical());

        compound.put("Inventory", this.inventory.serializeNBT(this.registryAccess()));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);

        this.entityData.set(HOLDING_INSTRUMENT, compound.getBoolean("HoldingInstrument"));
        this.entityData.set(IS_MUSICAL, compound.getBoolean("IsMusical"));

        if (compound.contains("Inventory")) {
            this.inventory.deserializeNBT(this.registryAccess(), compound.getCompound("Inventory"));
            this.entityData.set(HAT_ITEM, this.inventory.getStackInSlot(0));
            this.entityData.set(COSTUME_ITEM, this.inventory.getStackInSlot(1));
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!this.level().isClientSide) {
            if (isHoldingInstrument() && isTame()) {
                setHoldingInstrument(false);
                setInSittingPose(false);
                this.level().addFreshEntity(new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(),
                        new ItemStack((Holder<Item>) instrument, 1)));
            }
        }
        return super.hurt(source, amount);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (isTame() && getOwnerUUID().equals(player.getUUID())) {
            if (itemStack.is(ModItems.PROP_CASE)) {

                this.openCustomMenu(player);
                return InteractionResult.SUCCESS;

            } else if (itemStack.is(ModTags.Items.IS_BATON) && !isPlayingInstrument() && itemStack.get(ModDataComponents.MUSICIAN_UUID) == null) {

                itemStack.set(ModDataComponents.MUSICIAN_UUID, this.uuid);
                return InteractionResult.SUCCESS;

            } else if (itemStack.is(instrument) && !isHoldingInstrument()) {

                setHoldingInstrument(true);
                player.setItemInHand(hand, ItemStack.EMPTY);
                level().addParticle(ParticleTypes.NOTE, this.getX(), this.getY() + 2.5, this.getZ(), 0F, 0.5F, 0F);
                setOrderedToSit(true);
                return InteractionResult.CONSUME;

            } else if (itemStack.isEmpty() && isHoldingInstrument()) {

                setHoldingInstrument(false);
                player.setItemInHand(hand, new ItemStack(instrument.get(), 1));
                setOrderedToSit(false);
                return InteractionResult.SUCCESS;

            } else if (itemStack.is(ModItems.BRIEFCASE) && itemStack.getOrDefault(ModDataComponents.OPENED, false)
                        && getOwnerUUID().equals(player.getUUID())) {
                List<String> animals = itemStack.get(ModDataComponents.BRIEFCASE_ANIMAL_LIST);

                if (animals == null) {
                    // If it's not initialized, we store it
                    animals = new ArrayList<>(6);
                    itemStack.set(ModDataComponents.BRIEFCASE_ANIMAL_LIST, animals);
                }

                if (animals.size() < 6) {
                    if (!level().isClientSide()) {
                        List<String> newAnimals = new ArrayList<>(animals);
                        newAnimals.add(MusicUtil.musicalAnimalToString(this));
                        itemStack.set(ModDataComponents.BRIEFCASE_ANIMAL_LIST, newAnimals);

                        if (newAnimals.size() == 6) {
                            itemStack.set(ModDataComponents.OPENED, false);
                        }

                        ((ServerLevel) level()).sendParticles(ParticleTypes.PORTAL,
                                this.getX(), this.getY(), this.getZ(),
                                60, 0.5, 0.5, 0.5, 0F);

                        if (isPlayingInstrument() && this.getConductor() != null) {
                            this.getConductor().removeMusician(this);
                        }

                        this.discard();
                    } else {
                        level().playSound(player, this.blockPosition(), SoundEvents.PLAYER_TELEPORT, SoundSource.BLOCKS);
                    }
                    return InteractionResult.SUCCESS;
                } else {
                    return InteractionResult.FAIL;
                }


            }
        }
        return InteractionResult.FAIL;
    }

    private void openCustomMenu(Player player) {
        if (!this.level().isClientSide()) {
            ((ServerPlayer) player).openMenu(new SimpleMenuProvider((id, playerInventory, playerEntity) ->
                    new MusicianMenu(id, playerInventory, this), this.getDisplayName()), buf -> {
                buf.writeUUID(getUUID());
            });
        }
    }

    @Override
    public void tick() {
        if (ticksSinceLoaded < 40) {
            ticksSinceLoaded++;
        }
        super.tick();
    }

    public void tryToTame(Player player) {
        if (level().getRandom().nextInt(3) == 0 && !net.neoforged.neoforge.event.EventHooks.onAnimalTame(this, player)) {
            this.tame(player);
            this.navigation.stop();
            this.setTarget(null);
            this.playSound(ModSounds.SUCCESSFUL_TAME.get());
            this.level().broadcastEntityEvent(this, (byte) 7);
            if (!level().isClientSide()) {
                ModAdvancements.TAME_MUSICIAN.get().trigger((ServerPlayer) player);
            }
        } else {
            this.level().broadcastEntityEvent(this, (byte) 6);
        }
    }

    public void setHoldingInstrument(boolean holdingInstrument) {
        this.entityData.set(HOLDING_INSTRUMENT, holdingInstrument);
    }

    public boolean isHoldingInstrument() {
        return isHoldingInstrument;
    }

    public boolean isPlayingInstrument() {
        return conductorUUID != null;
    }

    public void setMusical() {
        this.entityData.set(IS_MUSICAL, true);
    }

    public boolean isMusical() {
        return this.entityData.get(IS_MUSICAL);
    }

    public @Nullable ConductorEntity getConductor() {
        if (this.level().isClientSide()) {
            return conductorUUID == null ? null : (ConductorEntity) ((ClientLevelAccessor) level()).callGetEntities().get(conductorUUID);
        } else {
            return conductorUUID == null ? null : (ConductorEntity) ((ServerLevel) level()).getEntity(conductorUUID);
        }
    }

    @Override
    public boolean shouldTryTeleportToOwner() {
        return false;
    }

    public void setConductor(ConductorEntity conductor) {
        UUID conductorUUID = conductor == null ? null : conductor.getUUID();

        this.entityData.set(CONDUCTOR_ID, Optional.ofNullable(conductorUUID));
    }

    public int getTicksSinceLoaded() {
        return ticksSinceLoaded;
    }

    public Item getHat() {
        return this.entityData.get(HAT_ITEM).getItem();
    }

    public Item getCostume() {
        return this.entityData.get(COSTUME_ITEM).getItem();
    }
}

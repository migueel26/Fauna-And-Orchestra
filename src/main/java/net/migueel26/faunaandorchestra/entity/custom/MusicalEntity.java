package net.migueel26.faunaandorchestra.entity.custom;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.advancements.ModAdvancements;
import net.migueel26.faunaandorchestra.component.ModDataComponents;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.item.custom.BatonItem;
import net.migueel26.faunaandorchestra.item.custom.BriefcaseItem;
import net.migueel26.faunaandorchestra.mixins.client.accessors.ClientLevelAccessor;
import net.migueel26.faunaandorchestra.screen.custom.FarmerMenu;
import net.migueel26.faunaandorchestra.screen.custom.MusicianMenu;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.migueel26.faunaandorchestra.util.ModTags;
import net.migueel26.faunaandorchestra.util.MusicUtil;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
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
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class MusicalEntity extends TamableAnimal {
    protected RegistryObject<Item> instrument;
    protected static final EntityDataAccessor<Boolean> HOLDING_INSTRUMENT = SynchedEntityData.defineId(MusicalEntity.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Optional<UUID>> CONDUCTOR_ID = SynchedEntityData.defineId(MusicalEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    protected static final EntityDataAccessor<Boolean> IS_MUSICAL = SynchedEntityData.defineId(MusicalEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<ItemStack> COSTUME_ITEM = SynchedEntityData.defineId(MusicalEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<ItemStack> HAT_ITEM = SynchedEntityData.defineId(MusicalEntity.class, EntityDataSerializers.ITEM_STACK);
    protected boolean isHoldingInstrument;
    protected UUID conductorUUID;
    private int ticksSinceLoaded;
    //private Integer count = null;
    //private Player lastAttempt;
    // Costumes
    public final static int HAT_SLOT = 0;
    public final static int COSTUME_SLOT = 1;
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
            playSound(SoundEvents.ARMOR_EQUIP_LEATHER, 1.0F, 1.0F + ((random.nextFloat()/2)-0.25F));

            if (slot == 0) {
                entityData.set(HAT_ITEM, getStackInSlot(HAT_SLOT));
                if (!level().isClientSide()) {
                    playSpecialClothingAnimation(getStackInSlot(HAT_SLOT));
                }

            } else {
                entityData.set(COSTUME_ITEM, getStackInSlot(COSTUME_SLOT));
                if (!level().isClientSide()) {
                    playSpecialClothingAnimation(getStackInSlot(COSTUME_SLOT));
                }
            }

            super.onContentsChanged(slot);
        }
    };
    private final LazyOptional<IItemHandler> inventoryOptional = LazyOptional.of(() -> this.inventory);

    protected MusicalEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
        this.instrument = getInstrument();
        this.conductorUUID = null;
        this.ticksSinceLoaded = 0;
    }

    public abstract RegistryObject<Item> getInstrument();

    @Override
    protected void defineSynchedData() {
        this.entityData.define(HOLDING_INSTRUMENT, false);
        this.entityData.define(IS_MUSICAL, false);
        this.entityData.define(CONDUCTOR_ID, Optional.empty());
        this.entityData.define(COSTUME_ITEM, ItemStack.EMPTY);
        this.entityData.define(HAT_ITEM, ItemStack.EMPTY);
        super.defineSynchedData();
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

        compound.put("Inventory", this.inventory.serializeNBT());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);

        this.entityData.set(HOLDING_INSTRUMENT, compound.getBoolean("HoldingInstrument"));
        this.entityData.set(IS_MUSICAL, compound.getBoolean("IsMusical"));

        if (compound.contains("Inventory")) {
            this.inventory.deserializeNBT(compound.getCompound("Inventory"));
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
                        new ItemStack(instrument.get(), 1)));
            }
        }
        return super.hurt(source, amount);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (isTame() && getOwnerUUID().equals(player.getUUID())) {
            if (itemStack.is(ModItems.PROP_CASE.get())) {

                this.openCustomMenu(player);
                return InteractionResult.SUCCESS;

            } else if (itemStack.is(ModTags.Items.IS_BATON) && !isPlayingInstrument() && !itemStack.getOrCreateTag().hasUUID(ModDataComponents.MUSICIAN_UUID)) {

                itemStack.getOrCreateTag().putUUID(ModDataComponents.MUSICIAN_UUID, this.uuid);
                if (!level().isClientSide()) {
                    ((ServerLevel) level()).sendParticles(ParticleTypes.WAX_OFF, getX(), getY()+0.5f, getZ(), 20, 0.2, 0.2, 0.2, 0.05);
                }
                return InteractionResult.SUCCESS;

            } else if (itemStack.is(instrument.get()) && !isHoldingInstrument()) {

                onSetInstrument();
                player.setItemInHand(hand, ItemStack.EMPTY);

                return InteractionResult.CONSUME;

            } else if (itemStack.isEmpty() && isHoldingInstrument()) {

                setHoldingInstrument(false);
                player.setItemInHand(hand, new ItemStack(instrument.get(), 1));
                setOrderedToSit(false);
                return InteractionResult.SUCCESS;

            } else if (itemStack.is(ModItems.BRIEFCASE.get()) && itemStack.hasTag() && itemStack.getTag().getBoolean(ModDataComponents.OPENED)
                    && getOwnerUUID().equals(player.getUUID())) {

                CompoundTag itemTag = itemStack.getOrCreateTag();
                ListTag entityList;

                // We get the list if there is one
                if (itemTag.contains(ModDataComponents.BRIEFCASE_ANIMAL_LIST, Tag.TAG_LIST)) {
                    entityList = itemTag.getList(ModDataComponents.BRIEFCASE_ANIMAL_LIST, Tag.TAG_COMPOUND);
                } else {
                    entityList = new ListTag();
                }

                if (entityList.size() < BriefcaseItem.MAX_CAPACITY) {
                    // Tag for the new entity data
                    CompoundTag entityData = new CompoundTag();

                    if (this.save(entityData)) {
                        ResourceLocation key = ForgeRegistries.ENTITY_TYPES.getKey(this.getType());
                        entityData.putString("id", key.toString());

                        // Remove the UUID
                        entityData.remove("UUID");

                        // We save the custom name if it has one
                        if (this.hasCustomName()) {
                            entityData.putString("DisplayName", this.getCustomName().getString());
                        }

                        // Add to list
                        entityList.add(entityData);
                        itemTag.put(ModDataComponents.BRIEFCASE_ANIMAL_LIST, entityList);

                        // Play sound and particles
                        level().playSound(player, this.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.BLOCKS);

                        if (!level().isClientSide()) {
                            ((ServerLevel) level()).sendParticles(ParticleTypes.PORTAL,
                                    this.getX(), this.getY(), this.getZ(),
                                    60, 0.5, 0.5, 0.5, 0F);
                        }

                        // Remove musician from orchestra
                        if (isPlayingInstrument() && this.getConductor() != null) {
                            this.getConductor().removeMusician(this);
                        }

                        // Close the briefcase if full
                        if (entityList.size() == BriefcaseItem.MAX_CAPACITY) {
                            itemStack.getOrCreateTag().putBoolean(ModDataComponents.OPENED, false);
                        }

                        // Eliminate entity
                        this.discard();

                        return InteractionResult.SUCCESS;
                    }

                }
            }
        }
        return InteractionResult.FAIL;
    }

    private void openCustomMenu(Player player) {
        if (!this.level().isClientSide() && player instanceof ServerPlayer serverPlayer) {

            SimpleMenuProvider menuProvider = new SimpleMenuProvider(
                    (id, playerInventory, playerEntity) -> new MusicianMenu(id, playerInventory, this),
                    this.getDisplayName()
            );

            NetworkHooks.openScreen(serverPlayer, menuProvider, buf -> {
                buf.writeUUID(this.getUUID());
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
        if (level().getRandom().nextInt(3) == 0 && !ForgeEventFactory.onAnimalTame(this, player)) {
            this.tame(player);
            this.navigation.stop();
            this.setTarget(null);
            this.playSound(ModSounds.SUCCESSFUL_TAME.get());
            this.level().broadcastEntityEvent(this, (byte) 7);
            if (!level().isClientSide()) {
                ModAdvancements.TAME_MUSICIAN.trigger((ServerPlayer) player);
            }
        } else {
            this.level().broadcastEntityEvent(this, (byte) 6);
        }
    }

    @Override
    public @Nullable <T extends Mob> T convertTo(EntityType<T> entityType, boolean transferInventory) {
        T mob = super.convertTo(entityType, transferInventory);
        if (mob instanceof MusicalEntity musicalEntity) {
            musicalEntity.setOwnerUUID(this.getOwnerUUID());
            musicalEntity.setTame(this.isTame());
            musicalEntity.setMusical(isMusical());
            musicalEntity.setConductor(getConductor());
            musicalEntity.setHoldingInstrument(isHoldingInstrument());
            musicalEntity.entityData.set(COSTUME_ITEM, this.entityData.get(COSTUME_ITEM));
            musicalEntity.entityData.set(HAT_ITEM, this.entityData.get(HAT_ITEM));
            musicalEntity.inventory = this.inventory;
        }

        return mob;
    }

    protected MusicalEntity createBaby(EntityType<? extends MusicalEntity> entityType, MusicalEntity parent) {
        MusicalEntity baby = entityType.create(level());

        if (baby != null) {
            boolean isMusical = this.isMusical();
            boolean isParentMusical = parent.isMusical();

            if (isMusical != isParentMusical) {
                baby.setMusical(random.nextBoolean());
            } else {
                baby.setMusical(isMusical);
            }
        }

        return baby;
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

    public void setMusical(boolean isMusical) {
        this.entityData.set(IS_MUSICAL, isMusical);
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

    public void playSpecialClothingAnimation(ItemStack stack) {

    }

    public void onEat(ItemEntity targetEntity) {
        Player owner = targetEntity.getOwner() instanceof Player player ? player : null;
        ItemStack stack = targetEntity.getItem();

        this.setInLove(owner);

        this.level().playSound(null, this.blockPosition(), SoundEvents.CAMEL_EAT, SoundSource.NEUTRAL);
        if (this.level() instanceof ServerLevel serverLevel) {
            Vec3 look = this.getLookAngle();
            double x = this.getX() + look.x * 0.5;
            double y = this.getEyeY() - 0.15;
            double z = this.getZ() + look.z * 0.5;

            serverLevel.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, stack),
                    x, y, z, 3, 0.1, 0.1, 0.1, 0.05
            );
        }

        stack.shrink(1);
        if (stack.isEmpty()) {
            targetEntity.discard();
        }
    }

    protected void onSetInstrument() {
        setHoldingInstrument(true);
        setOrderedToSit(true);

        searchForConductor();
    }

    public void searchForConductor() {
        Optional<ConductorEntity> potentialConductor = this.level()
                .getEntitiesOfClass(ConductorEntity.class, this.getBoundingBox().inflate(7))
                .stream()
                .filter(ConductorEntity::isHoldingBaton)
                .filter(ConductorEntity::isHoldingASheetMusic)
                .filter(cond -> cond.isMusicianApt(this))
                .filter(cond -> cond.getOrchestra().stream().noneMatch(this.getClass()::isInstance))
                .findAny();

        potentialConductor.ifPresent(this::setConductor);
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
}

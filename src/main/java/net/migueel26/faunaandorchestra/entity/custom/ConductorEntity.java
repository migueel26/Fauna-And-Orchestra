package net.migueel26.faunaandorchestra.entity.custom;

import net.migueel26.faunaandorchestra.block.ModBlocks;
import net.migueel26.faunaandorchestra.block.custom.ComposerGravestoneBlock;
import net.migueel26.faunaandorchestra.block.entity.ComposerGravestoneBlockEntity;
import net.migueel26.faunaandorchestra.component.ModDataComponents;
import net.migueel26.faunaandorchestra.entity.ModEntities;
import net.migueel26.faunaandorchestra.entity.custom.boss.TheGreatComposer;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.particles.ModParticleTypes;
import net.migueel26.faunaandorchestra.screen.custom.ConductorMenu;
import net.migueel26.faunaandorchestra.util.ModTags;
import net.migueel26.faunaandorchestra.util.MusicUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.*;

public abstract class ConductorEntity extends TamableAnimal {
    protected static final EntityDataAccessor<Boolean> HOLDING_BATON = SynchedEntityData.defineId(ConductorEntity.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Boolean> IS_LEGENDARY_BATON = SynchedEntityData.defineId(ConductorEntity.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Boolean> IS_MUSICAL = SynchedEntityData.defineId(ConductorEntity.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Boolean> IS_CONDUCTING = SynchedEntityData.defineId(ConductorEntity.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Boolean> IS_READY = SynchedEntityData.defineId(ConductorEntity.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Float> VOLUME = SynchedEntityData.defineId(ConductorEntity.class, EntityDataSerializers.FLOAT);
    protected boolean holdingBaton = false;
    protected boolean isConducting = false;
    // isReady -> It becomes true when a tamed ConductorEntity is clicked for the first time
    protected boolean isReady;
    // Server
    protected Set<MusicalEntity> orchestra = new HashSet<>();
    private float currentVolume = 1.0F;

    // Client
    private boolean particlesActivated;
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
    protected BlockPos composerGrave = null;

    public ConductorEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);

        this.particlesActivated = true;
        this.isReady = false;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(HOLDING_BATON, false);
        builder.define(IS_LEGENDARY_BATON, false);
        builder.define(IS_CONDUCTING, false);
        builder.define(IS_READY, false);
        builder.define(IS_MUSICAL, false);
        builder.define(VOLUME, 1.0F);
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

        if (IS_READY.equals(key)) {
            this.isReady = this.entityData.get(IS_READY);
        }

        if (VOLUME.equals(key)) {
            this.currentVolume = this.entityData.get(VOLUME);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);

        this.entityData.set(HOLDING_BATON, compound.getBoolean("HoldingBaton"));
        this.entityData.set(IS_LEGENDARY_BATON, compound.getBoolean("LegendaryBaton"));
        this.entityData.set(IS_READY, compound.getBoolean("IsReady"));
        this.entityData.set(VOLUME, compound.getFloat("Volume"));

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
        compound.putBoolean("LegendaryBaton", this.isHoldingLegendaryBaton());
        compound.putBoolean("IsReady", isConducting());
        compound.putFloat("Volume", currentVolume);

        if (!this.inventory.getStackInSlot(0).isEmpty()) {
            compound.put("SheetMusic", this.inventory.getStackInSlot(0).save(this.registryAccess()));
        }
    }

    @Override
    public void tick() {
        if (isTame() && isConducting()) {
            if (!level().isClientSide()) {
                // Server
                if (!isOrchestraEmpty()) {
                    ticksPlaying++;
                } else {
                    ticksPlaying = 0;
                }
            } else {
                // Client
                if (particlesActivated) {
                    if (ticksPlaying == 0) {
                        level().addParticle(ModParticleTypes.TREBLE_CLEF.get(),
                                this.getX(), this.getY() + 2.5, this.getZ(),
                                0, 0.025F, 0);
                    } else if (ticksPlaying % 15 == 0) {
                        level().addParticle(ModParticleTypes.FAUNA_NOTES.get(),
                                this.getX(), this.getY() + 2.5, this.getZ(),
                                0,0.025F,0);
                    }
                    ticksPlaying++;
                } else {
                    ticksPlaying = 0;
                }
            }

            // RESURRECTION
            if (getSheetMusic() == ModItems.RESURRECTION_SONG.get()) {
                if (ticksPlaying >= 1 && ticksPlaying <= 5) {
                    Optional<BlockPos> candidate = BlockPos.findClosestMatch(blockPosition(), 7, 7, pos -> level().getBlockState(pos).is(ModBlocks.COMPOSER_GRAVESTONE));
                    candidate.ifPresent(pos -> this.composerGrave = pos);
                }
            }

            if (composerGrave != null && getSheetMusic() == ModItems.RESURRECTION_SONG.get() && isOrchestraFull()) {
                if (ticksPlaying > 5) {
                    if (ticksPlaying % 10 == 0 && !level().isClientSide()) {
                        // Add particles periodically
                        ((ServerLevel) level()).sendParticles(ParticleTypes.SOUL_FIRE_FLAME, composerGrave.getCenter().x, composerGrave.getY(), composerGrave.getCenter().z,
                                20, 1,0.3, 1, 0.01);
                    }
                }

                if (ticksPlaying == 2500) {
                    BlockState graveState = level().getBlockState(composerGrave);
                    if (graveState.is(ModBlocks.COMPOSER_GRAVESTONE) && !graveState.getValue(ComposerGravestoneBlock.OPENED)
                            && level().getBlockEntity(composerGrave) instanceof ComposerGravestoneBlockEntity composerGravestoneBE) {
                        // START SUMMONING
                        if (!level().isClientSide()) {
                            // We spawn a lightning bolt
                            EntityType.LIGHTNING_BOLT.spawn((ServerLevel) level(), composerGrave, MobSpawnType.MOB_SUMMONED);
                        }
                        // Destroy sheet
                        this.inventory.setStackInSlot(0, ItemStack.EMPTY);
                        this.ticksPlaying = 0;
                        // Update block
                        level().setBlock(composerGrave, graveState.setValue(ComposerGravestoneBlock.OPENED, true), 3);

                        // Sound and particles
                        level().playSound(null, composerGrave, SoundEvents.GRINDSTONE_USE, SoundSource.BLOCKS, 1.0F, 0.5F);
                        // Open animation
                        composerGravestoneBE.open();
                        if (!level().isClientSide()) {

                            ((ServerLevel) level()).sendParticles(new BlockParticleOption(ParticleTypes.FALLING_DUST, Blocks.STONE.defaultBlockState()),
                                    composerGrave.getX(), composerGrave.getY() + 0.5F, composerGrave.getZ(), 20,
                                    0.2, 0, 0.2, 1.0F);

                        }

                        // Spawn TGC
                        TheGreatComposer theGreatComposer = new TheGreatComposer(ModEntities.THE_GREAT_COMPOSER.get(), level());
                        theGreatComposer.setPos(composerGrave.getCenter().x, composerGrave.below().getY(), composerGrave.getCenter().z);
                        theGreatComposer.setSpawnPos(composerGrave.below());
                        theGreatComposer.setYHeadRot(getYRot(graveState.getValue(ComposerGravestoneBlock.FACING)));
                        theGreatComposer.setYBodyRot(theGreatComposer.getYHeadRot());

                        level().addFreshEntity(theGreatComposer);
                    }
                }
            }
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

                Item item = isHoldingLegendaryBaton() ? ModItems.LEGENDARY_BATON.get() : ModItems.BATON.get();
                player.setItemInHand(hand, new ItemStack(item, 1));
                setHoldingBaton(false);
                setLegendaryBaton(false);
                setOrderedToSit(false);
                return InteractionResult.SUCCESS;
                
            } else if (itemStack.is(ModItems.BATON) && !isHoldingBaton()) {

                level().addParticle(ParticleTypes.NOTE, this.getX(), this.getY() + 2.5, this.getZ(), 0F, 0.5F, 0F);
                player.setItemInHand(hand, ItemStack.EMPTY);
                setHoldingBaton(true);
                setLegendaryBaton(false);
                setOrderedToSit(true);
                return InteractionResult.CONSUME;

            } else if (itemStack.is(ModItems.LEGENDARY_BATON) && !isHoldingBaton()) {

                level().addParticle(ParticleTypes.NOTE, this.getX(), this.getY() + 2.5, this.getZ(), 0F, 0.5F, 0F);
                player.setItemInHand(hand, ItemStack.EMPTY);
                setHoldingBaton(true);
                setLegendaryBaton(true);
                setOrderedToSit(true);

                
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
        return InteractionResult.PASS;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!this.level().isClientSide) {
            if (isHoldingBaton() && isTame()) {
                setHoldingBaton(false);
                if (isHoldingLegendaryBaton()) {
                    setLegendaryBaton(false);
                    this.level().addFreshEntity(new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(),
                            new ItemStack((Holder<Item>) ModItems.LEGENDARY_BATON, 1)));
                } else {
                    this.level().addFreshEntity(new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(),
                            new ItemStack((Holder<Item>) ModItems.BATON, 1)));
                }

            }
            setInSittingPose(false);
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

    private float getYRot(Direction facing) {
        return switch (facing) {
            case NORTH -> 180f;
            case SOUTH -> 0f;
            case WEST  -> 90f;
            case EAST  -> -90f;
            default -> 0f;
        };
    }

    public void activateParticles(boolean particlesActivated) {
        this.particlesActivated = particlesActivated;
    }

    public boolean areParticlesActivated() {
        return this.particlesActivated;
    }

    public void setHoldingBaton(boolean holdingBaton) {
        this.entityData.set(HOLDING_BATON, holdingBaton);
    }

    public void setReady(boolean ready) {
        this.entityData.set(IS_READY, ready);
    }

    public void setMusical(boolean isMusical) {
        this.entityData.set(IS_MUSICAL, isMusical);
    }

    public boolean isMusical() {
        return this.entityData.get(IS_MUSICAL);
    }


    public boolean isHoldingBaton() {
        return holdingBaton;
    }

    public boolean isConducting() {
        return isConducting;
    }

    public boolean isReady() {
        return isReady;
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

    public void setConducting(boolean setConducting) {
        this.entityData.set(IS_CONDUCTING, setConducting);
        this.isConducting = setConducting;
    }

    public boolean isOrchestraEmpty() {
        return orchestra == null || orchestra.isEmpty();
    }

    public boolean isOrchestraFull() {
        return orchestra.size() == MusicUtil.getMaxSize(this.getSheetMusic());
    }

    public float getCurrentVolume() {
        return this.entityData.get(VOLUME);
    }

    public void setCurrentVolume(float currentVolume) {
        this.entityData.set(VOLUME, currentVolume);
    }

    public boolean isMusicianApt(MusicalEntity musician) {
        return MusicUtil.getInstruments(getSheetMusic()).contains(musician.getInstrument().get());
    }

    public boolean isHoldingLegendaryBaton() {
        return entityData.get(IS_LEGENDARY_BATON);
    }

    public void setLegendaryBaton(boolean legendaryBaton) {
        this.entityData.set(IS_LEGENDARY_BATON, legendaryBaton);
    }
}

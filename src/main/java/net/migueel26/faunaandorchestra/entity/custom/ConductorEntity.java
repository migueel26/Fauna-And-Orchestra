package net.migueel26.faunaandorchestra.entity.custom;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.advancements.ModAdvancements;
import net.migueel26.faunaandorchestra.block.ModBlocks;
import net.migueel26.faunaandorchestra.block.custom.ComposerGravestoneBlock;
import net.migueel26.faunaandorchestra.block.entity.ComposerGravestoneBlockEntity;
import net.migueel26.faunaandorchestra.component.ModDataComponents;
import net.migueel26.faunaandorchestra.effect.ModEffects;
import net.migueel26.faunaandorchestra.entity.ModEntities;
import net.migueel26.faunaandorchestra.entity.custom.boss.TheGreatComposer;
import net.migueel26.faunaandorchestra.entity.custom.projectile.PhantomNoteProjectileEntity;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.particles.ModParticleTypes;
import net.migueel26.faunaandorchestra.screen.custom.ConductorMenu;
import net.migueel26.faunaandorchestra.util.ModTags;
import net.migueel26.faunaandorchestra.util.MusicUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.*;

public abstract class ConductorEntity extends TamableAnimal {
    protected static final EntityDataAccessor<Boolean> HOLDING_BATON = SynchedEntityData.defineId(ConductorEntity.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Boolean> IS_LEGENDARY_BATON = SynchedEntityData.defineId(ConductorEntity.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Boolean> IS_MUSICAL = SynchedEntityData.defineId(ConductorEntity.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Boolean> IS_CONDUCTING = SynchedEntityData.defineId(ConductorEntity.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Boolean> IS_READY = SynchedEntityData.defineId(ConductorEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<ItemStack> COSTUME_ITEM = SynchedEntityData.defineId(ConductorEntity.class, EntityDataSerializers.ITEM_STACK);
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
    public ItemStackHandler inventory = new ItemStackHandler(2) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            // Slot 0 -> SheetMusic
            // Slot 1 -> Is_Costume ItemTag and Wears_ITEMSTACK EntityTypeTag
            return (slot == 0 && stack.is(ModTags.Items.SHEET_MUSIC) ||
                    (slot == 1 && stack.is(ModTags.Items.IS_COSTUME) && (getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "wears_" + stack.getItem().getDescriptionId().split("\\.")[2]))))));
        }

        @Override
        protected int getStackLimit(int slot, ItemStack stack) {
            return 1;
        }

        @Override
        protected void onContentsChanged(int slot) {

            if (slot == 1) {
                entityData.set(COSTUME_ITEM, getStackInSlot(1));
                playSound(SoundEvents.ARMOR_EQUIP_LEATHER.value(), 1.0F, 1.0F + ((random.nextFloat() / 2) - 0.25F));
            }

            super.onContentsChanged(slot);
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
        builder.define(COSTUME_ITEM, ItemStack.EMPTY);
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

        if (compound.contains("Inventory")) {
            this.inventory.deserializeNBT(this.registryAccess(), compound.getCompound("Inventory"));
            this.entityData.set(COSTUME_ITEM, this.inventory.getStackInSlot(1));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);

        compound.putBoolean("HoldingBaton", this.isHoldingBaton());
        compound.putBoolean("LegendaryBaton", this.isHoldingLegendaryBaton());
        compound.putBoolean("IsReady", isConducting());
        compound.putFloat("Volume", currentVolume);

        compound.put("Inventory", this.inventory.serializeNBT(this.registryAccess()));
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
                                0, 0.025F, 0);
                    }
                    ticksPlaying++;
                } else {
                    ticksPlaying = 0;
                }
            }

            // RESURRECTION
            if (getSheetMusic() == ModItems.RESURRECTION_SONG.get()) {
                tryToResurrect();
            }

            // MAGIC
            if (isHoldingLegendaryBaton() && isHoldingASheetMusic() && isOrchestraFull()) {
                tryToApplyLegendaryEffect();
            }

            // WANDERING NOTES
            List<Player> players = level().getEntitiesOfClass(Player.class, this.getAttackBoundingBox().inflate(15));
            if (ticksPlaying % 30 == 0 && players.stream().anyMatch(player -> player.hasEffect(ModEffects.ABSOLUTE_HEARING))) {
                tryToSummonWanderingNote();
            }
        }

        super.tick();
    }

    private void tryToSummonWanderingNote() {
        WanderingNoteEntity entity = new WanderingNoteEntity(ModEntities.WANDERING_NOTE.get(), level());
        int x = this.getRandom().nextInt(9) - 4;
        int z = this.getRandom().nextInt(1, 3);
        int y = this.getRandom().nextInt(9) - 4;

        entity.moveTo(this.getX() + x, this.getY() + y, this.getZ() + z);
        this.level().addFreshEntity(entity);

        entity.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0f, 1.5f);
    }

    private void tryToResurrect() {
        if (ticksPlaying >= 1 && ticksPlaying <= 5) {
            Optional<BlockPos> candidate = BlockPos.findClosestMatch(blockPosition(), 7, 7, pos -> level().getBlockState(pos).is(ModBlocks.COMPOSER_GRAVESTONE) &&
                    !level().getBlockState(pos).getValue(ComposerGravestoneBlock.OPENED));
            candidate.ifPresent(pos -> this.composerGrave = pos);
        }


        if (composerGrave != null && isOrchestraFull()) {
            if (ticksPlaying > 5) {
                if (ticksPlaying % 10 == 0 && !level().isClientSide()) {
                    // Add particles periodically
                    ((ServerLevel) level()).sendParticles(ParticleTypes.SOUL_FIRE_FLAME, composerGrave.getCenter().x, composerGrave.getY(), composerGrave.getCenter().z,
                            20, 1, 0.3, 1, 0.01);
                }
            }

            if (ticksPlaying == 2500) {
                BlockState graveState = level().getBlockState(composerGrave);
                if (graveState.is(ModBlocks.COMPOSER_GRAVESTONE) && !graveState.getValue(ComposerGravestoneBlock.OPENED)
                        && level().getBlockEntity(composerGrave) instanceof ComposerGravestoneBlockEntity composerGravestoneBE) {
                    startResurrection(composerGravestoneBE, graveState);
                }
            }
        }
    }

    private void startResurrection(ComposerGravestoneBlockEntity composerGravestoneBE, BlockState graveState) {
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

    private void tryToApplyLegendaryEffect() {
        Item sheetMusic = getSheetMusic();

        if (level().isClientSide()) return; // Only run on the server

        ServerLevel serverLevel = (ServerLevel) level();

        if (sheetMusic.equals(ModItems.BLUES_SHEET_MUSIC.get())) {
            if (ticksPlaying == 10) {
                serverLevel.setDayTime(14000L); // night
                serverLevel.sendParticles(ParticleTypes.GLOW_SQUID_INK,
                        getX(), getY() + 10, getZ(), 100, 0.5, 10, 0.5, 0.05);
            }
        } else if (sheetMusic.equals(ModItems.BACH_AIR_SHEET_MUSIC.get())) {
            if (ticksPlaying == 10) {
                serverLevel.setDayTime(1000L); // early morning
                serverLevel.sendParticles(ParticleTypes.GLOW_SQUID_INK,
                        getX(), getY() + 10, getZ(), 100, 0.5, 10, 0.5, 0.05);
            }
        } else if (sheetMusic.equals(ModItems.GREENSLEEVES_SHEET_MUSIC.get())) {
            if (ticksPlaying % 40 == 0) {
                applyLegendaryOrchestraEffect(serverLevel, MobEffects.HEALTH_BOOST, 4);
            }
        } else if (sheetMusic.equals(ModItems.LA_BAMBA_SHEET_MUSIC.get())) {
            if (ticksPlaying % 40 == 0) {
                applyLegendaryOrchestraEffect(serverLevel, MobEffects.LUCK, 2);
            }
        } else if (sheetMusic.equals(ModItems.JAZZY_FUR_ELISE_SHEET_MUSIC.get())) {
            if (ticksPlaying % 40 == 0) {
                applyLegendaryOrchestraEffect(serverLevel, MobEffects.SATURATION, 2);
            }
        } else if (sheetMusic.equals(ModItems.DANCE_OF_THE_LITTLE_SWANS.get())) {
            if (ticksPlaying % 20 == 0) {
                Player owner = level().getPlayerByUUID(getOwnerUUID());
                Monster target = level().getNearestEntity(
                        Monster.class,
                        TargetingConditions.forCombat().range(15.0), // optional: limit distance
                        owner,
                        getX(), getY(), getZ(),
                        getBoundingBox().inflate(15)
                );

                if (target != null) {
                    Vec3 direction = target.position()
                            .add(0, target.getBbHeight() * 0.5, 0)
                            .subtract(this.position().add(0, 2.5, 0))
                            .normalize()
                            .scale(0.35);

                    PhantomNoteProjectileEntity note = new PhantomNoteProjectileEntity(this, direction, level());
                    note.setGood(true);
                    note.moveTo(getX(), getY() + 1.75f, getZ());
                    note.setDeltaMovement(direction);
                    level().addFreshEntity(note);
                }
            }
        } else if (sheetMusic.equals(ModItems.RESURRECTION_SONG.get())) {
            if (ticksPlaying == 10) {
                List<PlayerCanonEntity> entities =  level().getEntitiesOfClass(PlayerCanonEntity.class, getBoundingBox().inflate(64),
                        entity -> entity.getOwnerUUID().equals(this.getOwnerUUID()));
                if (this.getOwner() instanceof Player player &&
                       entities.isEmpty()) {
                    PlayerCanonEntity playerCanon = new PlayerCanonEntity(ModEntities.PLAYER_CANON.get(), level());
                    playerCanon.setPos(player.position());
                    playerCanon.setConductor(this);
                    playerCanon.setOwnerUUID(player.getUUID());

                    ModAdvancements.PLAYER_CANON.get().trigger((ServerPlayer) player);

                    level().addFreshEntity(playerCanon);
                }
            }
        }
    }

    private void applyLegendaryOrchestraEffect(ServerLevel serverLevel, Holder<MobEffect> luck, int amplifier) {
        List<ServerPlayer> players = serverLevel.getPlayers(
                player -> player.distanceTo(this) <= 50
        );
        for (Player player : players) {
            player.addEffect(new MobEffectInstance(luck, 100, amplifier, true, true));
        }
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
            case WEST -> 90f;
            case EAST -> -90f;
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
    public Item getCostume() {
        return this.entityData.get(COSTUME_ITEM).getItem();
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
        if (!isConducting) this.entityData.set(IS_CONDUCTING, true);
        isConducting = true;
    }

    public void removeMusician(MusicalEntity musicalEntity) {
        orchestra.remove(musicalEntity);
        if (orchestra.isEmpty()) this.entityData.set(IS_CONDUCTING, false);
        isConducting = false;
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

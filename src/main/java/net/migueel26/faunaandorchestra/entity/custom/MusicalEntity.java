package net.migueel26.faunaandorchestra.entity.custom;

import net.migueel26.faunaandorchestra.advancements.ModAdvancements;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.item.custom.BatonItem;
import net.migueel26.faunaandorchestra.item.custom.BriefcaseItem;
import net.migueel26.faunaandorchestra.mixins.client.accessors.ClientLevelAccessor;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.migueel26.faunaandorchestra.util.ModTags;
import net.migueel26.faunaandorchestra.util.MusicUtil;
import net.minecraft.core.Holder;
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
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class MusicalEntity extends TamableAnimal {
    protected RegistryObject<Item> instrument;
    protected static final EntityDataAccessor<Boolean> HOLDING_INSTRUMENT = SynchedEntityData.defineId(MusicalEntity.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Optional<UUID>> CONDUCTOR_ID = SynchedEntityData.defineId(MusicalEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    protected static final EntityDataAccessor<Boolean> IS_MUSICAL = SynchedEntityData.defineId(MusicalEntity.class, EntityDataSerializers.BOOLEAN);
    protected boolean isHoldingInstrument;
    protected UUID conductorUUID;
    private int ticksSinceLoaded;
    //private Integer count = null;
    //private Player lastAttempt;

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
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);

        this.entityData.set(HOLDING_INSTRUMENT, compound.getBoolean("HoldingInstrument"));
        this.entityData.set(IS_MUSICAL, compound.getBoolean("IsMusical"));
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
        ItemStack stack = player.getItemInHand(hand);
        if (isTame() && getOwnerUUID().equals(player.getUUID())) {
            if (stack.is(ModTags.Items.IS_BATON) && !isPlayingInstrument() && BatonItem.getMusicianUUID(stack) == null) {

                BatonItem.setMusicianUUID(stack, this.uuid);
                return InteractionResult.SUCCESS;

            } else if (stack.is(instrument.get()) && !isHoldingInstrument()) {

                setHoldingInstrument(true);
                player.setItemInHand(hand, ItemStack.EMPTY);
                level().addParticle(ParticleTypes.NOTE, this.getX(), this.getY() + 2.5, this.getZ(), 0F, 0.5F, 0F);
                setOrderedToSit(true);
                return InteractionResult.CONSUME;

            } else if (stack.isEmpty() && isHoldingInstrument()) {

                setHoldingInstrument(false);
                player.setItemInHand(hand, new ItemStack(instrument.get(), 1));
                setOrderedToSit(false);
                return InteractionResult.SUCCESS;

            } else if (stack.is(ModItems.BRIEFCASE.get()) && BriefcaseItem.isOpened(stack)
                        && getOwnerUUID().equals(player.getUUID())) {
                List<String> animals = BriefcaseItem.getAnimalList(stack);

                if (!stack.hasTag()) {
                    // If it's not initialized, we store it
                    animals = new ArrayList<>(6);
                    BriefcaseItem.setAnimalList(stack, animals);
                }

                if (animals.size() < 6) {
                    if (!level().isClientSide()) {
                        List<String> newAnimals = new ArrayList<>(animals);
                        newAnimals.add(MusicUtil.musicalAnimalToString(this));
                        BriefcaseItem.setAnimalList(stack, newAnimals);

                        if (newAnimals.size() == 6) {
                            BriefcaseItem.setOpened(stack, false);
                        }

                        ((ServerLevel) level()).sendParticles(ParticleTypes.PORTAL,
                                this.getX(), this.getY(), this.getZ(),
                                60, 0.5, 0.5, 0.5, 0F);

                        if (isPlayingInstrument() && this.getConductor() != null) {
                            this.getConductor().removeMusician(this);
                        }

                        this.discard();
                    } else {
                        level().playSound(player, this.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.BLOCKS);
                    }
                    return InteractionResult.SUCCESS;
                } else {
                    return InteractionResult.FAIL;
                }


            }
        }
        return InteractionResult.FAIL;
    }

    /*
    @Override
    public void tick() {
        if (count != null) {
            count++;
            if (count == 15) {
                count = null;
                tryToTame(lastAttempt);
            }
        }

        super.tick();
    }

    public void tameEvent(Player player) {
        count = 0;
        lastAttempt = player;
    }
    */

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

    public void setConductor(ConductorEntity conductor) {
        UUID conductorUUID = conductor == null ? null : conductor.getUUID();

        this.entityData.set(CONDUCTOR_ID, Optional.ofNullable(conductorUUID));
    }

    public int getTicksSinceLoaded() {
        return ticksSinceLoaded;
    }
}

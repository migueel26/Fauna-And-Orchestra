package net.migueel26.faunaandorchestra.entity.custom;

import net.migueel26.faunaandorchestra.mixins.client.accessors.ClientLevelAccessor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public abstract class MusicalEntity extends TamableAnimal {
    protected DeferredItem<Item> instrument;
    protected static final EntityDataAccessor<Boolean> HOLDING_INSTRUMENT = SynchedEntityData.defineId(MusicalEntity.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Optional<UUID>> CONDUCTOR_ID = SynchedEntityData.defineId(MusicalEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    protected static final EntityDataAccessor<Boolean> IS_MUSICAL = SynchedEntityData.defineId(MusicalEntity.class, EntityDataSerializers.BOOLEAN);
    protected boolean isHoldingInstrument;
    protected ConductorEntity conductor;
    //private Integer count = null;
    //private Player lastAttempt;

    protected MusicalEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
        this.instrument = getInstrument();
        this.conductor = null;
    }

    protected abstract DeferredItem<Item> getInstrument();

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(HOLDING_INSTRUMENT, false);
        builder.define(IS_MUSICAL, false);
        builder.define(CONDUCTOR_ID, Optional.empty());
        super.defineSynchedData(builder);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (HOLDING_INSTRUMENT.equals(key)) {
            this.isHoldingInstrument = this.entityData.get(HOLDING_INSTRUMENT);
        }

        if (CONDUCTOR_ID.equals(key)) {
            UUID conductorUUID = this.entityData.get(CONDUCTOR_ID).orElse(null);
            if (this.level().isClientSide()) {
                this.conductor = conductorUUID == null ? null : (ConductorEntity) ((ClientLevelAccessor) level()).callGetEntities().get(conductorUUID);
            } else {
                this.conductor = conductorUUID == null ? null : (ConductorEntity) ((ServerLevel) level()).getEntity(conductorUUID);
            }


        }

        super.onSyncedDataUpdated(key);
    }

    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        float random = this.random.nextFloat();
        if (random <= 0.2F) {
            entityData.set(IS_MUSICAL, true);
        }
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!this.level().isClientSide) {
            if (isHoldingInstrument() && isTame()) {
                setHoldingInstrument(false);
                this.level().addFreshEntity(new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(),
                        new ItemStack((Holder<Item>) instrument, 1)));
            }
        }
        return super.hurt(source, amount);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (isOwnedBy(player)) {
            if (itemStack.is(instrument)) {

                setHoldingInstrument(true);
                player.setItemInHand(hand, ItemStack.EMPTY);
                level().addParticle(ParticleTypes.NOTE, this.getX(), this.getY() + 2.5, this.getZ(), 0F, 0.5F, 0F);
                return InteractionResult.CONSUME;

            } else if (itemStack.isEmpty()) {

                setHoldingInstrument(false);
                player.setItemInHand(hand, new ItemStack(instrument.get(), 1));
                return InteractionResult.SUCCESS;

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

    public void tryToTame(Player player) {
        if (level().getRandom().nextInt(3) == 0 && !net.neoforged.neoforge.event.EventHooks.onAnimalTame(this, player)) {
            this.tame(player);
            this.navigation.stop();
            this.setTarget(null);
            this.level().broadcastEntityEvent(this, (byte) 7);
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
        return conductor != null;
    }

    public boolean isMusical() {
        return this.entityData.get(IS_MUSICAL);
    }

    public @Nullable ConductorEntity getConductor() {
        return conductor;
    }

    public void setConductor(ConductorEntity conductor) {
        if (conductor == null) {
            this.entityData.set(CONDUCTOR_ID, Optional.empty());
        } else {
            this.entityData.set(CONDUCTOR_ID, Optional.of(conductor.getUUID()));
        }
    }
}

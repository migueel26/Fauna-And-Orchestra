package net.migueel26.faunaandorchestra.entity.custom;

import net.migueel26.faunaandorchestra.item.ModItems;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

import java.util.HashSet;
import java.util.Set;

public abstract class ConductorEntity extends Animal {
    protected static final EntityDataAccessor<Boolean> HOLDING_BATON = SynchedEntityData.defineId(ConductorEntity.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Boolean> IS_CONDUCTOR = SynchedEntityData.defineId(ConductorEntity.class, EntityDataSerializers.BOOLEAN);
    protected boolean holdingBaton = false;
    protected Set<MusicalEntity> orchestra = new HashSet<>();
    protected Item sheetMusic = ModItems.BACH_AIR_SHEET_MUSIC.asItem();
    protected int ticksPlaying = 0;

    public ConductorEntity(EntityType<? extends Animal> entityType, Level level) {
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
    public void tick() {
        if (holdingBaton) {
            this.getNavigation().stop();
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
        setHoldingBaton(!isHoldingBaton());
        return InteractionResult.SUCCESS;
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
        return sheetMusic;
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

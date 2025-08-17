package net.migueel26.faunaandorchestra.entity.custom;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public abstract class TravellingMusician extends AgeableMob {
    protected static final EntityDataAccessor<Boolean> IS_PLAYING = SynchedEntityData.defineId(TravellingMusician.class, EntityDataSerializers.BOOLEAN);

    protected TravellingMusician(EntityType<? extends AgeableMob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(IS_PLAYING, false);
    }

    public void setPlaying(boolean playing) {
        this.entityData.set(IS_PLAYING, playing);
    }

    public boolean isPlaying() {
        return this.entityData.get(IS_PLAYING);
    }
}

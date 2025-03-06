package net.migueel26.faunaandorchestra.sound.custom;

import net.migueel26.faunaandorchestra.entity.custom.MusicalEntity;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

public class InstrumentSoundInstance extends AbstractTickableSoundInstance {
    private MusicalEntity entity;
    private int sourceID;
    private Integer ticksOffset;
    public InstrumentSoundInstance(MusicalEntity entity, SoundEvent soundEvent, int ticksOffset) {
        super(soundEvent, SoundSource.NEUTRAL, SoundInstance.createUnseededRandom());
        this.ticksOffset = ticksOffset;
        this.entity = entity;
        this.looping = true;
        this.delay = 0;
        this.volume = 2.0F;
        this.relative = true;
    }

    @Override
    public void tick() {
        if (entity.isRemoved() || entity.getConductor() == null || !entity.isHoldingInstrument()) {
            stop();
        }
    }

    public void stopSound() {
        super.stop();
    }

    public MusicalEntity getEntity() {
        return entity;
    }

    public int getSourceID() {
        return sourceID;
    }

    public void setSourceID(int sourceID) {
        this.sourceID = sourceID;
    }

    public Integer getTicksOffset() {
        return ticksOffset;
    }

    public void setTicksOffset(Integer ticksOffset) {
        this.ticksOffset = ticksOffset;
    }
}

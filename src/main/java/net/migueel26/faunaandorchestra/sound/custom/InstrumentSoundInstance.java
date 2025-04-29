package net.migueel26.faunaandorchestra.sound.custom;

import net.migueel26.faunaandorchestra.entity.custom.MusicalEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.SuspendedParticle;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

import javax.swing.text.html.parser.Entity;

public class InstrumentSoundInstance extends AbstractTickableSoundInstance {
    private MusicalEntity entity;
    private int sourceID;
    private Integer ticksOffset;
    private float oVolume;
    private int stopDelay = 5;
    public InstrumentSoundInstance(MusicalEntity entity, SoundEvent soundEvent, float volume, int ticksOffset) {
        super(soundEvent, SoundSource.NEUTRAL, SoundInstance.createUnseededRandom());
        this.ticksOffset = ticksOffset;
        this.entity = entity;
        this.looping = true;
        this.attenuation = Attenuation.LINEAR;
        this.delay = 0;
        this.oVolume = 1.5F * volume;
        this.volume = oVolume;
        this.x = entity.getX();
        this.y = entity.getY();
        this.z = entity.getZ();
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }

    @Override
    public void tick() {
        if (!entity.isPlayingInstrument() || entity.isRemoved()) {
            // There needs to be a little delay for the server and client to be synchronised
            if (stopDelay > 0) {
                stopDelay--;
            } else {
                stopSound();
            }
        } else {
            stopDelay = 5;

            // Didn't work as intended?
            this.x = this.entity.getX();
            this.y = this.entity.getY();
            this.z = this.entity.getZ();

            double distance = Minecraft.getInstance().player.distanceTo(entity);

            this.volume = (float) Math.max(0, oVolume - (distance * 0.05F));
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
}

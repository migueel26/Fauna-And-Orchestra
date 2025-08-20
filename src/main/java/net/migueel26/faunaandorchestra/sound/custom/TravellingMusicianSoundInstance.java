package net.migueel26.faunaandorchestra.sound.custom;

import net.migueel26.faunaandorchestra.entity.custom.TravellingMusician;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

import java.util.UUID;

public class TravellingMusicianSoundInstance extends AbstractTickableSoundInstance {
    TravellingMusician musician;
    private int stopDelay = 5;
    public TravellingMusicianSoundInstance(SoundEvent soundEvent, TravellingMusician musician) {
        super(soundEvent, SoundSource.NEUTRAL, SoundInstance.createUnseededRandom());
        this.musician = musician;
        this.looping = true;
        this.attenuation = Attenuation.LINEAR;
        this.volume = calculateVolume();
        this.x = musician.getX();
        this.y = musician.getY();
        this.z = musician.getZ();
    }

    @Override
    public void tick() {
        System.out.println(volume);
        if (!musician.isPlaying() || musician.isRemoved()) {
            if (stopDelay > 0) {
                stopDelay--;
            } else {
                stopSound();
            }
        } else {
            this.stopDelay = 5;

            this.x = musician.getX();
            this.y = musician.getY();
            this.z = musician.getZ();

            this.volume = calculateVolume();
        }
    }

    private float calculateVolume() {
        double distance = Minecraft.getInstance().player.distanceTo(musician);

        return (float) Math.max(0, 1.1F - (distance * 0.03125F));
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }

    public void stopSound() {
        super.stop();
    }

    public UUID getUUID() {
        return musician.getUUID();
    }
}

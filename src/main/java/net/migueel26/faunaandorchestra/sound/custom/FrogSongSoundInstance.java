package net.migueel26.faunaandorchestra.sound.custom;

import net.migueel26.faunaandorchestra.entity.custom.QuirkyFrogEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

import java.util.UUID;

public class FrogSongSoundInstance extends AbstractTickableSoundInstance {
    private final QuirkyFrogEntity quirkyFrog;
    public FrogSongSoundInstance(SoundEvent soundEvent, QuirkyFrogEntity quirkyFrog) {
        super(soundEvent, SoundSource.NEUTRAL, SoundInstance.createUnseededRandom());
        this.quirkyFrog = quirkyFrog;
        this.attenuation = Attenuation.LINEAR;
        this.volume = 1.0F;
        this.x = quirkyFrog.getX();
        this.y = quirkyFrog.getY();
        this.z = quirkyFrog.getZ();
    }

    @Override
    public void tick() {
        if (quirkyFrog.isDeadOrDying()) {
            this.stop();
        }

        this.x = quirkyFrog.getX();
        this.y = quirkyFrog.getY();
        this.z = quirkyFrog.getZ();
    }

    public void stopSound() {
        this.stop();
    }

    public UUID getFrogUUID() {
        return quirkyFrog.getUUID();
    }
}

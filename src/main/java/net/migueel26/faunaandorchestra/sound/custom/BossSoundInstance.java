package net.migueel26.faunaandorchestra.sound.custom;

import net.migueel26.faunaandorchestra.entity.custom.TravellingMusician;
import net.migueel26.faunaandorchestra.entity.custom.boss.TheGreatComposer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;

public class BossSoundInstance extends AbstractTickableSoundInstance {
    LivingEntity livingEntity;
    private int stopDelay = 5;
    public BossSoundInstance(SoundEvent soundEvent, LivingEntity livingEntity) {
        super(soundEvent, SoundSource.MUSIC, SoundInstance.createUnseededRandom());

        this.livingEntity = livingEntity;
        this.looping = true;
        this.volume = 0.5f;
    }

    @Override
    public void tick() {
        if (livingEntity instanceof TheGreatComposer theGreatComposer &&
                (theGreatComposer.isDeadOrDying() || theGreatComposer.isRemoved() || theGreatComposer.isFakeDead())) {
            if (stopDelay > 0) {
                stopDelay--;
            } else {
                stopSound();
            }
        }
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }

    public void stopSound() {
        super.stop();
    }
}

package net.migueel26.faunaandorchestra.sound.custom;

import net.migueel26.faunaandorchestra.entity.custom.MusicalEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

public class PlayerInstrumentSoundInstance extends AbstractTickableSoundInstance {
    private Player player;
    private int sourceID;
    private int stopDelay = 3;

    public PlayerInstrumentSoundInstance(Player player, SoundEvent soundEvent, float volume) {
        super(soundEvent, SoundSource.NEUTRAL, SoundInstance.createUnseededRandom());
        this.player = player;
        this.looping = true;
        this.attenuation = Attenuation.LINEAR;
        this.delay = 0;
        this.volume = volume;
        this.x = player.getX();
        this.y = player.getY();
        this.z = player.getZ();
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }

    @Override
    public void tick() {
        if (!player.isUsingItem() || player.isRemoved()) {
            if (stopDelay > 0) {
                stopDelay--;
            } else {
                stopSound();
            }
        } else {
            stopDelay = 3;

            this.x = this.player.getX();
            this.y = this.player.getY();
            this.z = this.player.getZ();
        }
    }

    public void stopSound() {
        super.stop();
    }

    public Player getPlayer() {
        return player;
    }

    public int getSourceID() {
        return sourceID;
    }

    public void setSourceID(int sourceID) {
        this.sourceID = sourceID;
    }
}

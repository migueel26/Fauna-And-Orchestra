package net.migueel26.faunaandorchestra.mixins.interfaces;

import com.mojang.blaze3d.audio.SoundBuffer;
import net.minecraft.client.resources.sounds.SoundInstance;

public interface IChannelMixin {

    void faunaSetByteOffset(SoundInstance p_sound, SoundBuffer soundBuffer, Integer offset);

    void faunaSetTickOffset(SoundInstance soundInstance, SoundBuffer soundBuffer, Integer offset);
}

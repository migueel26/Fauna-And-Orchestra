package net.migueel26.faunaandorchestra.mixins.client;

import com.mojang.blaze3d.audio.Channel;
import com.mojang.blaze3d.audio.SoundBuffer;
import net.migueel26.faunaandorchestra.mixins.client.accessors.SoundBufferAccessor;
import net.migueel26.faunaandorchestra.mixins.interfaces.IChannelMixin;
import net.migueel26.faunaandorchestra.sound.custom.InstrumentSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.nio.IntBuffer;
import java.util.OptionalInt;

@Mixin(Channel.class)
public class MixinChannel implements IChannelMixin {
    @Shadow @Final private int source;

    @Override
    public void faunaSetByteOffset(SoundInstance soundInstance, SoundBuffer soundBuffer, Integer sourceID) {
        OptionalInt bufferID = ((SoundBufferAccessor) soundBuffer).callGetAlBuffer();

        if (bufferID.isPresent()) {
            AL10.alSourcei(source, AL10.AL_BUFFER, bufferID.getAsInt());

            IntBuffer bytesOffset = BufferUtils.createIntBuffer(1);
            if (sourceID != 0) {
                // GET THE BYTE OFFSET (from the source)
                AL10.alGetSourcei(sourceID, AL11.AL_BYTE_OFFSET, bytesOffset);
            }
            // WE "PUT" THEM
            bytesOffset.rewind();

            IntBuffer actualByteOffset = BufferUtils.createIntBuffer(1).put(sourceID != 0 ? bytesOffset.get() : 0);
            actualByteOffset.rewind();
            AL11.alSourceiv(source, AL11.AL_BYTE_OFFSET, actualByteOffset);

            if (soundInstance instanceof InstrumentSoundInstance instrumentSoundInstance) {
                instrumentSoundInstance.setSourceID(source);
            }
        }

    }

    @Override
    public void faunaSetTickOffset(SoundInstance soundInstance, SoundBuffer soundBuffer, Integer offset) {
        OptionalInt bufferID = ((SoundBufferAccessor) soundBuffer).callGetAlBuffer();
        if (bufferID.isEmpty()) return;
        AL10.alSourcei(this.source, AL10.AL_BUFFER, bufferID.getAsInt());

        // READ THE FREQUENCIES OF THE SOUND
        int frequency = getBufferOf(bufferID.getAsInt(), AL10.AL_FREQUENCY);
        int samplesToOffset = (int) ((offset / 20.0f) * frequency);

        int sizeInBytes = getBufferOf(bufferID.getAsInt(), AL10.AL_SIZE);
        int bitsPerSample = getBufferOf(bufferID.getAsInt(), AL10.AL_BITS);
        int channels = getBufferOf(bufferID.getAsInt(), AL10.AL_CHANNELS);
        int lengthInSamples = sizeInBytes * 8 / (bitsPerSample * channels);
        samplesToOffset = samplesToOffset % lengthInSamples;

        // Set the sample offset on the source
        IntBuffer sampleOffset = BufferUtils.createIntBuffer(1).put(samplesToOffset);
        sampleOffset.rewind();
        AL11.alSourceiv(this.source, AL11.AL_SAMPLE_OFFSET, sampleOffset);

        // Attach the source id to the sound instance.
        // This is so that other sounds can grab the byte offset from this sound when they start.
        if (soundInstance instanceof InstrumentSoundInstance instrumentSoundInstance) {
            instrumentSoundInstance.setSourceID(source);
        }
    }

    @Unique
    private int getBufferOf(int buffer, int al) {
        IntBuffer intBuffer = BufferUtils.createIntBuffer(1);
        AL10.alGetBufferi(buffer, al, intBuffer);
        intBuffer.rewind();
        return intBuffer.get();
    }
}

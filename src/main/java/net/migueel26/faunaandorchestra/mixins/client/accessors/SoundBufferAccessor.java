package net.migueel26.faunaandorchestra.mixins.client.accessors;

import com.mojang.blaze3d.audio.SoundBuffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.OptionalInt;

@Mixin(SoundBuffer.class)
public interface SoundBufferAccessor {
    @Invoker
    OptionalInt callGetAlBuffer();
}
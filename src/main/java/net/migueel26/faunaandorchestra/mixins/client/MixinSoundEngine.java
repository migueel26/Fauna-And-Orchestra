package net.migueel26.faunaandorchestra.mixins.client;

import net.migueel26.faunaandorchestra.mixins.interfaces.IChannelMixin;
import net.migueel26.faunaandorchestra.mixins.interfaces.ISoundEngineMixin;
import net.migueel26.faunaandorchestra.sound.custom.InstrumentSoundInstance;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.client.sounds.SoundBufferLibrary;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Mixin(SoundEngine.class)
public class MixinSoundEngine implements ISoundEngineMixin {
    @Shadow
    @Final
    private SoundBufferLibrary soundBuffers;

    @Shadow
    @Final
    private List<TickableSoundInstance> tickingSounds;

    @Inject(method = "play",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/sounds/SoundBufferLibrary;getCompleteBuffer(Lnet/minecraft/resources/ResourceLocation;)Ljava/util/concurrent/CompletableFuture;",
                    shift = At.Shift.BEFORE),
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true)
    private void handleMusicalOfffsetSounds(SoundInstance soundInstance, CallbackInfo ci, WeighedSoundEvents $$1x,
                                            ResourceLocation $$2x, Sound sound, float $$4x, float $$5x, SoundSource soundSource,
                                            float $$7x, float $$8x, SoundInstance.Attenuation attenuation, boolean $$10x,
                                            Vec3 $$11x, boolean $$14, boolean $$15, CompletableFuture $$16, ChannelAccess.ChannelHandle channelaccess$channelhandle) {
        if (soundInstance instanceof InstrumentSoundInstance instrumentSoundInstance) {
            this.playInstrument(instrumentSoundInstance, channelaccess$channelhandle);
            ci.cancel();
        }
    }

    @Unique
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void playInstrument(InstrumentSoundInstance soundInstance, ChannelAccess.ChannelHandle channelAccess) {
        Sound sound = soundInstance.getSound();
        // ATTACH OUR OWN STATIC BUFFER WITH THE BYTE OFFSET
        this.soundBuffers.getCompleteBuffer(sound.getPath()).thenAccept((soundBuffer -> {
            channelAccess.execute(channel -> {

                if (soundInstance.getTicksOffset() == null) {
                    Optional<Integer> sourceOffset = tickingSounds.stream().filter(InstrumentSoundInstance.class::isInstance)
                            .map(soundInst -> ((InstrumentSoundInstance) soundInst).getSourceID()).findAny();
                    System.out.println("And here!");

                    if (sourceOffset.isPresent()) {
                        ((IChannelMixin) channel).faunaSetByteOffset(soundInstance, soundBuffer, sourceOffset.get());
                    } else {
                        ((IChannelMixin) channel).faunaSetTickOffset(soundInstance, soundBuffer, 0);
                    }
                } else {
                        ((IChannelMixin) channel).faunaSetTickOffset(soundInstance, soundBuffer, soundInstance.getTicksOffset());
                }
                channel.play();
            });
        }));

        this.tickingSounds.add(soundInstance);
    }

    @Override
    public void faunaStopMusic(UUID entityUUID) {
        tickingSounds.stream().filter(InstrumentSoundInstance.class::isInstance)
                .map(InstrumentSoundInstance.class::cast)
                .filter(sound -> sound.getEntity().getUUID().equals(entityUUID))
                .forEach(InstrumentSoundInstance::stopSound);
    }
}

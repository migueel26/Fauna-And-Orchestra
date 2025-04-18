package net.migueel26.faunaandorchestra.mixins.client;

import net.migueel26.faunaandorchestra.mixins.interfaces.ISoundManagerMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.sounds.Music;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MusicManager.class)
public class MixinMusicManager {
    @Shadow @Final Minecraft minecraft;
    @Shadow SoundInstance currentMusic;
    @Shadow public void stopPlaying() {}

    @Inject(method = "tick", at = @At(value = "RETURN", ordinal = 1))
    private void onTick(CallbackInfo ci) {
        if (((ISoundManagerMixin) minecraft.getSoundManager()).faunaIsThereAnOrchestra()) {
            if (minecraft.getSoundManager().isActive(this.currentMusic)) {
                this.stopPlaying();
            }
        }
    }
}

package net.migueel26.faunaandorchestra.mixins.client;

import net.migueel26.faunaandorchestra.mixins.interfaces.ISoundEngineMixin;
import net.migueel26.faunaandorchestra.mixins.interfaces.ISoundManagerMixin;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.SoundManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.UUID;

@Mixin(SoundManager.class)
public class MixinSoundManager implements ISoundManagerMixin {
    @Shadow @Final private SoundEngine soundEngine;

    @Override
    public void faunaStopMusic(UUID entityID) {
        ((ISoundEngineMixin) this.soundEngine).faunaStopMusic(entityID);
    }

    @Override
    public void faunaStopFrogMusic(UUID entityID) {
        ((ISoundEngineMixin) this.soundEngine).faunaStopFrogMusic(entityID);
    }

    @Override
    public boolean faunaIsThereAnOrchestra() {
        return ((ISoundEngineMixin) this.soundEngine).faunaIsThereAnOrchestra();
    }
}

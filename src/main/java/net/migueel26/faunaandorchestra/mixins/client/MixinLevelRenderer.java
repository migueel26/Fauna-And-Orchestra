package net.migueel26.faunaandorchestra.mixins.client;

import net.migueel26.faunaandorchestra.mixins.interfaces.ILevelRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class MixinLevelRenderer implements ILevelRenderer {
    @Shadow @Final
    Minecraft minecraft;

    @Shadow
    ClientLevel level;

    @Shadow private void notifyNearbyEntities(Level level, BlockPos pos, boolean playing) {}

    @Inject(method = "levelEvent", at = @At(value = "TAIL"))
    public void onLevelEvent(int type, BlockPos pos, int data, CallbackInfo ci) {
        if (type == 4005) {
            playOrchestraSong(pos);
        }
    }
    @Override
    public void playOrchestraSong(BlockPos pos) {
        this.minecraft.gui.setNowPlaying(Component.literal("The Song of Resurrection"));
        this.notifyNearbyEntities(this.level, pos, true);
    }
}

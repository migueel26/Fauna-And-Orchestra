package net.migueel26.faunaandorchestra.mixins.client;

import net.migueel26.faunaandorchestra.item.ModItems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class MixinPlayer {

    @Inject(
            method = "startAutoSpinAttack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;removeEntitiesOnShoulder()V"
            ),
            cancellable = true
    )
    private void cancelAnimationForMantis(int ticks, CallbackInfo ci) {
        Player player = (Player) (Object) this;

        ItemStack itemStack = player.getMainHandItem();

        if (itemStack.is(ModItems.MANTIS_DAGGER.get())) {
            ci.cancel();
        }
    }
}
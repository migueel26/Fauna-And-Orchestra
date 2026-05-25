package net.migueel26.faunaandorchestra.event;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.effect.ModEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ComputeFovModifierEvent;

@EventBusSubscriber(modid = FaunaAndOrchestra.MOD_ID, value = Dist.CLIENT)
public class ModClientEffectEvents {

    @SubscribeEvent
    public static void onComputeFov(ComputeFovModifierEvent event) {
        LocalPlayer player = Minecraft.getInstance().player;

        if (player != null && player.hasEffect(ModEffects.OVERWHELMING_SLOWNESS)) {
            event.setNewFovModifier(1.0F);
        }
    }
}
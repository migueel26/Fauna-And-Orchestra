package net.migueel26.faunaandorchestra.event;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.effect.ModEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ComputeFovModifierEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FaunaAndOrchestra.MOD_ID, value = Dist.CLIENT)
public class ModClientEffectEvents {

    @SubscribeEvent
    public static void onComputeFov(ComputeFovModifierEvent event) {
        LocalPlayer player = Minecraft.getInstance().player;

        if (player != null && player.hasEffect(ModEffects.OVERWHELMING_SLOWNESS.get())) {
            event.setNewFovModifier(1.0F);
        }
    }
}
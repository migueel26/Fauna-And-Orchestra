package net.migueel26.faunaandorchestra.event;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.networking.*;
import net.minecraft.Util;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FaunaAndOrchestra.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModClientEvents {
    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> {
            if (tintIndex == 0) {
                long time = Util.getMillis();
                float speed = 6000.0f;
                float hue = (time % (long) speed) / speed;
                int rgbColor = Mth.hsvToRgb(hue, 1.0f, 1.0f);
                // alpha | rgb
                return 0xFF000000 | rgbColor;
            }
            return 0xFFFFFFFF;
        }, ModItems.EVERFRUIT.get()); // Reemplaza con tu ítem
    }
}

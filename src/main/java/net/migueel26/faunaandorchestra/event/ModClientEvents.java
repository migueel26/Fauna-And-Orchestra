package net.migueel26.faunaandorchestra.event;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.networking.*;
import net.migueel26.faunaandorchestra.screen.ClientRecipeItemsTooltip;
import net.minecraft.Util;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber(modid = FaunaAndOrchestra.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModClientEvents {
    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register(getRainbowEffect(), ModItems.EVERFRUIT.get());
        event.register(getRainbowEffect(), ModItems.EVERJELLY.get());
    }

        private static @NotNull ItemColor getRainbowEffect() {
            return (stack, tintIndex) -> {
            if (tintIndex == 0) {
                long time = Util.getMillis();
                float speed = 6000.0f;
                float hue = (time % (long) speed) / speed;
                int rgbColor = Mth.hsvToRgb(hue, 1.0f, 1.0f);
                // alpha | rgb
                return 0xFF000000 | rgbColor;
            }
            return 0xFFFFFFFF;
        };
    }

    @SubscribeEvent
    public static void registerTooltipComponents(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(ClientRecipeItemsTooltip.RecipeItemsTooltip.class, ClientRecipeItemsTooltip::new);
    }
}

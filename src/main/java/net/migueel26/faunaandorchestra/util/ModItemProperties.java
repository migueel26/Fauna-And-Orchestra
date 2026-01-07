package net.migueel26.faunaandorchestra.util;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;

public class ModItemProperties {
    public static void addCustomItemProperties() {
        ItemProperties.register(ModItems.BRIEFCASE.get(),
                new ResourceLocation(FaunaAndOrchestra.MOD_ID, "opened"),
                (stack, level, entity, seed) -> {
                    boolean isOpen = stack.hasTag() && stack.getTag().getBoolean("opened");
                    return isOpen ? 1 : 0;
                });
    }
}

package net.migueel26.faunaandorchestra.util;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.component.ModDataComponents;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;

public class ModItemProperties {
    public static void addCustomItemProperties() {
        ItemProperties.register(ModItems.BRIEFCASE.get(), ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "opened"),
                (stack, level, entity, seed) -> stack.getOrDefault(ModDataComponents.OPENED, false) ? 1f : 0f);
    }
}

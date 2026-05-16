package net.migueel26.faunaandorchestra.client.item.armor;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.item.custom.armor.FloralBootsItem;
import net.migueel26.faunaandorchestra.item.custom.armor.FluffyBootsItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class FloralBootsRenderer extends GeoArmorRenderer<FloralBootsItem> {
    public FloralBootsRenderer() {
        super(new DefaultedItemGeoModel<>(ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "armor/floral_boots")));
    }
}

package net.migueel26.faunaandorchestra.client.item;

import net.migueel26.faunaandorchestra.item.custom.GeoBlockItem;
import net.migueel26.faunaandorchestra.item.custom.clothing.CosmeticItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class BambooTrapItemRenderer extends GeoItemRenderer<GeoBlockItem> {
    public BambooTrapItemRenderer() {
        super(new BambooTrapItemModel());
    }
}

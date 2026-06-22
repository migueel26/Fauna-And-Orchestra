package net.migueel26.faunaandorchestra.client.item;

import net.migueel26.faunaandorchestra.item.custom.clothing.CosmeticItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class TopHatItemRenderer extends GeoItemRenderer<CosmeticItem> {
    public TopHatItemRenderer() {
        super(new TopHatItemModel());
    }
}

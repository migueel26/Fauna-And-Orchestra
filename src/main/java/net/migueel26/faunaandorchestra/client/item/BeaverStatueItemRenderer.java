package net.migueel26.faunaandorchestra.client.item;

import net.migueel26.faunaandorchestra.item.custom.GeoBlockItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class BeaverStatueItemRenderer extends GeoItemRenderer<GeoBlockItem> {
    public BeaverStatueItemRenderer() {
        super(new BeaverStatueItemModel());
    }
}

package net.migueel26.faunaandorchestra.client.item;

import net.migueel26.faunaandorchestra.item.custom.GeoBlockItem;
import net.migueel26.faunaandorchestra.item.custom.TheGreatHeadItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class MotherStatueItemRenderer extends GeoItemRenderer<GeoBlockItem> {
    public MotherStatueItemRenderer() {
        super(new MotherStatueItemModel());
    }
}

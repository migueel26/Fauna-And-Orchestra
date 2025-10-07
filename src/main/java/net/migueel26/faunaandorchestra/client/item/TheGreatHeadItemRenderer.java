package net.migueel26.faunaandorchestra.client.item;

import net.migueel26.faunaandorchestra.item.custom.TheGreatHeadItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class TheGreatHeadItemRenderer extends GeoItemRenderer<TheGreatHeadItem> {
    public TheGreatHeadItemRenderer() {
        super(new TheGreatHeadItemModel());
    }
}

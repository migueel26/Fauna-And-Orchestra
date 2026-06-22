package net.migueel26.faunaandorchestra.client.item;

import net.migueel26.faunaandorchestra.item.custom.clothing.CosmeticItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class BaseballCapItemRenderer extends GeoItemRenderer<CosmeticItem> {
    public BaseballCapItemRenderer() {
        super(new BaseballCapItemModel());
    }
}

package net.migueel26.faunaandorchestra.client.item;

import net.migueel26.faunaandorchestra.item.custom.GeoBlockItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class TermiteChestItemRenderer extends GeoItemRenderer<GeoBlockItem> {
    public TermiteChestItemRenderer() {
        super(new TermiteChestItemModel());
    }
}

package net.migueel26.faunaandorchestra.client.entity;

import net.migueel26.faunaandorchestra.client.item.BaseballCapItemModel;
import net.migueel26.faunaandorchestra.client.item.FloatingBlossomItemModel;
import net.migueel26.faunaandorchestra.item.custom.FloatingBlossomItem;
import net.migueel26.faunaandorchestra.item.custom.clothing.CosmeticItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class FloatingBlossomItemRenderer extends GeoItemRenderer<FloatingBlossomItem> {
    public FloatingBlossomItemRenderer() {
        super(new FloatingBlossomItemModel());
    }
}

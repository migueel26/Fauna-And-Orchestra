package net.migueel26.faunaandorchestra.client.item;

import net.migueel26.faunaandorchestra.item.custom.MantisDaggerItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class MantisDaggerItemRenderer extends GeoItemRenderer<MantisDaggerItem> {
    public MantisDaggerItemRenderer() {
        super(new MantisDaggerItemModel());
    }
}

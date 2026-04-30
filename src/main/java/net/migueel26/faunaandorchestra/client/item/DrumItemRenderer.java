package net.migueel26.faunaandorchestra.client.item;

import net.migueel26.faunaandorchestra.item.custom.GeoInstrumentItem;
import net.migueel26.faunaandorchestra.item.custom.clothing.CosmeticItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class DrumItemRenderer extends GeoItemRenderer<GeoInstrumentItem> {
    public DrumItemRenderer() {
        super(new DrumItemModel());
    }
}

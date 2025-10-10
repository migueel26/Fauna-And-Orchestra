package net.migueel26.faunaandorchestra.client.item;

import net.migueel26.faunaandorchestra.item.custom.MelomancyCauldronItem;
import net.migueel26.faunaandorchestra.item.custom.TheGreatHeadItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class MelomancyCauldronItemRenderer extends GeoItemRenderer<MelomancyCauldronItem> {
    public MelomancyCauldronItemRenderer() {
        super(new MelomancyCauldronItemModel());
    }
}

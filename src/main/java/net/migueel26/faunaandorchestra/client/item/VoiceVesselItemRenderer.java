package net.migueel26.faunaandorchestra.client.item;

import net.migueel26.faunaandorchestra.item.custom.VoiceVesselItem;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class VoiceVesselItemRenderer extends GeoItemRenderer<VoiceVesselItem> {
    public VoiceVesselItemRenderer() {
        super(new VoiceVesselItemModel());
    }
}

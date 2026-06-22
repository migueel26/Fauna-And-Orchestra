package net.migueel26.faunaandorchestra.client.item;

import net.migueel26.faunaandorchestra.item.custom.SoundSensorItem;
import net.migueel26.faunaandorchestra.item.custom.VoiceVesselItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class SoundSensorItemRenderer extends GeoItemRenderer<SoundSensorItem> {
    public SoundSensorItemRenderer() {
        super(new SoundSensorItemModel());
    }
}

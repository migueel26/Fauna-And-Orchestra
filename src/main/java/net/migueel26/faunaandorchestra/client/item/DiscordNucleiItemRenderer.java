package net.migueel26.faunaandorchestra.client.item;

import net.migueel26.faunaandorchestra.item.custom.DiscordNucleiItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class DiscordNucleiItemRenderer extends GeoItemRenderer<DiscordNucleiItem> {
    public DiscordNucleiItemRenderer() {
        super(new DiscordNucleiItemModel());
    }
}

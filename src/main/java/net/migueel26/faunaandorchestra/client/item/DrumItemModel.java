package net.migueel26.faunaandorchestra.client.item;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.item.custom.GeoInstrumentItem;
import net.migueel26.faunaandorchestra.item.custom.clothing.CosmeticItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class DrumItemModel extends GeoModel<GeoInstrumentItem> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/item/drum.png");
    private static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "animations/item/voice_vessel.animation.json");
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "geo/item/drum.geo.json");
    @Override
    public ResourceLocation getModelResource(GeoInstrumentItem animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(GeoInstrumentItem animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(GeoInstrumentItem animatable) {
        return null;
    }
}

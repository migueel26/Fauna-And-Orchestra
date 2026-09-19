package net.migueel26.faunaandorchestra.client.item;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.item.custom.GeoInstrumentItem;
import net.migueel26.faunaandorchestra.item.custom.HarmonicaItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class HarmonicaItemModel extends GeoModel<HarmonicaItem> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/item/harmonica.png");
    private static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "animations/item/voice_vessel.animation.json");
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "geo/item/harmonica.geo.json");
    @Override
    public ResourceLocation getModelResource(HarmonicaItem animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(HarmonicaItem animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(HarmonicaItem animatable) {
        return null;
    }
}

package net.migueel26.faunaandorchestra.client.item;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.item.custom.GeoBlockItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BeaverStatueItemModel extends GeoModel<GeoBlockItem> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/block/beaver_statue.png");
    private static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "animations/block/beaver_statue.animation.json");
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "geo/block/beaver_statue.geo.json");
    @Override
    public ResourceLocation getModelResource(GeoBlockItem animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(GeoBlockItem animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(GeoBlockItem animatable) {
        return ANIMATIONS;
    }
}
package net.migueel26.faunaandorchestra.client.item;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.item.custom.FloatingBlossomItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class FloatingBlossomItemModel extends GeoModel<FloatingBlossomItem> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/entity/floating_blossom.png");
    private static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "animations/entity/floating_blossom.animation.json");
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "geo/entity/floating_blossom.geo.json");
    @Override
    public ResourceLocation getModelResource(FloatingBlossomItem animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(FloatingBlossomItem animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(FloatingBlossomItem animatable) {
        return ANIMATIONS;
    }
}

package net.migueel26.faunaandorchestra.client.entity.misc;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.custom.ButterflyEntity;
import net.migueel26.faunaandorchestra.entity.custom.misc.FloatingBlossomEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class FloatingBlossomModel extends GeoModel<FloatingBlossomEntity> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/entity/floating_blossom.png");
    private static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "animations/entity/floating_blossom.animation.json");
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "geo/entity/floating_blossom.geo.json");

    @Override
    public ResourceLocation getModelResource(FloatingBlossomEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(FloatingBlossomEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(FloatingBlossomEntity animatable) {
        return ANIMATIONS;
    }
}

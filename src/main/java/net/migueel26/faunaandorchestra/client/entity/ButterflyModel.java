package net.migueel26.faunaandorchestra.client.entity;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.custom.ButterflyEntity;
import net.migueel26.faunaandorchestra.entity.custom.MacawEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class ButterflyModel extends GeoModel<ButterflyEntity> {
    private static final ResourceLocation BLUE_TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/entity/butterfly_blue.png");
    private static final ResourceLocation ORANGE_TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/entity/butterfly_orange.png");
    private static final ResourceLocation MAGENTA_TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/entity/butterfly_magenta.png");
    private static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "animations/entity/butterfly.animation.json");
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "geo/entity/butterfly.geo.json");
    @Override
    public ResourceLocation getModelResource(ButterflyEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(ButterflyEntity animatable) {
        return switch (animatable.getVariant()) {
            case BLUE -> BLUE_TEXTURE;
            case ORANGE -> ORANGE_TEXTURE;
            case MAGENTA -> MAGENTA_TEXTURE;
        };
    }

    @Override
    public ResourceLocation getAnimationResource(ButterflyEntity animatable) {
        return ANIMATIONS;
    }
}

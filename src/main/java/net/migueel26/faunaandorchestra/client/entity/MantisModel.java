package net.migueel26.faunaandorchestra.client.entity;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.custom.MantisEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MantisModel extends GeoModel<MantisEntity> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/entity/mantis.png");
    private static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "animations/entity/mantis.animation.json");
    private static final ResourceLocation PLAYING_MANTIS = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "geo/entity/playing_mantis.geo.json");

    @Override
    public ResourceLocation getModelResource(MantisEntity mantis) {
        //TODO: DEACTIVATE THE VIOLIN AND PUPILS WHEN SO
        return PLAYING_MANTIS;
    }

    @Override
    public ResourceLocation getTextureResource(MantisEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(MantisEntity animatable) {
        return ANIMATIONS;
    }
}

package net.migueel26.faunaandorchestra.client.entity;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.custom.ButterflyEntity;
import net.migueel26.faunaandorchestra.entity.custom.MacawEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class ButterflyModel extends GeoModel<ButterflyEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(FaunaAndOrchestra.MOD_ID, "textures/entity/butterfly.png");
    private static final ResourceLocation ANIMATIONS = new ResourceLocation(FaunaAndOrchestra.MOD_ID, "animations/entity/butterfly.animation.json");
    private static final ResourceLocation MODEL = new ResourceLocation(FaunaAndOrchestra.MOD_ID, "geo/entity/butterfly.geo.json");

    @Override
    public ResourceLocation getModelResource(ButterflyEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(ButterflyEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(ButterflyEntity animatable) {
        return ANIMATIONS;
    }
}

package net.migueel26.faunaandorchestra.client.entity;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.custom.jazzy_dammys.DanB;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class DanBModel extends GeoModel<DanB> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/entity/dan_b.png");
    private static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "animations/entity/dan_b.animation.json");
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "geo/entity/dan_b.geo.json");
    @Override
    public ResourceLocation getModelResource(DanB animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(DanB animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(DanB animatable) {
        return ANIMATIONS;
    }

}

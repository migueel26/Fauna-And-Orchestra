package net.migueel26.faunaandorchestra.client.entity;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.custom.jazzy_dammys.DanB;
import net.migueel26.faunaandorchestra.entity.custom.village_trotters.Cornelius;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class CorneliusModel extends GeoModel<Cornelius> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/entity/cornelius.png");
    private static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "animations/entity/cornelius.animation.json");
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "geo/entity/cornelius.geo.json");
    @Override
    public ResourceLocation getModelResource(Cornelius animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(Cornelius animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(Cornelius animatable) {
        return ANIMATIONS;
    }

}

package net.migueel26.faunaandorchestra.client.entity;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.custom.jazzy_dammys.Delroy;
import net.migueel26.faunaandorchestra.entity.custom.jazzy_dammys.Denzel;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class DenzelModel extends GeoModel<Denzel> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/entity/denzel.png");
    private static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "animations/entity/denzel.animation.json");
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "geo/entity/denzel.geo.json");
    @Override
    public ResourceLocation getModelResource(Denzel animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(Denzel animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(Denzel animatable) {
        return ANIMATIONS;
    }

}

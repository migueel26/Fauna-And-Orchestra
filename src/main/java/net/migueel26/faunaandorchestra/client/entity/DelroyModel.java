package net.migueel26.faunaandorchestra.client.entity;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.custom.jazzy_dammys.DanB;
import net.migueel26.faunaandorchestra.entity.custom.jazzy_dammys.Delroy;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class DelroyModel extends GeoModel<Delroy> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/entity/delroy.png");
    private static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "animations/entity/delroy.animation.json");
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "geo/entity/delroy.geo.json");
    @Override
    public ResourceLocation getModelResource(Delroy animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(Delroy animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(Delroy animatable) {
        return ANIMATIONS;
    }

}

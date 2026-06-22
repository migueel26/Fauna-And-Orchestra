package net.migueel26.faunaandorchestra.client.block;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.block.entity.FloraEnhancerBlockEntity;
import net.migueel26.faunaandorchestra.block.entity.MotherStatueBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class FloraEnhancerModel extends GeoModel<FloraEnhancerBlockEntity> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/block/flora_enhancer.png");
    private static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "animations/block/mother_statue.animation.json");
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "geo/block/flora_enhancer.geo.json");
    @Override
    public ResourceLocation getModelResource(FloraEnhancerBlockEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(FloraEnhancerBlockEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(FloraEnhancerBlockEntity animatable) {
        return ANIMATIONS;
    }
}

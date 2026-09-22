package net.migueel26.faunaandorchestra.client.item;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.item.custom.GeoBlockItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;

public class TermiteChestItemModel extends GeoModel<GeoBlockItem> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/block/termite_chest.png");
    private static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "animations/block/termite_chest.animation.json");
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "geo/block/termite_chest.geo.json");
    @Override
    public ResourceLocation getModelResource(GeoBlockItem animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(GeoBlockItem animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(GeoBlockItem animatable) {
        return ANIMATIONS;
    }

    @Override
    public void setCustomAnimations(GeoBlockItem animatable, long instanceId, AnimationState<GeoBlockItem> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        GeoBone termite = getAnimationProcessor().getBone("termite");
        if (termite != null) {
            termite.setHidden(false);
        }
    }
}
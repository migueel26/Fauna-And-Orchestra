package net.migueel26.faunaandorchestra.client.item;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.custom.TermiteBaby;
import net.migueel26.faunaandorchestra.item.custom.BasicGeoItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class TermiteItemModel extends GeoModel<BasicGeoItem> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/entity/termite.png");
    private static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "animations/entity/termite.animation.json");
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "geo/entity/termite.geo.json");
    @Override
    public ResourceLocation getModelResource(BasicGeoItem animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(BasicGeoItem animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(BasicGeoItem animatable) {
        return ANIMATIONS;
    }

    @Override
    public void setCustomAnimations(BasicGeoItem animatable, long instanceId, AnimationState<BasicGeoItem> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        GeoBone head = getAnimationProcessor().getBone("head");

        if (head != null) {
            head.setRotY(0f);
            head.setRotZ(0f);
            head.setRotX(0f);
        }
    }
}

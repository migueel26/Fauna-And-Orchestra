package net.migueel26.faunaandorchestra.client.entity;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.custom.MacawEntity;
import net.migueel26.faunaandorchestra.entity.custom.MadameButterflyEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class MadameButterflyModel extends GeoModel<MadameButterflyEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(FaunaAndOrchestra.MOD_ID, "textures/entity/madame_butterfly.png");
    private static final ResourceLocation ANIMATIONS = new ResourceLocation(FaunaAndOrchestra.MOD_ID, "animations/entity/madame_butterfly.animation.json");
    private static final ResourceLocation MODEL = new ResourceLocation(FaunaAndOrchestra.MOD_ID, "geo/entity/madame_butterfly.geo.json");

    @Override
    public ResourceLocation getModelResource(MadameButterflyEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(MadameButterflyEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(MadameButterflyEntity animatable) {
        return ANIMATIONS;
    }

    @Override
    public void setCustomAnimations(MadameButterflyEntity madame, long instanceId, AnimationState<MadameButterflyEntity> animationState) {
        CoreGeoBone cello = getAnimationProcessor().getBone("violin");
        CoreGeoBone bow = getAnimationProcessor().getBone("bow");

        cello.setHidden(!madame.isHoldingInstrument());
        bow.setHidden(!madame.isHoldingInstrument());

    }
}

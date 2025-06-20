package net.migueel26.faunaandorchestra.client.entity;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.custom.MantisEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class MantisModel extends GeoModel<MantisEntity> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/entity/mantis.png");
    private static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "animations/entity/mantis.animation.json");
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "geo/entity/mantis.geo.json");

    @Override
    public ResourceLocation getModelResource(MantisEntity mantis) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(MantisEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(MantisEntity animatable) {
        return ANIMATIONS;
    }

    @Override
    public void setCustomAnimations(MantisEntity mantis, long instanceId, AnimationState<MantisEntity> animationState) {
        GeoBone head = getAnimationProcessor().getBone("head");

        if (head != null && !mantis.isPlayingInstrument()) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
        }

        GeoBone left_pupil = getAnimationProcessor().getBone("left_pupil");
        GeoBone right_pupil = getAnimationProcessor().getBone("right_pupil");
        GeoBone violin = getAnimationProcessor().getBone("violin");
        GeoBone bow = getAnimationProcessor().getBone("bow");

        if (mantis.isAngry()) {
            left_pupil.setHidden(true);
            right_pupil.setHidden(true);
        } else {
            left_pupil.setHidden(false);
            right_pupil.setHidden(false);
        }

        if (mantis.isHoldingInstrument()) {
            violin.setHidden(false);
            bow.setHidden(false);
        } else {
            violin.setHidden(true);
            bow.setHidden(true);
        }
    }
}

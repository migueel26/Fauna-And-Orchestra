package net.migueel26.faunaandorchestra.client.entity;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.custom.MantisEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class MantisModel extends GeoModel<MantisEntity> {
    private static final ResourceLocation NORMAL_TEXTURE = new ResourceLocation(FaunaAndOrchestra.MOD_ID, "textures/entity/mantis.png");
    private static final ResourceLocation NORMAL_ANIMATIONS = new ResourceLocation(FaunaAndOrchestra.MOD_ID, "animations/entity/mantis.animation.json");
    private static final ResourceLocation NORMAL_MODEL = new ResourceLocation(FaunaAndOrchestra.MOD_ID, "geo/entity/mantis.geo.json");
    private static final ResourceLocation ORCHID_TEXTURE = new ResourceLocation(FaunaAndOrchestra.MOD_ID, "textures/entity/orchid_mantis.png");
    private static final ResourceLocation ORCHID_ANIMATIONS = new ResourceLocation(FaunaAndOrchestra.MOD_ID, "animations/entity/orchid_mantis.animation.json");
    private static final ResourceLocation ORCHID_MODEL = new ResourceLocation(FaunaAndOrchestra.MOD_ID, "geo/entity/orchid_mantis.geo.json");

    @Override
    public ResourceLocation getModelResource(MantisEntity mantis) {
        return switch (mantis.getVariant()) {
            case ORCHID -> ORCHID_MODEL;
            default -> NORMAL_MODEL;
        };
    }

    @Override
    public ResourceLocation getTextureResource(MantisEntity mantis) {
        return switch (mantis.getVariant()) {
            case ORCHID -> ORCHID_TEXTURE;
            default -> NORMAL_TEXTURE;
        };
    }

    @Override
    public ResourceLocation getAnimationResource(MantisEntity mantis) {
        return switch (mantis.getVariant()) {
            case ORCHID -> ORCHID_ANIMATIONS;
            default -> NORMAL_ANIMATIONS;
        };
    }

    @Override
    public void setCustomAnimations(MantisEntity mantis, long instanceId, AnimationState<MantisEntity> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("head");

        if (head != null && !mantis.isPlayingInstrument()) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
        }

        CoreGeoBone left_pupil = getAnimationProcessor().getBone("left_pupil");
        CoreGeoBone right_pupil = getAnimationProcessor().getBone("right_pupil");
        CoreGeoBone violin = getAnimationProcessor().getBone("violin");
        CoreGeoBone bow = getAnimationProcessor().getBone("bow");

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

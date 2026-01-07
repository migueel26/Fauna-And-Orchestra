package net.migueel26.faunaandorchestra.client.entity;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.custom.LemurEntity;
import net.migueel26.faunaandorchestra.entity.custom.PenguinEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class LemurModel extends GeoModel<LemurEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(FaunaAndOrchestra.MOD_ID, "textures/entity/lemur.png");
    private static final ResourceLocation ANIMATIONS = new ResourceLocation(FaunaAndOrchestra.MOD_ID, "animations/entity/lemur.animation.json");
    private static final ResourceLocation MODEL = new ResourceLocation(FaunaAndOrchestra.MOD_ID, "geo/entity/lemur.geo.json");

    @Override
    public ResourceLocation getModelResource(LemurEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(LemurEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(LemurEntity animatable) {
        return ANIMATIONS;
    }

    @Override
    public void setCustomAnimations(LemurEntity lemur, long instanceId, AnimationState<LemurEntity> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("head");

        if (head != null && !lemur.isPlayingInstrument()) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
        }

        CoreGeoBone oboe = getAnimationProcessor().getBone("oboe");
        CoreGeoBone secondary_oboe = getAnimationProcessor().getBone("secondary_oboe");

        oboe.setHidden(!lemur.isHoldingInstrument());
        secondary_oboe.setHidden(!animationState.isCurrentAnimation(LemurEntity.WALK_OBOE));
    }
}

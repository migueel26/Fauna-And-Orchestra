package net.migueel26.faunaandorchestra.client.entity;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.custom.RedPandaEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class RedPandaModel extends GeoModel<RedPandaEntity> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/entity/red_panda.png");
    private static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "animations/entity/red_panda.animation.json");
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "geo/entity/red_panda.geo.json");
    @Override
    public ResourceLocation getModelResource(RedPandaEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(RedPandaEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(RedPandaEntity animatable) {
        return ANIMATIONS;
    }

    @Override
    public void setCustomAnimations(RedPandaEntity redPanda, long instanceId, AnimationState<RedPandaEntity> animationState) {
        GeoBone head = getAnimationProcessor().getBone("head");


        if (head != null && !redPanda.isPlayingInstrument() && !animationState.isMoving() && redPanda.isCurrentlyNotChangingStances()) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);;

            if (redPanda.isHoldingInstrument() || redPanda.isStanding()) {
                head.setRotX((entityData.headPitch()-80) * Mth.DEG_TO_RAD);
                head.setRotZ(-entityData.netHeadYaw() * Mth.DEG_TO_RAD);
            } else {
                head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
                head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
            }
        }

        GeoBone keytar = getAnimationProcessor().getBone("keytar");

        keytar.setHidden(!redPanda.isHoldingInstrument());
    }
}

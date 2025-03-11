package net.migueel26.faunaandorchestra.client.entity;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.custom.PenguinEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class PenguinModel extends GeoModel<PenguinEntity> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/entity/penguin.png");
    private static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "animations/entity/penguin.animation.json");
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "geo/entity/penguin.geo.json");

    @Override
    public ResourceLocation getModelResource(PenguinEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(PenguinEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(PenguinEntity animatable) {
        return ANIMATIONS;
    }

    @Override
    public void setCustomAnimations(PenguinEntity penguin, long instanceId, AnimationState<PenguinEntity> animationState) {
        GeoBone head = getAnimationProcessor().getBone("head");

        if (head != null && !penguin.isPlayingInstrument() && !animationState.isMoving()) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
        }
    }
}

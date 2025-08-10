package net.migueel26.faunaandorchestra.client.entity;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.custom.BeaverEntity;
import net.migueel26.faunaandorchestra.entity.custom.MacawEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class BeaverModel extends GeoModel<BeaverEntity> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/entity/beaver.png");
    private static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "animations/entity/beaver.animation.json");
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "geo/entity/beaver.geo.json");
    @Override
    public ResourceLocation getModelResource(BeaverEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(BeaverEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(BeaverEntity animatable) {
        return ANIMATIONS;
    }


    @Override
    public void setCustomAnimations(BeaverEntity beaver, long instanceId, AnimationState<BeaverEntity> animationState) {
        GeoBone head = getAnimationProcessor().getBone("head");

        if (head != null && !beaver.isPlayingInstrument()) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

            if (beaver.isHoldingInstrument() || beaver.isBuilding()) {
                head.setRotX((entityData.headPitch()-90) * Mth.DEG_TO_RAD);
                head.setRotZ(-entityData.netHeadYaw() * Mth.DEG_TO_RAD);
            } else {
                head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
                head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
            }

        }

        GeoBone saxophone = getAnimationProcessor().getBone("saxophone");

        saxophone.setHidden(!beaver.isHoldingInstrument());
    }
}

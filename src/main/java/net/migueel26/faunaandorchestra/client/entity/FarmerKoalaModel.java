package net.migueel26.faunaandorchestra.client.entity;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.custom.koala_workers.FarmerKoalaEntity;
import net.migueel26.faunaandorchestra.entity.custom.koala_workers.MelomancerKoalaEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class FarmerKoalaModel extends GeoModel<FarmerKoalaEntity> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/entity/farmer_koala.png");
    private static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "animations/entity/farmer_koala.animation.json");
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "geo/entity/farmer_koala.geo.json");
    @Override
    public ResourceLocation getModelResource(FarmerKoalaEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(FarmerKoalaEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(FarmerKoalaEntity animatable) {
        return ANIMATIONS;
    }


    @Override
    public void setCustomAnimations(FarmerKoalaEntity koala, long instanceId, AnimationState<FarmerKoalaEntity> animationState) {
        GeoBone head = getAnimationProcessor().getBone("head");
        if (head != null && (!animationState.getController().isPlayingTriggeredAnimation() || koala.isInLunchBreak()) && (!koala.isKoalaSleeping() || !koala.hasWorkingStation())) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
        }

    }
}

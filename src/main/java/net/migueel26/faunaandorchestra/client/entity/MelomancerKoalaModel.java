package net.migueel26.faunaandorchestra.client.entity;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.custom.koala_workers.MelomancerKoalaEntity;
import net.migueel26.faunaandorchestra.entity.custom.koala_workers.TailorKoalaEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class MelomancerKoalaModel extends GeoModel<MelomancerKoalaEntity> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/entity/melomancer_koala.png");
    private static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "animations/entity/melomancer_koala.animation.json");
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "geo/entity/melomancer_koala.geo.json");
    @Override
    public ResourceLocation getModelResource(MelomancerKoalaEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(MelomancerKoalaEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(MelomancerKoalaEntity animatable) {
        return ANIMATIONS;
    }


    @Override
    public void setCustomAnimations(MelomancerKoalaEntity koala, long instanceId, AnimationState<MelomancerKoalaEntity> animationState) {
        GeoBone head = getAnimationProcessor().getBone("head");
        if (head != null && (!koala.isMixing() || koala.isInLunchBreak()) && !koala.isKoalaSleeping()) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
        }

    }
}

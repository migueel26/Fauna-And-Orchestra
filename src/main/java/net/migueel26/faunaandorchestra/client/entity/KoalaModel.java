package net.migueel26.faunaandorchestra.client.entity;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.custom.BeaverEntity;
import net.migueel26.faunaandorchestra.entity.custom.KoalaEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class KoalaModel extends GeoModel<KoalaEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(FaunaAndOrchestra.MOD_ID, "textures/entity/koala.png");
    private static final ResourceLocation ANIMATIONS = new ResourceLocation(FaunaAndOrchestra.MOD_ID, "animations/entity/koala.animation.json");
    private static final ResourceLocation MODEL = new ResourceLocation(FaunaAndOrchestra.MOD_ID, "geo/entity/koala.geo.json");
    @Override
    public ResourceLocation getModelResource(KoalaEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(KoalaEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(KoalaEntity animatable) {
        return ANIMATIONS;
    }


    @Override
    public void setCustomAnimations(KoalaEntity koala, long instanceId, AnimationState<KoalaEntity> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("head");
        if (head != null && !koala.isKoalaSleeping()) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
        }

    }
}

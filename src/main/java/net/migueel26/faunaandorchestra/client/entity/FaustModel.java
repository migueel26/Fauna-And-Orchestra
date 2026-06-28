package net.migueel26.faunaandorchestra.client.entity;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.custom.Faust;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class FaustModel extends GeoModel<Faust> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(FaunaAndOrchestra.MOD_ID, "textures/entity/faust.png");
    private static final ResourceLocation ANIMATIONS = new ResourceLocation(FaunaAndOrchestra.MOD_ID, "animations/entity/faust.animation.json");
    private static final ResourceLocation MODEL = new ResourceLocation(FaunaAndOrchestra.MOD_ID, "geo/entity/faust.geo.json");
    @Override
    public ResourceLocation getModelResource(Faust animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(Faust animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(Faust animatable) {
        return ANIMATIONS;
    }


    @Override
    public void setCustomAnimations(Faust faust, long instanceId, AnimationState<Faust> animationState) {
        /*GeoBone head = getAnimationProcessor().getBone("head");
        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
        }*/

    }
}

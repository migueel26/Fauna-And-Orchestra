package net.migueel26.faunaandorchestra.client.entity;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.custom.Faust;
import net.migueel26.faunaandorchestra.entity.custom.Orion;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class OrionModel extends GeoModel<Orion> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/entity/orion.png");
    private static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "animations/entity/orion.animation.json");
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "geo/entity/orion.geo.json");
    @Override
    public ResourceLocation getModelResource(Orion animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(Orion animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(Orion animatable) {
        return ANIMATIONS;
    }


    @Override
    public void setCustomAnimations(Orion orion, long instanceId, AnimationState<Orion> animationState) {
        /*GeoBone head = getAnimationProcessor().getBone("head");
        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
        }*/

    }
}

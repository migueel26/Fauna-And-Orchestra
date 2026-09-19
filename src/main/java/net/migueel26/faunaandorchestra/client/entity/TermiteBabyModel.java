package net.migueel26.faunaandorchestra.client.entity;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.custom.TermiteBaby;
import net.migueel26.faunaandorchestra.entity.custom.TermiteQueen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class TermiteBabyModel extends GeoModel<TermiteBaby> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/entity/termite.png");
    private static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "animations/entity/termite.animation.json");
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "geo/entity/termite.geo.json");
    @Override
    public ResourceLocation getModelResource(TermiteBaby animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(TermiteBaby animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(TermiteBaby animatable) {
        return ANIMATIONS;
    }


    @Override
    public void setCustomAnimations(TermiteBaby termiteBaby, long instanceId, AnimationState<TermiteBaby> animationState) {
        GeoBone head = getAnimationProcessor().getBone("head");
        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
        }
    }
}

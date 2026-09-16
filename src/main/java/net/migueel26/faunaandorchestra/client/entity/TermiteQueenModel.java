package net.migueel26.faunaandorchestra.client.entity;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.custom.Faust;
import net.migueel26.faunaandorchestra.entity.custom.TermiteQueen;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class TermiteQueenModel extends GeoModel<TermiteQueen> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/entity/termite_queen.png");
    private static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "animations/entity/termite_queen.animation.json");
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "geo/entity/termite_queen.geo.json");
    @Override
    public ResourceLocation getModelResource(TermiteQueen animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(TermiteQueen animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(TermiteQueen animatable) {
        return ANIMATIONS;
    }


    @Override
    public void setCustomAnimations(TermiteQueen termiteQueen, long instanceId, AnimationState<TermiteQueen> animationState) {
        /*GeoBone head = getAnimationProcessor().getBone("head");
        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
        }*/

    }
}

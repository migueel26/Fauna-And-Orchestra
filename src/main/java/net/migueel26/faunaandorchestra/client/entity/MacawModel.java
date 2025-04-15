package net.migueel26.faunaandorchestra.client.entity;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.custom.MacawEntity;
import net.migueel26.faunaandorchestra.entity.custom.MantisEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class MacawModel extends GeoModel<MacawEntity> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/entity/macaw.png");
    private static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "animations/entity/macaw.animation.json");
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "geo/entity/macaw.geo.json");

    @Override
    public ResourceLocation getModelResource(MacawEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(MacawEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(MacawEntity animatable) {
        return ANIMATIONS;
    }

    @Override
    public void setCustomAnimations(MacawEntity macaw, long instanceId, AnimationState<MacawEntity> animationState) {
        GeoBone head = getAnimationProcessor().getBone("head");

        if (head != null && !macaw.isPlayingInstrument() && !animationState.isMoving()) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);

        }

        GeoBone double_bass = getAnimationProcessor().getBone("double_bass");

        double_bass.setHidden(!macaw.isHoldingInstrument());
    }
}

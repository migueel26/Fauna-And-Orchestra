package net.migueel26.faunaandorchestra.client.block;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.block.entity.SingingCropBlockEntity;
import net.migueel26.faunaandorchestra.block.entity.TheGreatHeadBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class SingingCropModel extends GeoModel<SingingCropBlockEntity> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/block/singing_crop.png");
    private static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "animations/block/singing_crop.animation.json");
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "geo/block/singing_crop.geo.json");
    @Override
    public ResourceLocation getModelResource(SingingCropBlockEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(SingingCropBlockEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(SingingCropBlockEntity animatable) {
        return ANIMATIONS;
    }

    @Override
    public void setCustomAnimations(SingingCropBlockEntity animatable, long instanceId, AnimationState<SingingCropBlockEntity> animationState) {
        boolean isEmerging = animationState.isCurrentAnimation(SingingCropBlockEntity.EMERGE);
        if (isEmerging) {
            getAnimationProcessor().getBone("left_pupil").setHidden(false);
            getAnimationProcessor().getBone("right_pupil").setHidden(false);
        } else {
            getAnimationProcessor().getBone("left_pupil").setHidden(true);
            getAnimationProcessor().getBone("right_pupil").setHidden(true);
        }
        super.setCustomAnimations(animatable, instanceId, animationState);
    }
}

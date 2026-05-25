package net.migueel26.faunaandorchestra.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.migueel26.faunaandorchestra.entity.custom.BeaverEntity;
import net.migueel26.faunaandorchestra.entity.custom.MacawEntity;
import net.migueel26.faunaandorchestra.entity.custom.PenguinEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Pose;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BeaverRenderer extends GeoEntityRenderer<BeaverEntity> {
    public BeaverRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BeaverModel());
    }

    @Override
    protected float getShadowRadius(BeaverEntity entity) {
        return entity.getDimensions(Pose.STANDING).width() * 0.65F;
    }
    @Override
    public void renderRecursively(PoseStack poseStack, BeaverEntity beaver, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        if (bone.getName().equals("saxophone")) {
            if (!beaver.isHoldingInstrument()) {
                bone.setHidden(true);
            }
        }

        if (bone.getName().equals("head")) {
            if (beaver.isBaby()) {
                bone.setScaleX(1.35F);
                bone.setScaleY(1.35F);
                bone.setScaleZ(1.35F);
            } else {
                bone.setScaleX(1.0F);
                bone.setScaleY(1.0F);
                bone.setScaleZ(1.0F);
            }
        }

        super.renderRecursively(poseStack, beaver, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }

    @Override
    public void scaleModelForRender(float widthScale, float heightScale, PoseStack poseStack, BeaverEntity animatable, BakedGeoModel model, boolean isReRender, float partialTick, int packedLight, int packedOverlay) {
        super.scaleModelForRender(animatable.getAgeScale(), animatable.getAgeScale(), poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);
    }
}

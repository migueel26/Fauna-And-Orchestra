package net.migueel26.faunaandorchestra.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.migueel26.faunaandorchestra.entity.custom.TermiteQueen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Pose;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class TermiteQueenRenderer extends GeoEntityRenderer<TermiteQueen> {
    public TermiteQueenRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new TermiteQueenModel());
    }

    @Override
    public void scaleModelForRender(float widthScale, float heightScale, PoseStack poseStack, TermiteQueen animatable, BakedGeoModel model, boolean isReRender, float partialTick, int packedLight, int packedOverlay) {
        super.scaleModelForRender(widthScale * 1.25f, heightScale * 1.25f, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);
    }

    @Override
    public void preRender(PoseStack poseStack, TermiteQueen animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.shadowRadius = animatable.getDimensions(Pose.STANDING).width * 0.65F;
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
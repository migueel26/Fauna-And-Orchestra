package net.migueel26.faunaandorchestra.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.migueel26.faunaandorchestra.entity.custom.TermiteBaby;
import net.migueel26.faunaandorchestra.entity.custom.TermiteQueen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Pose;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class TermiteBabyRenderer extends GeoEntityRenderer<TermiteBaby> {
    public TermiteBabyRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new TermiteBabyModel());
    }

    @Override
    public void renderRecursively(PoseStack poseStack, TermiteBaby animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        if (bone.getName().equals("glass_bottle") || bone.getName().equals("liquid_music")) {
            bone.setHidden(true);
        }
        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }

    @Override
    public void scaleModelForRender(float widthScale, float heightScale, PoseStack poseStack, TermiteBaby animatable, BakedGeoModel model, boolean isReRender, float partialTick, int packedLight, int packedOverlay) {
        super.scaleModelForRender(widthScale * 0.75f, heightScale * 0.75f, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);
    }

    @Override
    protected float getShadowRadius(TermiteBaby entity) {
        return entity.getDimensions(Pose.STANDING).width() * 0.65F;
    }
}

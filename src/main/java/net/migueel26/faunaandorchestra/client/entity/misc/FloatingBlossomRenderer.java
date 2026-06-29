package net.migueel26.faunaandorchestra.client.entity.misc;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.migueel26.faunaandorchestra.client.entity.ButterflyModel;
import net.migueel26.faunaandorchestra.entity.custom.ButterflyEntity;
import net.migueel26.faunaandorchestra.entity.custom.misc.FloatingBlossomEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Pose;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FloatingBlossomRenderer extends GeoEntityRenderer<FloatingBlossomEntity> {
    public FloatingBlossomRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new FloatingBlossomModel());
    }

    @Override
    public void preRender(PoseStack poseStack, FloatingBlossomEntity animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.shadowRadius = animatable.getDimensions(Pose.STANDING).width * 0.35F;
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public void renderRecursively(PoseStack poseStack, FloatingBlossomEntity animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (bone.getName().equals("floating_blossom")) {
            bone.setHidden(animatable.getLifetime() == animatable.MAX_LIFETIME);
        }
        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}

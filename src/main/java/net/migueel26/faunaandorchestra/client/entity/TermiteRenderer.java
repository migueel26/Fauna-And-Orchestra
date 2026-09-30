package net.migueel26.faunaandorchestra.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.migueel26.faunaandorchestra.entity.custom.TermiteBaby;
import net.migueel26.faunaandorchestra.entity.custom.TermiteEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Pose;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class TermiteRenderer extends GeoEntityRenderer<TermiteEntity> {
    public TermiteRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new TermiteModel());
    }

    @Override
    public void preRender(PoseStack poseStack, TermiteEntity animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.shadowRadius = animatable.getDimensions(Pose.STANDING).width * 0.65F;
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public void actuallyRender(PoseStack poseStack, TermiteEntity animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        boolean hasLiquid = animatable.hasLiquid();

        model.getBone("liquid_music").get().setHidden(!hasLiquid);
        model.getBone("glass_bottle").get().setHidden(hasLiquid);

        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}

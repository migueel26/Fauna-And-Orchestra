package net.migueel26.faunaandorchestra.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.migueel26.faunaandorchestra.entity.custom.koala_workers.MelomancerKoalaEntity;
import net.migueel26.faunaandorchestra.entity.custom.koala_workers.TailorKoalaEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Pose;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MelomancerKoalaRenderer extends GeoEntityRenderer<MelomancerKoalaEntity> {
    public MelomancerKoalaRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new MelomancerKoalaModel());
    }

    @Override
    protected float getShadowRadius(MelomancerKoalaEntity entity) {
        return entity.getDimensions(Pose.STANDING).width() * 0.65F;
    }

    @Override
    public void renderRecursively(PoseStack poseStack, MelomancerKoalaEntity koala, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        if (bone.getName().equals("gingko_biloba")) {
            bone.setHidden(!koala.isInLunchBreak());
        }
        super.renderRecursively(poseStack, koala, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }
}

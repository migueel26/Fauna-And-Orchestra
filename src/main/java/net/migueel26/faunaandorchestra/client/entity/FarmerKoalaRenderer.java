package net.migueel26.faunaandorchestra.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.migueel26.faunaandorchestra.entity.custom.koala_workers.FarmerKoalaEntity;
import net.migueel26.faunaandorchestra.entity.custom.koala_workers.MelomancerKoalaEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Pose;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FarmerKoalaRenderer extends GeoEntityRenderer<FarmerKoalaEntity> {
    public FarmerKoalaRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new FarmerKoalaModel());
    }

    @Override
    protected float getShadowRadius(FarmerKoalaEntity entity) {
        return entity.getDimensions(Pose.STANDING).width() * 0.65F;
    }

    @Override
    public void renderRecursively(PoseStack poseStack, FarmerKoalaEntity koala, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        if (bone.getName().equals("gingko_biloba")) {
            bone.setHidden(!koala.isInLunchBreak());
        }
        if (bone.getName().equals("iron_hoe")) {
            bone.setHidden(koala.isInLunchBreak());
        }
        super.renderRecursively(poseStack, koala, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }
}

package net.migueel26.faunaandorchestra.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.migueel26.faunaandorchestra.entity.custom.ConductorEntity;
import net.migueel26.faunaandorchestra.entity.custom.MantisEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ConductorRenderer extends GeoEntityRenderer<ConductorEntity> {
    public ConductorRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ConductorModel());
    }

    @Override
    public void renderRecursively(PoseStack poseStack, ConductorEntity mantis, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        if (bone.getName().equals("bow") || bone.getName().equals("violin")) {
            if(!mantis.isConducting()) {
                bone.setHidden(true);
            }
        }

        super.renderRecursively(poseStack, mantis, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }
}

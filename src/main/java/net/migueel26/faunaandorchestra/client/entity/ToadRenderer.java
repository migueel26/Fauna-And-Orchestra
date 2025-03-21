package net.migueel26.faunaandorchestra.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.migueel26.faunaandorchestra.entity.custom.ToadEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ToadRenderer extends GeoEntityRenderer<ToadEntity> {
    public ToadRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ToadModel());
    }

    @Override
    public void renderRecursively(PoseStack poseStack, ToadEntity toad, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        if (bone.getName().equals("bow") || bone.getName().equals("violin")) {
            if(!toad.isHoldingBaton()) {
                bone.setHidden(true);
            }
        }

        super.renderRecursively(poseStack, toad, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }
}

package net.migueel26.faunaandorchestra.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.migueel26.faunaandorchestra.entity.custom.MacawEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Pose;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MacawRenderer extends GeoEntityRenderer<MacawEntity> {
    public MacawRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new MacawModel());
    }

    @Override
    protected float getShadowRadius(MacawEntity entity) {
        return entity.getDimensions(Pose.STANDING).width() * 0.65F;
    }

    @Override
    public void renderRecursively(PoseStack poseStack, MacawEntity macaw, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        if (bone.getName().equals("double_bass")) {
            if (!macaw.isHoldingInstrument()) {
                bone.setHidden(true);
            }
        }

        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }
}

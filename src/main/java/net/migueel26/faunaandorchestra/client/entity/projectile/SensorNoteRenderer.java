package net.migueel26.faunaandorchestra.client.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.custom.WanderingNoteEntity;
import net.migueel26.faunaandorchestra.entity.custom.projectile.SensorNote;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class SensorNoteRenderer extends EntityRenderer<SensorNote> {
    public SensorNoteRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    public void render(SensorNote entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        int alpha;
        poseStack.pushPose();
        poseStack.scale(1.25F, 1.25F, 1.25F);
        poseStack.translate(-0.2, -0.5, 0);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());

        alpha = 255;

        PoseStack.Pose posestack$pose = poseStack.last();
        VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.entityTranslucent(getTextureLocation(entity)));
        vertex(vertexconsumer, posestack$pose, packedLight, 0.0F, 0, 0, 1, alpha);
        vertex(vertexconsumer, posestack$pose, packedLight, 1.0F, 0, 1, 1, alpha);
        vertex(vertexconsumer, posestack$pose, packedLight, 1.0F, 1, 1, 0, alpha);
        vertex(vertexconsumer, posestack$pose, packedLight, 0.0F, 1, 0, 0, alpha);
        poseStack.popPose();

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, int packedLight, float x, int y, int u, int v, int alpha) {
        consumer.addVertex(pose, x - 0.5F, (float)y - 0.25F, 0.0F)
                .setColor(255, 255, 255, alpha)
                .setUv((float)u, (float)v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight)
                .setNormal(pose, 0.0F, 1.0F, 0.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(SensorNote entity) {
        int index = entity.getTextureIndex();
        return ResourceLocation.fromNamespaceAndPath(
                FaunaAndOrchestra.MOD_ID, "textures/particle/fauna_note_" + index + ".png");
    }

}

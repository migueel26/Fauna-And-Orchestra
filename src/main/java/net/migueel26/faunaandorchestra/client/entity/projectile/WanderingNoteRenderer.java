package net.migueel26.faunaandorchestra.client.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.custom.WanderingNoteEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.projectile.DragonFireball;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class WanderingNoteRenderer extends EntityRenderer<WanderingNoteEntity> {
    public WanderingNoteRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    public void render(WanderingNoteEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        int alpha;
        poseStack.pushPose();
        poseStack.scale(2.0F, 2.0F, 2.0F);
        poseStack.translate(-0.2, -0.2, 0);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());

        alpha = Math.round(Mth.clamp(getValue(entity), 0, 255));

        PoseStack.Pose posestack$pose = poseStack.last();
        VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.entityTranslucent(getTextureLocation(entity)));
        vertex(vertexconsumer, posestack$pose, packedLight, 0.0F, 0, 0, 1, alpha);
        vertex(vertexconsumer, posestack$pose, packedLight, 1.0F, 0, 1, 1, alpha);
        vertex(vertexconsumer, posestack$pose, packedLight, 1.0F, 1, 1, 0, alpha);
        vertex(vertexconsumer, posestack$pose, packedLight, 0.0F, 1, 0, 0, alpha);
        poseStack.popPose();

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    private static float getValue(WanderingNoteEntity entity) {
        return (float) (255 * Math.pow((1 - (float) entity.getLifetime() /255), (float) 1/3));
    }

    private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, int packedLight, float x, int y, int u, int v, int alpha) {
        Matrix4f matrix4f = pose.pose();
        Matrix3f matrix3f = pose.normal();

        consumer.vertex(matrix4f, x - 0.5F, (float)y - 0.25F, 0.0F)
                .color(255, 255, 255, alpha)
                .uv((float)u, (float)v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(matrix3f, 0.0F, 1.0F, 0.0F)
                .endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(WanderingNoteEntity entity) {
        int index = entity.getTextureIndex();
        return new ResourceLocation(
                FaunaAndOrchestra.MOD_ID, "textures/particle/fauna_note_" + index + ".png");
    }
}

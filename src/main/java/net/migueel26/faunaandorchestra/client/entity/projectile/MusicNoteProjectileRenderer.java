package net.migueel26.faunaandorchestra.client.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.custom.projectile.MusicNoteProjectileEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class MusicNoteProjectileRenderer extends EntityRenderer<MusicNoteProjectileEntity> {
    private int tick = 0;
    private static final ResourceLocation MAIN_TEXTURE_LOCATION = new ResourceLocation(FaunaAndOrchestra.MOD_ID, "textures/entity/music_note_projectile.png");
    private static final ResourceLocation BACK_TEXTURE_LOCATION = new ResourceLocation(FaunaAndOrchestra.MOD_ID, "textures/entity/music_note_projectile_back.png");
    private static final RenderType MAIN_RENDER_TYPE = RenderType.entityCutoutNoCull(MAIN_TEXTURE_LOCATION);
    private static final RenderType BACK_RENDER_TYPE = RenderType.entityTranslucent(BACK_TEXTURE_LOCATION);

    public MusicNoteProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    protected int getBlockLightLevel(MusicNoteProjectileEntity entity, BlockPos pos) {
        return 15;
    }

    @Override
    public void render(MusicNoteProjectileEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());

        float ageInTicks = entity.tickCount + partialTicks;
        float scalePulse = (float) (0.25 * Math.sin(ageInTicks * 0.075) + 1.75f);

        poseStack.pushPose();
        poseStack.scale(1.5F, 1.5F, 1.5F);

        PoseStack.Pose mainPose = poseStack.last();
        VertexConsumer mainConsumer = buffer.getBuffer(MAIN_RENDER_TYPE);

        vertex(mainConsumer, mainPose, packedLight, 0.0F, 0, 1, 1, 255);
        vertex(mainConsumer, mainPose, packedLight, 1.0F, 0, 0, 1, 255);
        vertex(mainConsumer, mainPose, packedLight, 1.0F, 1, 0, 0, 255);
        vertex(mainConsumer, mainPose, packedLight, 0.0F, 1, 1, 0, 255);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.scale(scalePulse, scalePulse, scalePulse);

        poseStack.translate(0.0D, -0.05D, -0.03D);

        PoseStack.Pose backPose = poseStack.last();
        VertexConsumer backConsumer = buffer.getBuffer(BACK_RENDER_TYPE);

        vertex(backConsumer, backPose, packedLight, 0.0F, 0, 1, 1, 128);
        vertex(backConsumer, backPose, packedLight, 1.0F, 0, 0, 1, 128);
        vertex(backConsumer, backPose, packedLight, 1.0F, 1, 0, 0, 128);
        vertex(backConsumer, backPose, packedLight, 0.0F, 1, 1, 0, 128);
        poseStack.popPose();

        poseStack.popPose();

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
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
    public ResourceLocation getTextureLocation(MusicNoteProjectileEntity entity) {
        return MAIN_TEXTURE_LOCATION;
    }
}

package net.migueel26.faunaandorchestra.client.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.custom.WanderingNoteEntity;
import net.migueel26.faunaandorchestra.entity.custom.projectile.PhantomNoteProjectileEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.DragonFireball;

public class PhantomNoteProjectileRenderer extends EntityRenderer<PhantomNoteProjectileEntity> {
    private static final ResourceLocation DEFAULT_TEXTURE_LOCATION = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/entity/phantom_note.png");

    public PhantomNoteProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    protected int getBlockLightLevel(DragonFireball entity, BlockPos pos) {
        return 15;
    }

    public void render(PhantomNoteProjectileEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.scale(1.0F, 1.0F, 1.0F);

        if (!entity.isBad()) poseStack.translate(0, 0.25f, 0);

        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        PoseStack.Pose posestack$pose = poseStack.last();
        VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(entity)));
        vertex(vertexconsumer, posestack$pose, packedLight, 0.0F, 0, 0, 1);
        vertex(vertexconsumer, posestack$pose, packedLight, 1.0F, 0, 1, 1);
        vertex(vertexconsumer, posestack$pose, packedLight, 1.0F, 1, 1, 0);
        vertex(vertexconsumer, posestack$pose, packedLight, 0.0F, 1, 0, 0);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, int packedLight, float x, int y, int u, int v) {
        consumer.addVertex(pose, x - 0.5F, (float)y - 0.25F, 0.0F)
                .setColor(-1)
                .setUv((float)u, (float)v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight)
                .setNormal(pose, 0.0F, 1.0F, 0.0F);
    }

    /**
     * Returns the location of an entity's texture.
     */
    public ResourceLocation getTextureLocation(PhantomNoteProjectileEntity entity) {
        return entity.isBad() ? DEFAULT_TEXTURE_LOCATION : ResourceLocation.fromNamespaceAndPath(
                FaunaAndOrchestra.MOD_ID, "textures/particle/fauna_note_" + entity.getTextureIndex() + ".png");
    }
}

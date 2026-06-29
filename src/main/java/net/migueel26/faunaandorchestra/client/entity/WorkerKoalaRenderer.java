package net.migueel26.faunaandorchestra.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.migueel26.faunaandorchestra.entity.custom.WanderingKoalaEntity;
import net.migueel26.faunaandorchestra.entity.custom.koala_workers.WorkerKoalaEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Pose;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class WorkerKoalaRenderer extends GeoEntityRenderer<WorkerKoalaEntity> {
    public WorkerKoalaRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new WorkerKoalaModel());
    }

    @Override
    public void preRender(PoseStack poseStack, WorkerKoalaEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.shadowRadius = entity.getDimensions(Pose.STANDING).width * 0.65F;
        super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}

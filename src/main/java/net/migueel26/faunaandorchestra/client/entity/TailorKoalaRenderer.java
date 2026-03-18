package net.migueel26.faunaandorchestra.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.migueel26.faunaandorchestra.entity.custom.koala_workers.TailorKoalaEntity;
import net.migueel26.faunaandorchestra.entity.custom.koala_workers.WorkerKoalaEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Pose;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class TailorKoalaRenderer extends GeoEntityRenderer<TailorKoalaEntity> {
    public TailorKoalaRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new TailorKoalaModel());
    }

    @Override
    protected float getShadowRadius(TailorKoalaEntity entity) {
        return entity.getDimensions(Pose.STANDING).width() * 0.65F;
    }

    @Override
    public void renderRecursively(PoseStack poseStack, TailorKoalaEntity koala, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        if (bone.getName().equals("gingko_biloba")) {
            bone.setHidden(!koala.isInLunchBreak());
        }
        super.renderRecursively(poseStack, koala, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }
}

package net.migueel26.faunaandorchestra.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.migueel26.faunaandorchestra.entity.custom.KoalaEntity;
import net.migueel26.faunaandorchestra.entity.custom.QuirkyFrogEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Pose;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class KoalaRenderer extends GeoEntityRenderer<KoalaEntity> {
    public KoalaRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new KoalaModel());
    }

    @Override
    protected float getShadowRadius(KoalaEntity entity) {
        return entity.getDimensions(Pose.STANDING).width() * 0.65F;
    }
}

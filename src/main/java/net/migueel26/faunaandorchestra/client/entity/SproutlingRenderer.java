package net.migueel26.faunaandorchestra.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.migueel26.faunaandorchestra.entity.custom.BeaverEntity;
import net.migueel26.faunaandorchestra.entity.custom.SproutlingEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Pose;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SproutlingRenderer extends GeoEntityRenderer<SproutlingEntity> {
    public SproutlingRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SproutlingModel());
    }

    @Override
    protected float getShadowRadius(SproutlingEntity entity) {
        return entity.getDimensions(Pose.STANDING).width() * 0.65F;
    }
}

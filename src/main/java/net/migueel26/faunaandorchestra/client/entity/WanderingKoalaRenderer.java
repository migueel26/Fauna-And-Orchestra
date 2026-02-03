package net.migueel26.faunaandorchestra.client.entity;

import net.migueel26.faunaandorchestra.entity.custom.WanderingKoalaEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Pose;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class WanderingKoalaRenderer extends GeoEntityRenderer<WanderingKoalaEntity> {
    public WanderingKoalaRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new WanderingKoalaModel());
    }

    @Override
    protected float getShadowRadius(WanderingKoalaEntity entity) {
        return entity.getDimensions(Pose.STANDING).width() * 0.65F;
    }
}

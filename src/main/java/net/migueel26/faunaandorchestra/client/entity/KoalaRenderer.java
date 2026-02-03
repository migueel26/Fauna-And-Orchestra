package net.migueel26.faunaandorchestra.client.entity;

import net.migueel26.faunaandorchestra.entity.custom.WanderingKoalaEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Pose;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class KoalaRenderer extends GeoEntityRenderer<WanderingKoalaEntity> {
    public KoalaRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new KoalaModel());
    }

    @Override
    protected float getShadowRadius(WanderingKoalaEntity entity) {
        return entity.getDimensions(Pose.STANDING).width() * 0.65F;
    }
}

package net.migueel26.faunaandorchestra.client.entity;

import net.migueel26.faunaandorchestra.entity.custom.ButlerKoalaEntity;
import net.migueel26.faunaandorchestra.entity.custom.WanderingKoalaEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Pose;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ButlerKoalaRenderer extends GeoEntityRenderer<ButlerKoalaEntity> {
    public ButlerKoalaRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ButlerKoalaModel());
    }

    @Override
    protected float getShadowRadius(ButlerKoalaEntity entity) {
        return entity.getDimensions(Pose.STANDING).width() * 0.65F;
    }
}

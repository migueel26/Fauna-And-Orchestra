package net.migueel26.faunaandorchestra.client.entity;

import net.migueel26.faunaandorchestra.entity.custom.koala_workers.TailorKoalaEntity;
import net.migueel26.faunaandorchestra.entity.custom.koala_workers.WorkerKoalaEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Pose;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class TailorKoalaRenderer extends GeoEntityRenderer<TailorKoalaEntity> {
    public TailorKoalaRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new TailorKoalaModel());
    }

    @Override
    protected float getShadowRadius(TailorKoalaEntity entity) {
        return entity.getDimensions(Pose.STANDING).width() * 0.65F;
    }
}

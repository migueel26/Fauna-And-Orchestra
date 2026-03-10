package net.migueel26.faunaandorchestra.client.entity;

import net.migueel26.faunaandorchestra.entity.custom.WanderingKoalaEntity;
import net.migueel26.faunaandorchestra.entity.custom.koala_workers.WorkerKoalaEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Pose;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class WorkerKoalaRenderer extends GeoEntityRenderer<WorkerKoalaEntity> {
    public WorkerKoalaRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new WorkerKoalaModel());
    }

    @Override
    protected float getShadowRadius(WorkerKoalaEntity entity) {
        return entity.getDimensions(Pose.STANDING).width() * 0.65F;
    }
}

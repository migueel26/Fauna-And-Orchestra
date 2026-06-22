package net.migueel26.faunaandorchestra.client.entity;

import net.migueel26.faunaandorchestra.entity.custom.jazzy_dammys.DanB;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Pose;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class DanBRenderer extends GeoEntityRenderer<DanB> {
    public DanBRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DanBModel());
    }

    @Override
    protected float getShadowRadius(DanB entity) {
        return entity.getDimensions(Pose.STANDING).width() * 0.65F;
    }
}

package net.migueel26.faunaandorchestra.client.entity;

import net.migueel26.faunaandorchestra.entity.custom.jazzy_dammys.DanB;
import net.migueel26.faunaandorchestra.entity.custom.jazzy_dammys.Delroy;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Pose;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class DelroyRenderer extends GeoEntityRenderer<Delroy> {
    public DelroyRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DelroyModel());
    }

    @Override
    protected float getShadowRadius(Delroy entity) {
        return entity.getDimensions(Pose.STANDING).width() * 0.65F;
    }
}

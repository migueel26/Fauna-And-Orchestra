package net.migueel26.faunaandorchestra.client.entity;

import net.migueel26.faunaandorchestra.entity.custom.jazzy_dammys.Delroy;
import net.migueel26.faunaandorchestra.entity.custom.jazzy_dammys.Denise;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Pose;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class DeniseRenderer extends GeoEntityRenderer<Denise> {
    public DeniseRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DeniseModel());
    }

    @Override
    protected float getShadowRadius(Denise entity) {
        return entity.getDimensions(Pose.STANDING).width() * 0.65F;
    }
}

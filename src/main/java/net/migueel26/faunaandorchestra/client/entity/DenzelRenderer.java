package net.migueel26.faunaandorchestra.client.entity;

import net.migueel26.faunaandorchestra.entity.custom.jazzy_dammys.Denise;
import net.migueel26.faunaandorchestra.entity.custom.jazzy_dammys.Denzel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Pose;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class DenzelRenderer extends GeoEntityRenderer<Denzel> {
    public DenzelRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DenzelModel());
    }

    @Override
    protected float getShadowRadius(Denzel entity) {
        return entity.getDimensions(Pose.STANDING).width() * 0.65F;
    }
}

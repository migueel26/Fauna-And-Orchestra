package net.migueel26.faunaandorchestra.client.entity;

import net.migueel26.faunaandorchestra.entity.custom.jazzy_dammys.DanB;
import net.migueel26.faunaandorchestra.entity.custom.village_trotters.Cornelius;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Pose;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CorneliusRenderer extends GeoEntityRenderer<Cornelius> {
    public CorneliusRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new CorneliusModel());
    }

    @Override
    protected float getShadowRadius(Cornelius entity) {
        return entity.getDimensions(Pose.STANDING).width() * 0.65F;
    }
}

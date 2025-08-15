package net.migueel26.faunaandorchestra.client.entity;

import net.migueel26.faunaandorchestra.entity.custom.Faust;
import net.migueel26.faunaandorchestra.entity.custom.Orion;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Pose;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class OrionRenderer extends GeoEntityRenderer<Orion> {
    public OrionRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new OrionModel());
    }

    @Override
    protected float getShadowRadius(Orion entity) {
        return entity.getDimensions(Pose.STANDING).width() * 0.65F;
    }
}

package net.migueel26.faunaandorchestra.client.entity;

import net.migueel26.faunaandorchestra.entity.custom.Faust;
import net.migueel26.faunaandorchestra.entity.custom.KoalaEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Pose;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FaustRenderer extends GeoEntityRenderer<Faust> {
    public FaustRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new FaustModel());
    }

    @Override
    protected float getShadowRadius(Faust entity) {
        return entity.getDimensions(Pose.STANDING).width() * 0.65F;
    }
}

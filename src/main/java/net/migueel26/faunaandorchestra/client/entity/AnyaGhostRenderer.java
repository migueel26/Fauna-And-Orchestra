package net.migueel26.faunaandorchestra.client.entity;

import net.migueel26.faunaandorchestra.entity.custom.AnyaGhost;
import net.migueel26.faunaandorchestra.entity.custom.Orion;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Pose;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class AnyaGhostRenderer extends GeoEntityRenderer<AnyaGhost> {
    public AnyaGhostRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new AnyaGhostModel());
    }

    @Override
    protected float getShadowRadius(AnyaGhost entity) {
        return entity.getDimensions(Pose.STANDING).width() * 0.65F;
    }
}

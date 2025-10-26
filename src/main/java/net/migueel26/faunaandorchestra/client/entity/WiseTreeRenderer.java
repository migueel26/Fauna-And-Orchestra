package net.migueel26.faunaandorchestra.client.entity;

import net.migueel26.faunaandorchestra.entity.custom.Orion;
import net.migueel26.faunaandorchestra.entity.custom.WiseTree;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Pose;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class WiseTreeRenderer extends GeoEntityRenderer<WiseTree> {
    public WiseTreeRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new WiseTreeModel());
    }

    @Override
    protected float getShadowRadius(WiseTree entity) {
        return entity.getDimensions(Pose.STANDING).width() * 0.65F;
    }
}

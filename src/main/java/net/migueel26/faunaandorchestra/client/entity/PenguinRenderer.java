package net.migueel26.faunaandorchestra.client.entity;

import net.migueel26.faunaandorchestra.entity.custom.PenguinEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PenguinRenderer extends GeoEntityRenderer<PenguinEntity> {
    public PenguinRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new PenguinModel());
    }
}

package net.migueel26.faunaandorchestra.client.entity;

import net.migueel26.faunaandorchestra.entity.custom.MantisEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MantisRenderer extends GeoEntityRenderer<MantisEntity> {
    public MantisRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new MantisModel());
    }
}

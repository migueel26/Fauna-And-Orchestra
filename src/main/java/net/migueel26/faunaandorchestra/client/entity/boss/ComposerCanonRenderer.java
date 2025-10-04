package net.migueel26.faunaandorchestra.client.entity.boss;

import net.migueel26.faunaandorchestra.entity.custom.boss.ComposerCanonEntity;
import net.migueel26.faunaandorchestra.entity.custom.boss.TheGreatComposer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Pose;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class ComposerCanonRenderer extends GeoEntityRenderer<ComposerCanonEntity> {
    public ComposerCanonRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ComposerCanonModel());
    }

    @Override
    protected float getShadowRadius(ComposerCanonEntity entity) {
        return entity.getDimensions(Pose.STANDING).width() * 0.65F;
    }
}

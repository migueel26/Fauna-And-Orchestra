package net.migueel26.faunaandorchestra.client.entity;

import net.migueel26.faunaandorchestra.entity.custom.LivingMusicEntity;
import net.migueel26.faunaandorchestra.entity.custom.SproutlingEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Pose;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class LivingMusicRenderer extends GeoEntityRenderer<LivingMusicEntity> {
    public LivingMusicRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new LivingMusicModel());
    }

    @Override
    protected float getShadowRadius(LivingMusicEntity entity) {
        return entity.getDimensions(Pose.STANDING).width() * 0.65F;
    }
}

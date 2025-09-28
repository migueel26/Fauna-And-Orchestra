package net.migueel26.faunaandorchestra.client.entity.boss;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.migueel26.faunaandorchestra.client.entity.BeaverModel;
import net.migueel26.faunaandorchestra.entity.custom.BeaverEntity;
import net.migueel26.faunaandorchestra.entity.custom.boss.TheGreatComposer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Pose;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class TheGreatComposerRenderer extends GeoEntityRenderer<TheGreatComposer> {
    public TheGreatComposerRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new TheGreatComposerModel());

        addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    @Override
    protected float getShadowRadius(TheGreatComposer entity) {
        return entity.getDimensions(Pose.STANDING).width() * 0.65F;
    }
}

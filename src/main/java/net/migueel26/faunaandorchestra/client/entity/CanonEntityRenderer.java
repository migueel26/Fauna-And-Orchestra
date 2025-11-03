package net.migueel26.faunaandorchestra.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.migueel26.faunaandorchestra.entity.custom.AbstractCanonEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Pose;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CanonEntityRenderer extends GeoEntityRenderer<AbstractCanonEntity> {
    private static final ResourceLocation SWORD_TEXTURE = ResourceLocation.withDefaultNamespace("textures/item/iron_sword.png");
    public CanonEntityRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new CanonEntityModel());
    }

    @Override
    protected float getShadowRadius(AbstractCanonEntity entity) {
        return entity.getDimensions(Pose.STANDING).width() * 0.65F;
    }

    @Override
    public void renderRecursively(PoseStack poseStack, AbstractCanonEntity animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        if (bone.getName().equals("iron_sword")) {
            // Use the vanilla iron_sword
            RenderType swordRenderType = RenderType.entityCutoutNoCull(SWORD_TEXTURE);
            VertexConsumer swordBuffer = bufferSource.getBuffer(swordRenderType);

            super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, swordBuffer, isReRender, partialTick, packedLight, packedOverlay, colour);
        } else {
            super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
        }
    }
}

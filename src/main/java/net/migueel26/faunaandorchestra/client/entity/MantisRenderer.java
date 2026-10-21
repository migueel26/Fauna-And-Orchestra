package net.migueel26.faunaandorchestra.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.migueel26.faunaandorchestra.entity.custom.MantisEntity;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.util.ClientUtil;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Pose;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class MantisRenderer extends GeoEntityRenderer<MantisEntity> {
    public MantisRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new MantisModel());

        //addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    @Override
    public void scaleModelForRender(float widthScale, float heightScale, PoseStack poseStack, MantisEntity animatable, BakedGeoModel model, boolean isReRender, float partialTick, int packedLight, int packedOverlay) {
        super.scaleModelForRender(animatable.getScale(), animatable.getScale(), poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);
    }

    @Override
    public void preRender(PoseStack poseStack, MantisEntity animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.shadowRadius = animatable.getDimensions(Pose.STANDING).width * 0.85F;
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public void renderRecursively(PoseStack poseStack, MantisEntity mantis, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (bone.getName().equals("bow") || bone.getName().equals("violin")) {
            if(!mantis.isHoldingInstrument() || mantis.isAngry()) {
                bone.setHidden(true);
            }
        }

        switch (bone.getName()) {
            case "fake_moustache" -> ClientUtil.setHatHidden(bone, mantis.getHat(), ModItems.FAKE_MOUSTACHE.get());
            case "rose" -> ClientUtil.setHatHidden(bone, mantis.getHat(), ModItems.ROSE.get());
            case "mask_of_the_enlightened" -> ClientUtil.setHatHidden(bone, mantis.getHat(), ModItems.MASK_OF_THE_ENLIGHTENED.get());
        }

        buffer = ClientUtil.renderSpecialClothesOnHoliday(poseStack, mantis, bone, renderType, bufferSource, buffer, isReRender, packedLight, packedOverlay);

        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}

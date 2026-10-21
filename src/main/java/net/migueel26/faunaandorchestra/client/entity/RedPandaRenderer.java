package net.migueel26.faunaandorchestra.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.migueel26.faunaandorchestra.entity.custom.RedPandaEntity;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.util.ClientUtil;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Pose;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class RedPandaRenderer extends GeoEntityRenderer<RedPandaEntity> {
    public RedPandaRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new RedPandaModel());
    }

    @Override
    public void preRender(PoseStack poseStack, RedPandaEntity animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.shadowRadius = animatable.getDimensions(Pose.STANDING).width * 0.65F;
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public void scaleModelForRender(float widthScale, float heightScale, PoseStack poseStack, RedPandaEntity animatable, BakedGeoModel model, boolean isReRender, float partialTick, int packedLight, int packedOverlay) {
        super.scaleModelForRender(animatable.getScale(), animatable.getScale(), poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);
    }

    @Override
    public void renderRecursively(PoseStack poseStack, RedPandaEntity redPanda, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (bone.getName().equals("keytar")) {
            if (!redPanda.isHoldingInstrument()) {
                bone.setHidden(true);
            }
        }

        if (bone.getName().equals("head")) {
            if (redPanda.isBaby()) {
                bone.setScaleX(1.35F);
                bone.setScaleY(1.35F);
                bone.setScaleZ(1.35F);
            } else {
                bone.setScaleX(1.0F);
                bone.setScaleY(1.0F);
                bone.setScaleZ(1.0F);
            }
        }

        switch (bone.getName()) {
            case "fake_moustache" -> ClientUtil.setHatHidden(bone, redPanda.getHat(), ModItems.FAKE_MOUSTACHE.get());
            case "top_hat" -> ClientUtil.setHatHidden(bone, redPanda.getHat(), ModItems.TOP_HAT.get());
            case "imaginal_disk" -> ClientUtil.setHatHidden(bone, redPanda.getHat(), ModItems.IMAGINAL_DISK.get());
        }

        buffer = ClientUtil.renderSpecialClothesOnHoliday(poseStack, redPanda, bone, renderType, bufferSource, buffer, isReRender, packedLight, packedOverlay);

        super.renderRecursively(poseStack, redPanda, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}

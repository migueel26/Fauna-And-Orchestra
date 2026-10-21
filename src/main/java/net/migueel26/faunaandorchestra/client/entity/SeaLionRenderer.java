package net.migueel26.faunaandorchestra.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.migueel26.faunaandorchestra.entity.custom.PenguinEntity;
import net.migueel26.faunaandorchestra.entity.custom.SeaLionEntity;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.util.ClientUtil;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Pose;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SeaLionRenderer extends GeoEntityRenderer<SeaLionEntity> {
    public SeaLionRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SeaLionModel());
    }

    @Override
    public void preRender(PoseStack poseStack, SeaLionEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.shadowRadius = entity.getDimensions(Pose.STANDING).width * 0.65F;
        super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public void render(SeaLionEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        float scale = 1.25f;
        poseStack.scale(scale, scale, scale);

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public void renderRecursively(PoseStack poseStack, SeaLionEntity seaLion, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (bone.getName().equals("strings") || bone.getName().equals("left_drumstick") || bone.getName().equals("right_drumstick")) {
            if (!seaLion.isHoldingInstrument()) {
                bone.setHidden(true);
            }
        }

        switch (bone.getName()) {
            case "top_hat" -> ClientUtil.setHatHidden(bone, seaLion.getHat(), ModItems.TOP_HAT.get());
            case "right_monocle" -> ClientUtil.setHatHidden(bone, seaLion.getHat(), ModItems.RIGHT_MONOCLE.get());
            case "left_monocle" -> ClientUtil.setHatHidden(bone, seaLion.getHat(), ModItems.LEFT_MONOCLE.get());
        }

        buffer = ClientUtil.renderSpecialClothesOnHoliday(poseStack, seaLion, bone, renderType, bufferSource, buffer, isReRender, packedLight, packedOverlay);

        super.renderRecursively(poseStack, seaLion, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}

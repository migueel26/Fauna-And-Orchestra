package net.migueel26.faunaandorchestra.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.migueel26.faunaandorchestra.entity.custom.EmperorPenguinEntity;
import net.migueel26.faunaandorchestra.entity.custom.PenguinEntity;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.util.ClientUtil;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Pose;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EmperorPenguinRenderer extends GeoEntityRenderer<EmperorPenguinEntity> {
    public EmperorPenguinRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new EmperorPenguinModel());
    }

    @Override
    protected float getShadowRadius(EmperorPenguinEntity entity) {
        return entity.getDimensions(Pose.STANDING).width() * 0.65F;
    }

    @Override
    public void renderRecursively(PoseStack poseStack, EmperorPenguinEntity penguin, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        if (bone.getName().equals("long_flute")) {
            if (!penguin.isHoldingInstrument()) {
                bone.setHidden(true);
            }
        }

        switch (bone.getName()) {
            case "santa_hat" -> ClientUtil.setHatHidden(bone, penguin.getHat(), ModItems.SANTA_HAT.get());
            case "baseball_cap" -> ClientUtil.setHatHidden(bone, penguin.getHat(), ModItems.BASEBALL_CAP.get());
            case "phantom_mask" -> ClientUtil.setHatHidden(bone, penguin.getHat(), ModItems.PHANTOM_MASK.get());
        }

        buffer = ClientUtil.renderSpecialClothesOnHoliday(poseStack, penguin, bone, renderType, bufferSource, buffer, isReRender, packedLight, packedOverlay);

        super.renderRecursively(poseStack, penguin, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }
}

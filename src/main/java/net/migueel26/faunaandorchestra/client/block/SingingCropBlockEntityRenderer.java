package net.migueel26.faunaandorchestra.client.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.migueel26.faunaandorchestra.block.custom.SingingCropBlock;
import net.migueel26.faunaandorchestra.block.entity.SingingCropBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class SingingCropBlockEntityRenderer extends GeoBlockRenderer<SingingCropBlockEntity> {
    public SingingCropBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(new SingingCropModel());
    }

    @Override
    public void renderRecursively(PoseStack poseStack, SingingCropBlockEntity animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        int age = animatable.getBlockState().getValue(SingingCropBlock.AGE);
        if (bone.getName().startsWith("age_")) {
            bone.setHidden(!bone.getName().equals("age_" + age));
        }
        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public void actuallyRender(PoseStack poseStack, SingingCropBlockEntity animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        int age = animatable.getBlockState().getValue(SingingCropBlock.AGE);
        if (age == 0) {
            model.getBone("age_0").get().setHidden(false);
            model.getBone("age_1").get().setHidden(true);
            model.getBone("age_2").get().setHidden(true);
            model.getBone("age_3").get().setHidden(true);
        } else if (age == 1) {
            model.getBone("age_0").get().setHidden(true);
            model.getBone("age_1").get().setHidden(false);
            model.getBone("age_2").get().setHidden(true);
            model.getBone("age_3").get().setHidden(true);
        } else if (age == 2) {
            model.getBone("age_0").get().setHidden(true);
            model.getBone("age_1").get().setHidden(true);
            model.getBone("age_2").get().setHidden(false);
            model.getBone("age_3").get().setHidden(true);
        } else {
            model.getBone("age_0").get().setHidden(true);
            model.getBone("age_1").get().setHidden(true);
            model.getBone("age_2").get().setHidden(true);
            model.getBone("age_3").get().setHidden(false);
        }
    }
}

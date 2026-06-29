package net.migueel26.faunaandorchestra.client.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.migueel26.faunaandorchestra.block.custom.FloraEnhancerBlock;
import net.migueel26.faunaandorchestra.block.custom.MotherStatueBlock;
import net.migueel26.faunaandorchestra.block.entity.FloraEnhancerBlockEntity;
import net.migueel26.faunaandorchestra.block.entity.MotherStatueBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class FloraEnhancerBlockEntityRenderer extends GeoBlockRenderer<FloraEnhancerBlockEntity> {

    public FloraEnhancerBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(new FloraEnhancerModel());
    }

    @Override
    public boolean shouldRenderOffScreen(FloraEnhancerBlockEntity blockEntity) {
        return true;
    }

    @Override
    public void renderRecursively(PoseStack poseStack, FloraEnhancerBlockEntity animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (bone.getName().equals("flora_forta")) {
            bone.setHidden(!(animatable.getMoisture() == FloraEnhancerBlock.MAX_MOISTURE));
        }
        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}

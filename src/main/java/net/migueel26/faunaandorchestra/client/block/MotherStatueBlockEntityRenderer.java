package net.migueel26.faunaandorchestra.client.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.migueel26.faunaandorchestra.block.custom.ComposerGravestoneBlock;
import net.migueel26.faunaandorchestra.block.custom.MotherStatueBlock;
import net.migueel26.faunaandorchestra.block.entity.ComposerGravestoneBlockEntity;
import net.migueel26.faunaandorchestra.block.entity.MotherStatueBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class MotherStatueBlockEntityRenderer extends GeoBlockRenderer<MotherStatueBlockEntity> {

    public MotherStatueBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(new MotherStatueModel());
    }

    @Override
    public boolean shouldRenderOffScreen(MotherStatueBlockEntity blockEntity) {
        return true;
    }

    @Override
    public AABB getRenderBoundingBox(MotherStatueBlockEntity blockEntity) {
        return new AABB(
                blockEntity.getBlockPos().offset(-16, -16, -16).getCenter(),
                blockEntity.getBlockPos().offset(16, 16, 16).getCenter()
        );
    }

    @Override
    public void renderRecursively(PoseStack poseStack, MotherStatueBlockEntity animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        if (bone.getName().equals("crown")) {
            bone.setHidden(!animatable.getBlockState().getValue(MotherStatueBlock.LEGENDARY));
        }
        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }

    @Override
    public void actuallyRender(PoseStack poseStack, MotherStatueBlockEntity gravestone, BakedGeoModel model, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        BlockState state = gravestone.getBlockState();
        if (state.getValue(MotherStatueBlock.HALF) == DoubleBlockHalf.UPPER) {
            return;
        }
        super.actuallyRender(poseStack, gravestone, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }
}

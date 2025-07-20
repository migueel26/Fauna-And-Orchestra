package net.migueel26.faunaandorchestra.client.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.migueel26.faunaandorchestra.block.custom.ComposerGravestoneBlock;
import net.migueel26.faunaandorchestra.block.entity.ComposerGravestoneBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class ComposerGravestoneBlockEntityRenderer extends GeoBlockRenderer<ComposerGravestoneBlockEntity> {

    public ComposerGravestoneBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(new ComposerGravestoneModel());
    }

    @Override
    public void actuallyRender(PoseStack poseStack, ComposerGravestoneBlockEntity gravestone, BakedGeoModel model, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        BlockState state = gravestone.getBlockState();
        if (state.getValue(ComposerGravestoneBlock.PART) == BedPart.HEAD) {
            return;
        }
        super.actuallyRender(poseStack, gravestone, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }
}

package net.migueel26.faunaandorchestra.client.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.migueel26.faunaandorchestra.block.custom.MotherStatueBlock;
import net.migueel26.faunaandorchestra.block.entity.BeaverStatueBlockEntity;
import net.migueel26.faunaandorchestra.block.entity.ComposerGravestoneBlockEntity;
import net.migueel26.faunaandorchestra.block.entity.MotherStatueBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class BeaverStatueBlockEntityRenderer extends GeoBlockRenderer<BeaverStatueBlockEntity> {

    public BeaverStatueBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(new BeaverStatueModel());
    }

    @Override
    public boolean shouldRenderOffScreen(BeaverStatueBlockEntity blockEntity) {
        return true;
    }

    @Override
    public void actuallyRender(PoseStack poseStack, BeaverStatueBlockEntity statue, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        BlockState state = statue.getBlockState();
        if (state.getValue(MotherStatueBlock.HALF) == DoubleBlockHalf.UPPER) {
            return;
        }
        super.actuallyRender(poseStack, statue, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}

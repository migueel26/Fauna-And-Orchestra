package net.migueel26.faunaandorchestra.client.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.migueel26.faunaandorchestra.block.custom.ComposerGravestoneBlock;
import net.migueel26.faunaandorchestra.block.custom.TipCaseBlock;
import net.migueel26.faunaandorchestra.block.entity.ComposerGravestoneBlockEntity;
import net.migueel26.faunaandorchestra.block.entity.TipCaseBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class TipCaseBlockEntityRenderer extends GeoBlockRenderer<TipCaseBlockEntity> {

    public TipCaseBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(new TipCaseModel());
    }

    @Override
    public void actuallyRender(PoseStack poseStack, TipCaseBlockEntity tipCase, BakedGeoModel model, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        BlockState state = tipCase.getBlockState();

        GeoBone coins1 = model.getBone("coins1").get();
        GeoBone coins2 = model.getBone("coins2").get();
        GeoBone coins3 = model.getBone("coins3").get();

        coins1.setHidden(true);
        coins2.setHidden(true);
        coins3.setHidden(true);

        if (state.getValue(TipCaseBlock.TIPS) >= 16) {
            coins1.setHidden(false);
        }

        if (state.getValue(TipCaseBlock.TIPS) >= 32) {
            coins2.setHidden(false);
        }

        if (state.getValue(TipCaseBlock.TIPS) >= 64) {
            coins3.setHidden(false);
        }

        super.actuallyRender(poseStack, tipCase, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }
}

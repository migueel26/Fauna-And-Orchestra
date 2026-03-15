package net.migueel26.faunaandorchestra.client.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.migueel26.faunaandorchestra.block.custom.MotherStatueBlock;
import net.migueel26.faunaandorchestra.block.custom.SewingMachineBlock;
import net.migueel26.faunaandorchestra.block.entity.BeaverStatueBlockEntity;
import net.migueel26.faunaandorchestra.block.entity.SewingMachineBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class SewingMachineBlockEntityRenderer extends GeoBlockRenderer<SewingMachineBlockEntity> {

    public SewingMachineBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(new SewingMachineModel());
    }

    @Override
    public boolean shouldRenderOffScreen(SewingMachineBlockEntity blockEntity) {
        return true;
    }

    @Override
    public AABB getRenderBoundingBox(SewingMachineBlockEntity blockEntity) {
        return new AABB(
                blockEntity.getBlockPos().offset(-16, -16, -16).getCenter(),
                blockEntity.getBlockPos().offset(16, 16, 16).getCenter()
        );
    }

    @Override
    public void actuallyRender(PoseStack poseStack, SewingMachineBlockEntity statue, BakedGeoModel model, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        BlockState state = statue.getBlockState();
        if (state.getValue(SewingMachineBlock.PART) == BedPart.FOOT) {
            return;
        }
        super.actuallyRender(poseStack, statue, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }
}

package net.migueel26.faunaandorchestra.client.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.migueel26.faunaandorchestra.block.custom.ListenerContainerBlock;
import net.migueel26.faunaandorchestra.block.entity.ListenerBlockEntity;
import net.migueel26.faunaandorchestra.block.entity.ListenerContainerBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class ListenerContainerBlockEntityRenderer extends GeoBlockRenderer<ListenerContainerBlockEntity> {
    public ListenerContainerBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(new ListenerContainerModel());
    }

    @Override
    public void actuallyRender(PoseStack poseStack, ListenerContainerBlockEntity listener, BakedGeoModel model, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        BlockState state = listener.getBlockState();
        GeoBone bottle = model.getBone("bottle").get();
        GeoBone tap = model.getBone("tap").get();
        GeoBone water = model.getBone("water").get();

        if (!state.getValue(ListenerContainerBlock.BOTTLE)) {
            bottle.setHidden(true);
        } else {
            bottle.setHidden(false);
            int droplets = state.getValue(ListenerContainerBlock.DROPLETS);
            if (droplets < 32) {
                water.setHidden(true);
            } else {
                water.setHidden(false);
            }
            if (droplets < 64) {
                tap.setHidden(true);
            } else {
                tap.setHidden(false);
            }
        }
        super.actuallyRender(poseStack, listener, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }
}

package net.migueel26.faunaandorchestra.client.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.migueel26.faunaandorchestra.block.custom.ListenerContainerBlock;
import net.migueel26.faunaandorchestra.block.entity.DiscordNucleiBlockEntity;
import net.migueel26.faunaandorchestra.block.entity.ListenerContainerBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class DiscordNucleiBlockEntityRenderer extends GeoBlockRenderer<DiscordNucleiBlockEntity> {
    public DiscordNucleiBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(new DiscordNucleiModel());
    }

    @Override
    public void actuallyRender(PoseStack poseStack, DiscordNucleiBlockEntity blockEntity, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);

        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        ItemStack stack = blockEntity.inventory.getStackInSlot(0);

        poseStack.pushPose();
        poseStack.translate(0f, 0.8f, 0f);
        poseStack.scale(0.5f, 0.5f, 0.5f);
        poseStack.mulPose(Axis.YP.rotationDegrees(blockEntity.getRenderingRotation()));

        if (blockEntity.getLevel() != null) {
            itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, getLightLevel(blockEntity.getLevel(),
                    blockEntity.getBlockPos()), OverlayTexture.NO_OVERLAY, poseStack, bufferSource, blockEntity.getLevel(), 1);
            poseStack.popPose();
        }
    }

    private int getLightLevel(Level level, BlockPos pos) {
        int bLight = level.getBrightness(LightLayer.BLOCK, pos);
        int sLight = level.getBrightness(LightLayer.SKY, pos);
        return LightTexture.pack(bLight, sLight);
    }
}

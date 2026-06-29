package net.migueel26.faunaandorchestra.client.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.migueel26.faunaandorchestra.block.entity.HangingJarBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraftforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.List;

public class HangingJarBlockEntityRenderer implements BlockEntityRenderer<HangingJarBlockEntity> {
    public HangingJarBlockEntityRenderer(BlockEntityRendererProvider.Context context) {

    }

    private int getLightLevel(Level level, BlockPos pos) {
        int bLight = level.getBrightness(LightLayer.BLOCK, pos);
        int sLight = level.getBrightness(LightLayer.SKY, pos);
        return LightTexture.pack(bLight, sLight);
    }

    @Override
    public void render(HangingJarBlockEntity blockEntity, float v, PoseStack poseStack, MultiBufferSource bufferSource, int i, int i1) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        ItemStackHandler inventory = blockEntity.inventory;
        float offset = 0.02f;

        List<ItemStack> stacks = new ArrayList<>(inventory.getSlots());

        for (int slot = 0; slot < inventory.getSlots(); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (!stack.isEmpty()) {
                stacks.add(stack);
            }
        }

        for (ItemStack stack : stacks) {
            float rotation = 90;
            boolean isBlock = stack.getItem() instanceof BlockItem && itemRenderer.getModel(stack, null, null, 0).isGui3d();

            if (isBlock) {
                offset += 0.04f;
                rotation = 0;
            }

            poseStack.pushPose();
            poseStack.translate(0.5f, offset, 0.5f);
            poseStack.scale(isBlock ? 0.5f : 0.4f, isBlock ? 0.15f : 0.4f, isBlock ? 0.5f : 0.4f);
            poseStack.mulPose(Axis.XP.rotationDegrees(rotation));

            offset += isBlock ? 0.04f : 0.035f;

            itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, getLightLevel(blockEntity.getLevel(),
                    blockEntity.getBlockPos()), OverlayTexture.NO_OVERLAY, poseStack, bufferSource, blockEntity.getLevel(), 1);
            poseStack.popPose();
        }

    }
}

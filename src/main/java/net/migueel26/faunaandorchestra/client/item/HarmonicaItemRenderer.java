package net.migueel26.faunaandorchestra.client.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.migueel26.faunaandorchestra.item.custom.HarmonicaItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class HarmonicaItemRenderer extends GeoItemRenderer<HarmonicaItem> {
    public HarmonicaItemRenderer() {
        super(new HarmonicaItemModel());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {

        // first person fix
        if (transformType == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND || transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
            Minecraft mc = Minecraft.getInstance();

            if (mc.player != null && mc.player.isUsingItem() && mc.player.getUseItem().is(stack.getItem())) {
                poseStack.pushPose();

                // X (L/R), Y (U/D), Z (F/B)
                poseStack.translate(0.1, -0.2, -0.45);
                poseStack.scale(0.8f, 0.8f, 0.8f);

                super.renderByItem(stack, transformType, poseStack, buffer, packedLight, packedOverlay);
                poseStack.popPose();
                return;
            }
        }

        super.renderByItem(stack, transformType, poseStack, buffer, packedLight, packedOverlay);
    }
}

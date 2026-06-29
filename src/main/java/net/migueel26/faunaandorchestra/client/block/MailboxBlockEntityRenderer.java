package net.migueel26.faunaandorchestra.client.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.migueel26.faunaandorchestra.block.custom.MailboxBlock;
import net.migueel26.faunaandorchestra.block.custom.MotherStatueBlock;
import net.migueel26.faunaandorchestra.block.entity.BeaverStatueBlockEntity;
import net.migueel26.faunaandorchestra.block.entity.MailboxBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.AABB;
import org.apache.logging.log4j.core.net.MailManager;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class MailboxBlockEntityRenderer extends GeoBlockRenderer<MailboxBlockEntity> {

    public MailboxBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(new MailboxModel());
    }

    @Override
    public boolean shouldRenderOffScreen(MailboxBlockEntity blockEntity) {
        return true;
    }

    @Override
    public void actuallyRender(PoseStack poseStack, MailboxBlockEntity mailbox, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        BlockState state = mailbox.getBlockState();
        if (state.getValue(MailboxBlock.HALF) == DoubleBlockHalf.UPPER) {
            return;
        }

        model.getBone("body").get().setHidden(!state.getValue(MailboxBlock.MAILBIRD));
        super.actuallyRender(poseStack, mailbox, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}

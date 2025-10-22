package net.migueel26.faunaandorchestra.client.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.migueel26.faunaandorchestra.block.entity.TheGreatHeadBlockEntity;
import net.migueel26.faunaandorchestra.block.entity.VoiceChamberBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class VoiceChamberBlockEntityRenderer extends GeoBlockRenderer<VoiceChamberBlockEntity> {
    public VoiceChamberBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(new VoiceChamberModel());
    }

    @Override
    public void actuallyRender(PoseStack poseStack, VoiceChamberBlockEntity animatable, BakedGeoModel model, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        model.getBone("floating_voice").get().setHidden(animatable.getVoice().isEmpty());
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }
}

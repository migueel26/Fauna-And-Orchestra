package net.migueel26.faunaandorchestra.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.migueel26.faunaandorchestra.entity.custom.village_trotters.VillageTrotterEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Pose;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class VillageTrotterRenderer extends GeoEntityRenderer<VillageTrotterEntity> {
    public VillageTrotterRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new VillageTrotterModel());
    }

    @Override
    protected float getShadowRadius(VillageTrotterEntity entity) {
        float shadowRadius = entity.getVillageTrotterType() == VillageTrotterEntity.VillageTrotter.MANTIS ? 0.85F : 0.65F;

        return entity.getDimensions(Pose.STANDING).width() * shadowRadius;
    }

    // The baked models are shared with the regular musician renderers, which toggle these bones,
    // so their state has to be set explicitly on every render
    @Override
    public void renderRecursively(PoseStack poseStack, VillageTrotterEntity trotter, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        switch (bone.getName()) {
            // Hats and accessories
            case "fake_moustache", "rose", "mask_of_the_enlightened", "top_hat", "imaginal_disk",
                 "santa_hat", "baseball_cap", "propeller_hat", "left_monocle", "right_monocle" -> bone.setHidden(true);
            // Instruments
            case "violin", "bow", "keytar", "long_flute", "double_bass" -> bone.setHidden(false);
            // Baby musicians scale their head
            case "head" -> {
                bone.setScaleX(1.0F);
                bone.setScaleY(1.0F);
                bone.setScaleZ(1.0F);
            }
        }

        super.renderRecursively(poseStack, trotter, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }
}

package net.migueel26.faunaandorchestra.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.migueel26.faunaandorchestra.entity.custom.AbstractCanonEntity;
import net.migueel26.faunaandorchestra.entity.custom.PlayerCanonEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;

public class CanonItemLayer extends BlockAndItemGeoLayer<AbstractCanonEntity> {

    public CanonItemLayer(GeoRenderer<AbstractCanonEntity> renderer) {
        super(renderer);
    }

    @Nullable
    @Override
    protected ItemStack getStackForBone(GeoBone bone, AbstractCanonEntity animatable) {
        if (bone.getName().equals("iron_sword")) {
            if (animatable instanceof PlayerCanonEntity) {
                return new ItemStack(Items.IRON_SWORD);
            }
        }
        return null;
    }

    @Override
    protected ItemDisplayContext getTransformTypeForStack(GeoBone bone, ItemStack stack, AbstractCanonEntity animatable) {
        if (bone.getName().equals("iron_sword")) {
            return ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
        }
        return ItemDisplayContext.NONE;
    }

    @Override
    protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack, AbstractCanonEntity animatable, MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay) {
        poseStack.mulPose(Axis.XP.rotationDegrees(-90));
        poseStack.mulPose(Axis.YP.rotationDegrees(180));

        super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
    }
}

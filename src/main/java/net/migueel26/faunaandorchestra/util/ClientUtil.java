package net.migueel26.faunaandorchestra.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.migueel26.faunaandorchestra.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.cache.object.GeoQuad;
import software.bernie.geckolib.cache.object.GeoVertex;
import software.bernie.geckolib.util.RenderUtil;

import java.time.LocalDate;
import java.time.Month;

public class ClientUtil {
    private static final ItemStack CARVED_PUMPKIN = new ItemStack(Items.CARVED_PUMPKIN);

    public static boolean isHalloween() {
        LocalDate date = LocalDate.now();
        return date.getMonth() == Month.OCTOBER && date.getDayOfMonth() == 31;
    }

    public static void setHatHidden(GeoBone bone, Item wornHat, Item boneHat) {
        bone.setHidden(wornHat != boneHat || (isHalloween() && Config.halloweenPumpkins));
    }

    public static VertexConsumer renderSpecialClothesOnHoliday(PoseStack poseStack, LivingEntity entity, GeoBone head, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, int packedLight, int packedOverlay) {
        if (!isReRender && head.getName().equals("head")) {
            if (isHalloween() && Config.halloweenPumpkins) {
                renderPumpkin(head, entity, poseStack, bufferSource, packedLight, packedOverlay);
            }

            return bufferSource.getBuffer(renderType);
        }
        return buffer;
    }

    private static void renderPumpkin(GeoBone head, LivingEntity entity, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (head.isHidden() || head.getCubes().isEmpty()) {
            return;
        }

        poseStack.pushPose();

        // apply the head bone's transformations to the pose stack
        RenderUtil.prepMatrixForBone(poseStack, head);

        // this loop calculates the bounding box of the head bone in model space
        float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE, minZ = Float.MAX_VALUE;
        float maxX = -Float.MAX_VALUE, maxY = -Float.MAX_VALUE, maxZ = -Float.MAX_VALUE;
        boolean hasVertices = false;

        for (GeoCube cube : head.getCubes()) {
            for (GeoQuad quad : cube.quads()) {
                if (quad == null) continue;
                for (GeoVertex vertex : quad.vertices()) {
                    Vector3f pos = vertex.position();
                    minX = Math.min(minX, pos.x());
                    maxX = Math.max(maxX, pos.x());
                    minY = Math.min(minY, pos.y());
                    maxY = Math.max(maxY, pos.y());
                    minZ = Math.min(minZ, pos.z());
                    maxZ = Math.max(maxZ, pos.z());
                    hasVertices = true;
                }
            }
        }

        if (!hasVertices) {
            poseStack.popPose();
            return;
        }

        float sizeX = maxX - minX;
        float sizeY = maxY - minY;
        float sizeZ = maxZ - minZ;

        float centerX = (minX + maxX) / 2.0F;
        float centerY = (minY + maxY) / 2.0F;
        float centerZ = (minZ + maxZ) / 2.0F;

        poseStack.translate(centerX, centerY, centerZ);

        // scale up a bit more for beaks, ears and such
        float maxDimension = Math.max(sizeX, Math.max(sizeY, sizeZ));
        float scale = maxDimension * 1.1F;
        poseStack.scale(scale, scale, scale);

        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        itemRenderer.renderStatic(entity, CARVED_PUMPKIN, ItemDisplayContext.NONE, false, poseStack, bufferSource, entity.level(), packedLight, packedOverlay, entity.getId());

        poseStack.popPose();
    }
}

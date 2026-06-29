package net.migueel26.faunaandorchestra.client.entity;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.custom.QuirkyFrogEntity;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class QuirkyFrogModel extends GeoModel<QuirkyFrogEntity> {
    private static final ResourceLocation NORMAL_TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/entity/quirky_frog.png");
    private static final ResourceLocation TAILCOAT_TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/entity/quirky_frog_tailcoat.png");
    private static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "animations/entity/quirky_frog.animation.json");
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "geo/entity/quirky_frog.geo.json");

    @Override
    public ResourceLocation getModelResource(QuirkyFrogEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(QuirkyFrogEntity frog) {
        if (frog.getCostume() == ModItems.TAILCOAT.get()) {
            return TAILCOAT_TEXTURE;
        } else {
            return NORMAL_TEXTURE;
        }
    }

    @Override
    public ResourceLocation getAnimationResource(QuirkyFrogEntity animatable) {
        return ANIMATIONS;
    }

    @Override
    public void setCustomAnimations(QuirkyFrogEntity entity, long instanceId, AnimationState<QuirkyFrogEntity> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("frog_head");

        if (head != null && !entity.isConducting()) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

            if (entity.isHoldingBaton()) {
                head.setRotX((entityData.headPitch()+45) * Mth.DEG_TO_RAD);
                head.setRotZ(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
            } else {
                head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
                head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
            }
        }

        CoreGeoBone baton = getAnimationProcessor().getBone("baton");
        CoreGeoBone legendaryBaton = getAnimationProcessor().getBone("legendary_baton");
        if (entity.isHoldingBaton()) {
            if (entity.isHoldingLegendaryBaton()) {
                baton.setHidden(true);
                legendaryBaton.setHidden(false);
            } else {
                baton.setHidden(false);
                legendaryBaton.setHidden(true);
            }
        } else {
            baton.setHidden(true);
            legendaryBaton.setHidden(true);
        }
    }
}

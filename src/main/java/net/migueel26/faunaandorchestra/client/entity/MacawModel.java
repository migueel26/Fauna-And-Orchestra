package net.migueel26.faunaandorchestra.client.entity;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.custom.MacawEntity;
import net.migueel26.faunaandorchestra.entity.custom.MantisEntity;
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

public class MacawModel extends GeoModel<MacawEntity> {
    private static final ResourceLocation NORMAL_TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/entity/macaw.png");
    private static final ResourceLocation TUXEDO_TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/entity/macaw_tuxedo.png");
    private static final ResourceLocation WHITE_TUXEDO_TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/entity/macaw_white_tuxedo.png");
    private static final ResourceLocation BASEBALL_TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/entity/macaw_baseball.png");
    private static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "animations/entity/macaw.animation.json");
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "geo/entity/macaw.geo.json");

    @Override
    public ResourceLocation getModelResource(MacawEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(MacawEntity macaw) {
        if (macaw.getCostume() == ModItems.TUXEDO.get()) {
            return TUXEDO_TEXTURE;
        } else if (macaw.getCostume() == ModItems.WHITE_TUXEDO.get()) {
            return WHITE_TUXEDO_TEXTURE;
        } else if (macaw.getCostume() == ModItems.BASEBALL_JACKET.get()) {
            return BASEBALL_TEXTURE;
        } else {
            return NORMAL_TEXTURE;
        }
    }

    @Override
    public ResourceLocation getAnimationResource(MacawEntity animatable) {
        return ANIMATIONS;
    }

    @Override
    public void setCustomAnimations(MacawEntity macaw, long instanceId, AnimationState<MacawEntity> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("head");

        if (head != null && !macaw.isPlayingInstrument() && !animationState.isMoving()) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);

        }

        CoreGeoBone double_bass = getAnimationProcessor().getBone("double_bass");

        getAnimationProcessor().getBone("right_monocle").setHidden(macaw.getHat() != ModItems.RIGHT_MONOCLE.get());
        getAnimationProcessor().getBone("left_monocle").setHidden(macaw.getHat() != ModItems.LEFT_MONOCLE.get());
        getAnimationProcessor().getBone("rose").setHidden(macaw.getHat() != ModItems.ROSE.get());
        getAnimationProcessor().getBone("baseball_cap").setHidden(macaw.getHat() != ModItems.BASEBALL_CAP.get());

        double_bass.setHidden(!macaw.isHoldingInstrument());
    }
}

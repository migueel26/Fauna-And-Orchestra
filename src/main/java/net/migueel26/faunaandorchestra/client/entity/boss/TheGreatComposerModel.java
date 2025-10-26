package net.migueel26.faunaandorchestra.client.entity.boss;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.custom.LemurEntity;
import net.migueel26.faunaandorchestra.entity.custom.boss.TheGreatComposer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class TheGreatComposerModel extends GeoModel<TheGreatComposer> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/entity/the_great_composer.png");
    private static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "animations/entity/the_great_composer.animation.json");
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "geo/entity/the_great_composer.geo.json");

    @Override
    public ResourceLocation getModelResource(TheGreatComposer animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(TheGreatComposer animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(TheGreatComposer animatable) {
        return ANIMATIONS;
    }

    @Override
    public void setCustomAnimations(TheGreatComposer theGreatComposer, long instanceId, AnimationState<TheGreatComposer> animationState) {
        GeoBone head = getAnimationProcessor().getBone("head");

        if (head != null && !theGreatComposer.isBusy() && !theGreatComposer.isSpawning()) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
        }

        GeoBone chest = getAnimationProcessor().getBone("chest");

        if (theGreatComposer.isFinalPhase() && theGreatComposer.getState() != TheGreatComposer.ComposerBossState.RESURRECTING) {
            chest.setHidden(true);
        } else {
            chest.setHidden(false);
        }

    }
}

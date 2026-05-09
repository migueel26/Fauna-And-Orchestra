package net.migueel26.faunaandorchestra.client.entity;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.custom.LemurEntity;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class LemurModel extends GeoModel<LemurEntity> {
    private static final ResourceLocation NORMAL_TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/entity/lemur.png");
    private static final ResourceLocation TUXEDO_TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/entity/lemur_tuxedo.png");
    private static final ResourceLocation WHITE_TUXEDO_TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/entity/lemur_white_tuxedo.png");
    private static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "animations/entity/lemur.animation.json");
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "geo/entity/lemur.geo.json");

    @Override
    public ResourceLocation getModelResource(LemurEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(LemurEntity lemur) {
        return switch (lemur.getCostume()) {
            case Item costume when costume == ModItems.TUXEDO.get() -> TUXEDO_TEXTURE;
            case Item costume when costume == ModItems.WHITE_TUXEDO.get() -> WHITE_TUXEDO_TEXTURE;
            default -> NORMAL_TEXTURE;
        };
    }

    @Override
    public ResourceLocation getAnimationResource(LemurEntity animatable) {
        return ANIMATIONS;
    }

    @Override
    public void setCustomAnimations(LemurEntity lemur, long instanceId, AnimationState<LemurEntity> animationState) {
        GeoBone head = getAnimationProcessor().getBone("head");

        if (head != null && !lemur.isPlayingInstrument()) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
        }

        GeoBone oboe = getAnimationProcessor().getBone("oboe");
        GeoBone secondary_oboe = getAnimationProcessor().getBone("secondary_oboe");

        oboe.setHidden(!lemur.isHoldingInstrument());

        getAnimationProcessor().getBone("fake_moustache").setHidden(lemur.getHat() != ModItems.FAKE_MOUSTACHE.get());
        getAnimationProcessor().getBone("top_hat").setHidden(lemur.getHat() != ModItems.TOP_HAT.get());
        getAnimationProcessor().getBone("rose").setHidden(lemur.getHat() != ModItems.ROSE.get());

        secondary_oboe.setHidden(!animationState.isCurrentAnimation(LemurEntity.WALK_OBOE));
    }
}

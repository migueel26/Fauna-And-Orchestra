package net.migueel26.faunaandorchestra.client.entity;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.custom.MacawEntity;
import net.migueel26.faunaandorchestra.entity.custom.MadameButterflyEntity;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class MadameButterflyModel extends GeoModel<MadameButterflyEntity> {
    private static final ResourceLocation NORMAL_TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/entity/madame_butterfly.png");
    private static final ResourceLocation TUXEDO_TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/entity/madame_butterfly_tuxedo.png");
    private static final ResourceLocation GOLDEN_TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/entity/madame_butterfly_gold.png");
    private static final ResourceLocation COLORFUL_TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/entity/madame_butterfly_colorful.png");
    private static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "animations/entity/madame_butterfly.animation.json");
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "geo/entity/madame_butterfly.geo.json");

    @Override
    public ResourceLocation getModelResource(MadameButterflyEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(MadameButterflyEntity butterfly) {
        return switch (butterfly.getCostume()) {
            case Item costume when costume == ModItems.SILVER_TINT.get() -> TUXEDO_TEXTURE;
            case Item costume when costume == ModItems.GOLDEN_TINT.get() -> GOLDEN_TEXTURE;
            case Item costume when costume == ModItems.COLORFUL_TINT.get() -> COLORFUL_TEXTURE;
            default -> NORMAL_TEXTURE;
        };
    }

    @Override
    public ResourceLocation getAnimationResource(MadameButterflyEntity animatable) {
        return ANIMATIONS;
    }

    @Override
    public void setCustomAnimations(MadameButterflyEntity madame, long instanceId, AnimationState<MadameButterflyEntity> animationState) {
        GeoBone cello = getAnimationProcessor().getBone("violin");
        GeoBone bow = getAnimationProcessor().getBone("bow");

        cello.setHidden(!madame.isHoldingInstrument());
        bow.setHidden(!madame.isHoldingInstrument());

    }
}

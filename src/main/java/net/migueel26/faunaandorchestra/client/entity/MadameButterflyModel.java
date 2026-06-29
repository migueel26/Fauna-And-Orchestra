package net.migueel26.faunaandorchestra.client.entity;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.custom.MacawEntity;
import net.migueel26.faunaandorchestra.entity.custom.MadameButterflyEntity;
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
        if (butterfly.getCostume() == ModItems.SILVER_TINT.get()) {
            return TUXEDO_TEXTURE;
        } else if (butterfly.getCostume() == ModItems.GOLDEN_TINT.get()) {
            return GOLDEN_TEXTURE;
        } else if (butterfly.getCostume() == ModItems.COLORFUL_TINT.get()) {
            return COLORFUL_TEXTURE;
        } else {
            return NORMAL_TEXTURE;
        }
    }

    @Override
    public ResourceLocation getAnimationResource(MadameButterflyEntity animatable) {
        return ANIMATIONS;
    }

    @Override
    public void setCustomAnimations(MadameButterflyEntity madame, long instanceId, AnimationState<MadameButterflyEntity> animationState) {
        CoreGeoBone cello = getAnimationProcessor().getBone("violin");
        CoreGeoBone bow = getAnimationProcessor().getBone("bow");

        cello.setHidden(!madame.isHoldingInstrument());
        bow.setHidden(!madame.isHoldingInstrument());

    }
}

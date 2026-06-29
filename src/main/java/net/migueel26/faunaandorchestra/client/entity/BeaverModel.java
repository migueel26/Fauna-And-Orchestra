package net.migueel26.faunaandorchestra.client.entity;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.custom.BeaverEntity;
import net.migueel26.faunaandorchestra.entity.custom.MacawEntity;
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

public class BeaverModel extends GeoModel<BeaverEntity> {
    private static final ResourceLocation NORMAL_TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/entity/beaver.png");
    private static final ResourceLocation TUXEDO_TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/entity/beaver_tuxedo.png");
    private static final ResourceLocation WHITE_TUXEDO_TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/entity/beaver_white_tuxedo.png");
    private static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "animations/entity/beaver.animation.json");
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "geo/entity/beaver.geo.json");
    @Override
    public ResourceLocation getModelResource(BeaverEntity beaver) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(BeaverEntity beaver) {
        if (beaver.getCostume() == ModItems.TUXEDO.get()) {
            return TUXEDO_TEXTURE;
        } else if (beaver.getCostume() == ModItems.WHITE_TUXEDO.get()) {
            return WHITE_TUXEDO_TEXTURE;
        } else {
            return NORMAL_TEXTURE;
        }
    }

    @Override
    public ResourceLocation getAnimationResource(BeaverEntity animatable) {
        return ANIMATIONS;
    }


    @Override
    public void setCustomAnimations(BeaverEntity beaver, long instanceId, AnimationState<BeaverEntity> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("head");

        if (head != null && !beaver.isPlayingInstrument()) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

            if (beaver.isHoldingInstrument() || beaver.isBuilding()) {
                head.setRotX((entityData.headPitch()-90) * Mth.DEG_TO_RAD);
                head.setRotZ(-entityData.netHeadYaw() * Mth.DEG_TO_RAD);
            } else {
                head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
                head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
            }

        }

        CoreGeoBone saxophone = getAnimationProcessor().getBone("saxophone");

        getAnimationProcessor().getBone("top_hat").setHidden(beaver.getHat() != ModItems.TOP_HAT.get());
        getAnimationProcessor().getBone("right_monocle").setHidden(beaver.getHat() != ModItems.RIGHT_MONOCLE.get());
        getAnimationProcessor().getBone("left_monocle").setHidden(beaver.getHat() != ModItems.LEFT_MONOCLE.get());

        saxophone.setHidden(!beaver.isHoldingInstrument());
    }
}

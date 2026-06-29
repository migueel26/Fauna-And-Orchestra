package net.migueel26.faunaandorchestra.client.entity;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.custom.BeaverEntity;
import net.migueel26.faunaandorchestra.entity.custom.SeaLionEntity;
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

public class SeaLionModel extends GeoModel<SeaLionEntity> {
    private static final ResourceLocation NORMAL_TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/entity/sea_lion.png");
    private static final ResourceLocation TUXEDO_TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/entity/sea_lion_tuxedo.png");
    private static final ResourceLocation WHITE_TUXEDO_TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/entity/sea_lion_white_tuxedo.png");
    private static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "animations/entity/sea_lion.animation.json");
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "geo/entity/sea_lion.geo.json");
    @Override
    public ResourceLocation getModelResource(SeaLionEntity beaver) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(SeaLionEntity beaver) {
        if (beaver.getCostume() == ModItems.TUXEDO.get()) {
            return TUXEDO_TEXTURE;
        } else if (beaver.getCostume() == ModItems.WHITE_TUXEDO.get()) {
            return WHITE_TUXEDO_TEXTURE;
        } else {
            return NORMAL_TEXTURE;
        }
    }

    @Override
    public ResourceLocation getAnimationResource(SeaLionEntity animatable) {
        return ANIMATIONS;
    }


    @Override
    public void setCustomAnimations(SeaLionEntity seaLion, long instanceId, AnimationState<SeaLionEntity> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("head");

        if (head != null && !seaLion.isPlayingInstrument() && !animationState.isMoving()) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);

        }

        getAnimationProcessor().getBone("top_hat").setHidden(seaLion.getHat() != ModItems.TOP_HAT.get());
        getAnimationProcessor().getBone("right_monocle").setHidden(seaLion.getHat() != ModItems.RIGHT_MONOCLE.get());
        getAnimationProcessor().getBone("left_monocle").setHidden(seaLion.getHat() != ModItems.LEFT_MONOCLE.get());

        setHiddenInstrument(!seaLion.isHoldingInstrument());
    }

    public void setHiddenInstrument(boolean bool) {
        getAnimationProcessor().getBone("strings").setHidden(bool);
        getAnimationProcessor().getBone("right_drumstick").setHidden(bool);
        getAnimationProcessor().getBone("left_drumstick").setHidden(bool);
    }
}

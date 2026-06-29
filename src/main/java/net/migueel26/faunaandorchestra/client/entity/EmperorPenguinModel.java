package net.migueel26.faunaandorchestra.client.entity;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.custom.EmperorPenguinEntity;
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

public class EmperorPenguinModel extends GeoModel<EmperorPenguinEntity> {
    private static final ResourceLocation NORMAL_TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/entity/emperor_penguin.png");
    private static final ResourceLocation TUXEDO_TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/entity/emperor_penguin_tuxedo.png");
    private static final ResourceLocation WHITE_TUXEDO_TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/entity/emperor_penguin_white_tuxedo.png");
    private static final ResourceLocation SANTA_TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/entity/emperor_penguin_santa.png");
    private static final ResourceLocation BASEBALL_TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/entity/emperor_penguin_baseball.png");
    private static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "animations/entity/emperor_penguin.animation.json");
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "geo/entity/emperor_penguin.geo.json");

    @Override
    public ResourceLocation getModelResource(EmperorPenguinEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(EmperorPenguinEntity penguin) {
        if (penguin.getCostume() == ModItems.TUXEDO.get()) {
            return TUXEDO_TEXTURE;
        } else if (penguin.getCostume() == ModItems.WHITE_TUXEDO.get()) {
            return WHITE_TUXEDO_TEXTURE;
        } else if (penguin.getCostume() == ModItems.SANTA_COSTUME.get()) {
            return SANTA_TEXTURE;
        } else if (penguin.getCostume() == ModItems.BASEBALL_JACKET.get()) {
            return BASEBALL_TEXTURE;
        } else {
            return NORMAL_TEXTURE;
        }
    }

    @Override
    public ResourceLocation getAnimationResource(EmperorPenguinEntity animatable) {
        return ANIMATIONS;
    }

    @Override
    public void setCustomAnimations(EmperorPenguinEntity penguin, long instanceId, AnimationState<EmperorPenguinEntity> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("head");

        if (head != null && !penguin.isPlayingInstrument() && !animationState.isMoving() && !penguin.isBusy()) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
        }

        CoreGeoBone flute = getAnimationProcessor().getBone("long_flute");

        getAnimationProcessor().getBone("santa_hat").setHidden(penguin.getHat() != ModItems.SANTA_HAT.get());
        getAnimationProcessor().getBone("baseball_cap").setHidden(penguin.getHat() != ModItems.BASEBALL_CAP.get());

        flute.setHidden(!penguin.isHoldingInstrument());
    }
}

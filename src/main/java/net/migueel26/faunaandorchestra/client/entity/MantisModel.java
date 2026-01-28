package net.migueel26.faunaandorchestra.client.entity;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.custom.MantisEntity;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class MantisModel extends GeoModel<MantisEntity> {
    private static final ResourceLocation NORMAL_TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/entity/mantis.png");
    private static final ResourceLocation NORMAL_TUXEDO_TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/entity/mantis_tuxedo.png");
    private static final ResourceLocation NORMAL_ANIMATIONS = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "animations/entity/mantis.animation.json");
    private static final ResourceLocation NORMAL_MODEL = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "geo/entity/mantis.geo.json");
    private static final ResourceLocation ORCHID_TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/entity/orchid_mantis.png");
    private static final ResourceLocation ORCHID_TUXEDO_TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/entity/orchid_mantis_tuxedo.png");
    private static final ResourceLocation ORCHID_ANIMATIONS = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "animations/entity/orchid_mantis.animation.json");
    private static final ResourceLocation ORCHID_MODEL = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "geo/entity/orchid_mantis.geo.json");

    @Override
    public ResourceLocation getModelResource(MantisEntity mantis) {
        return switch (mantis.getVariant()) {
            case ORCHID -> ORCHID_MODEL;
            default -> NORMAL_MODEL;
        };
    }

    @Override
    public ResourceLocation getTextureResource(MantisEntity mantis) {
        Item costume = mantis.getCostume();
        if (costume == ModItems.TUXEDO.get()) {
            return switch (mantis.getVariant()) {
                case ORCHID -> ORCHID_TUXEDO_TEXTURE;
                default -> NORMAL_TUXEDO_TEXTURE;
            };
        } else {
            return switch (mantis.getVariant()) {
                case ORCHID -> ORCHID_TEXTURE;
                default -> NORMAL_TEXTURE;
            };
        }
    }

    @Override
    public ResourceLocation getAnimationResource(MantisEntity mantis) {
        return switch (mantis.getVariant()) {
            case ORCHID -> ORCHID_ANIMATIONS;
            default -> NORMAL_ANIMATIONS;
        };
    }

    @Override
    public void setCustomAnimations(MantisEntity mantis, long instanceId, AnimationState<MantisEntity> animationState) {
        GeoBone head = getAnimationProcessor().getBone("head");

        if (head != null && !mantis.isPlayingInstrument()) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
        }

        GeoBone left_pupil = getAnimationProcessor().getBone("left_pupil");
        GeoBone right_pupil = getAnimationProcessor().getBone("right_pupil");
        GeoBone violin = getAnimationProcessor().getBone("violin");
        GeoBone bow = getAnimationProcessor().getBone("bow");

        if (mantis.isAngry()) {
            left_pupil.setHidden(true);
            right_pupil.setHidden(true);
        } else {
            left_pupil.setHidden(false);
            right_pupil.setHidden(false);
        }

        if (mantis.isHoldingInstrument()) {
            violin.setHidden(false);
            bow.setHidden(false);
        } else {
            violin.setHidden(true);
            bow.setHidden(true);
        }

        getAnimationProcessor().getBone("fake_moustache").setHidden(mantis.getHat() != ModItems.FAKE_MOUSTACHE.get());
        getAnimationProcessor().getBone("rose").setHidden(mantis.getHat() != ModItems.ROSE.get());
    }
}

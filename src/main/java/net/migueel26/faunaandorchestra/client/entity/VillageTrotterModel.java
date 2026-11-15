package net.migueel26.faunaandorchestra.client.entity;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.custom.village_trotters.VillageTrotterEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class VillageTrotterModel extends GeoModel<VillageTrotterEntity> {
    private static final ResourceLocation MANTIS_TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/entity/mantis_white_tuxedo.png");
    private static final ResourceLocation MANTIS_ANIMATIONS = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "animations/entity/mantis.animation.json");
    private static final ResourceLocation MANTIS_MODEL = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "geo/entity/mantis.geo.json");
    private static final ResourceLocation RED_PANDA_TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/entity/red_panda_white_tuxedo.png");
    private static final ResourceLocation RED_PANDA_ANIMATIONS = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "animations/entity/red_panda.animation.json");
    private static final ResourceLocation RED_PANDA_MODEL = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "geo/entity/red_panda.geo.json");
    private static final ResourceLocation PENGUIN_TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/entity/penguin_white_tuxedo.png");
    private static final ResourceLocation PENGUIN_ANIMATIONS = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "animations/entity/penguin.animation.json");
    private static final ResourceLocation PENGUIN_MODEL = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "geo/entity/penguin.geo.json");
    private static final ResourceLocation MACAW_TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/entity/macaw_white_tuxedo.png");
    private static final ResourceLocation MACAW_ANIMATIONS = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "animations/entity/macaw.animation.json");
    private static final ResourceLocation MACAW_MODEL = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "geo/entity/macaw.geo.json");


    @Override
    public ResourceLocation getModelResource(VillageTrotterEntity animal) {
        return switch (animal.getVillageTrotterType()) {
            case MANTIS -> MANTIS_MODEL;
            case PENGUIN -> PENGUIN_MODEL;
            case RED_PANDA -> RED_PANDA_MODEL;
            case MACAW ->  MACAW_MODEL;
        };
    }

    @Override
    public ResourceLocation getTextureResource(VillageTrotterEntity animal) {
        return switch (animal.getVillageTrotterType()) {
            case MANTIS -> MANTIS_TEXTURE;
            case PENGUIN -> PENGUIN_TEXTURE;
            case RED_PANDA -> RED_PANDA_TEXTURE;
            case MACAW ->  MACAW_TEXTURE;
        };
    }

    @Override
    public ResourceLocation getAnimationResource(VillageTrotterEntity animal) {
        return switch (animal.getVillageTrotterType()) {
            case MANTIS -> MANTIS_ANIMATIONS;
            case PENGUIN -> PENGUIN_ANIMATIONS;
            case RED_PANDA -> RED_PANDA_ANIMATIONS;
            case MACAW ->  MACAW_ANIMATIONS;
        };
    }

    @Override
    public void setCustomAnimations(VillageTrotterEntity mantis, long instanceId, AnimationState<VillageTrotterEntity> animationState) {
        GeoBone head = getAnimationProcessor().getBone("head");

        if (head != null && !mantis.isQuestFinished()) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

            if (mantis.getVillageTrotterType() == VillageTrotterEntity.VillageTrotter.RED_PANDA) {
                head.setRotX((entityData.headPitch() - 80) * Mth.DEG_TO_RAD);
                head.setRotZ(-entityData.netHeadYaw() * Mth.DEG_TO_RAD);
            } else {
                head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
                head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
            }

        }
    }
}

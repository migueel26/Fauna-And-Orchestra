package net.migueel26.faunaandorchestra.client.entity;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.custom.AbstractCanonEntity;
import net.migueel26.faunaandorchestra.entity.custom.PlayerCanonEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class CanonEntityModel extends GeoModel<AbstractCanonEntity> {
    private static final ResourceLocation DEFAULT_TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/entity/anya_ghost.png");
    private static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "animations/entity/player_canon.animation.json");
    private static final ResourceLocation ANYA_MODEL = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "geo/entity/anya_ghost.geo.json");
    private static final ResourceLocation PLAYER_CANON_MODEL = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "geo/entity/player_canon.geo.json");
    @Override
    public ResourceLocation getModelResource(AbstractCanonEntity animatable) {
        return animatable instanceof PlayerCanonEntity ? PLAYER_CANON_MODEL : ANYA_MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(AbstractCanonEntity animatable) {
        return animatable.getSkin() == null ? DEFAULT_TEXTURE : animatable.getSkin();
    }

    @Override
    public ResourceLocation getAnimationResource(AbstractCanonEntity animatable) {
        return ANIMATIONS;
    }


    @Override
    public void setCustomAnimations(AbstractCanonEntity anya, long instanceId, AnimationState<AbstractCanonEntity> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("Head");
        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
        }

    }
}

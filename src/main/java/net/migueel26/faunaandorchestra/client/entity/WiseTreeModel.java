package net.migueel26.faunaandorchestra.client.entity;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.custom.Orion;
import net.migueel26.faunaandorchestra.entity.custom.WiseTree;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;

public class WiseTreeModel extends GeoModel<WiseTree> {
    private static final ResourceLocation WISE_TREE_TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/entity/wise_tree.png");
    private static final ResourceLocation WISE_TREE_SPROUT_TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/entity/wise_tree_sprout.png");
    private static final ResourceLocation WISE_TREE_ANIMATIONS = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "animations/entity/wise_tree.animation.json");
    private static final ResourceLocation WISE_TREE_SPROUT_ANIMATIONS = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "animations/entity/wise_tree_sprout.animation.json");
    private static final ResourceLocation WISE_TREE_MODEL = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "geo/entity/wise_tree.geo.json");
    private static final ResourceLocation WISE_TREE_SPROUT_MODEL = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "geo/entity/wise_tree_sprout.geo.json");
    private static final ResourceLocation WISE_TREE_YOUNG_MODEL = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "geo/entity/wise_tree_young.geo.json");
    @Override
    public ResourceLocation getModelResource(WiseTree animatable) {
        return switch (animatable.getLifeStage()) {
            case 1 -> WISE_TREE_SPROUT_MODEL;
            case 2 -> WISE_TREE_YOUNG_MODEL;
            default -> WISE_TREE_MODEL;
        };
    }

    @Override
    public ResourceLocation getTextureResource(WiseTree animatable) {
        return animatable.getLifeStage() == 1 ? WISE_TREE_SPROUT_TEXTURE : WISE_TREE_TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(WiseTree animatable) {
        return animatable.getLifeStage() == 1 ? WISE_TREE_SPROUT_ANIMATIONS : WISE_TREE_ANIMATIONS;
    }


    @Override
    public void setCustomAnimations(WiseTree wiseTree, long instanceId, AnimationState<WiseTree> animationState) {
        if (wiseTree.getLifeStage() == 3) {
            GeoBone fruit = getAnimationProcessor().getBone("fruit");
            fruit.setHidden(!animationState.isCurrentAnimation(WiseTree.DROP));
        }
    }
}

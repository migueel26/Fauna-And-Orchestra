package net.migueel26.faunaandorchestra.client.block;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.block.custom.MelomancyCauldronBlock;
import net.migueel26.faunaandorchestra.block.entity.MelomancyCauldronBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;

public class MelomancyCauldronModel extends GeoModel<MelomancyCauldronBlockEntity> {
    private static final ResourceLocation DEFAULT_TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/block/melomancy_cauldron.png");
    private static final ResourceLocation INK_TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/block/melomancy_cauldron_ink.png");
    private static final ResourceLocation DISCORD_TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/block/melomancy_cauldron_discord.png");
    private static final ResourceLocation OFFERING_TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/block/melomancy_cauldron_offering.png");
    private static final ResourceLocation HEARING_TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/block/melomancy_cauldron_hearing.png");
    private static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "animations/block/melomancy_cauldron.animation.json");
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "geo/block/melomancy_cauldron.geo.json");
    @Override
    public ResourceLocation getModelResource(MelomancyCauldronBlockEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(MelomancyCauldronBlockEntity animatable) {
        return switch (animatable.getMixResult()) {
            case String item when item.startsWith("discord") -> DISCORD_TEXTURE;
            case "musical_ink", "steelsonic" -> INK_TEXTURE;
            case "offering" -> OFFERING_TEXTURE;
            case "absolute_hearing" -> HEARING_TEXTURE;
            default -> DEFAULT_TEXTURE;
        };
    }

    @Override
    public ResourceLocation getAnimationResource(MelomancyCauldronBlockEntity animatable) {
        return ANIMATIONS;
    }

    @Override
    public void setCustomAnimations(MelomancyCauldronBlockEntity animatable, long instanceId, AnimationState<MelomancyCauldronBlockEntity> animationState) {
        int liquid = animatable.getBlockState().getValue(MelomancyCauldronBlock.LIQUID);
        GeoBone liquidBone = getAnimationProcessor().getBone("liquid");

        if (!animationState.isCurrentAnimation(MelomancyCauldronBlockEntity.EMPTY)) {
            if (liquid == 0) {
                liquidBone.setHidden(true);
                liquidBone.setPosY(-6.8f);
            } else if (liquid == 1) {
                liquidBone.setHidden(false);
                liquidBone.setPosY(-6.8f);
            } else if (liquid == 2) {
                liquidBone.setHidden(false);
                liquidBone.setPosY(-2.8f);
            } else {
                liquidBone.setHidden(false);
                liquidBone.setPosY(0f);
            }
        }

        super.setCustomAnimations(animatable, instanceId, animationState);
    }
}

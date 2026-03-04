package net.migueel26.faunaandorchestra.client.block;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.block.custom.MelomancyCauldronBlock;
import net.migueel26.faunaandorchestra.block.entity.MelomancyCauldronBlockEntity;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;

public class MelomancyCauldronModel extends GeoModel<MelomancyCauldronBlockEntity> {
    private static final ResourceLocation DEFAULT_TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/block/melomancy_cauldron.png");
    private static final ResourceLocation INK_TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/block/melomancy_cauldron_ink.png");
    private static final ResourceLocation DISCORD_TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/block/melomancy_cauldron_discord.png");
    private static final ResourceLocation OFFERING_TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/block/melomancy_cauldron_offering.png");
    private static final ResourceLocation HEARING_TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/block/melomancy_cauldron_hearing.png");
    private static final ResourceLocation SEED_TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/block/melomancy_cauldron_seed.png");
    private static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "animations/block/melomancy_cauldron.animation.json");
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "geo/block/melomancy_cauldron.geo.json");
    @Override
    public ResourceLocation getModelResource(MelomancyCauldronBlockEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(MelomancyCauldronBlockEntity animatable) {
        if (animatable.getVisualResult().isEmpty()) {
            return DEFAULT_TEXTURE;
        }

        Item resultItem = animatable.getVisualResult().getItem();

        if (resultItem == ModItems.DISCORD_ESSENCE.get() || resultItem == ModItems.RESURRECTION_SONG.get()) {
            return DISCORD_TEXTURE;
        }

        else if (resultItem == ModItems.MUSICAL_INK.get() ||
                resultItem == ModItems.STEELSONIC_INGOT.get() ||
                resultItem == ModItems.BOOGIE_BOMB.get() ||
                resultItem == ModItems.AMPLIFIER_CRYSTAL.get()) {
            return INK_TEXTURE;
        }

        else if (resultItem == ModItems.OFFERING.get()) {
            return OFFERING_TEXTURE;
        }
        else if (resultItem == Items.POTION) {
            return HEARING_TEXTURE;
        }
        else if (resultItem == ModItems.SINGING_SEED.get()) {
            return SEED_TEXTURE;
        }

        return DEFAULT_TEXTURE;
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

package net.migueel26.faunaandorchestra.client.block;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.block.ModBlocks;
import net.migueel26.faunaandorchestra.block.entity.ComposerGravestoneBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ComposerGravestoneModel extends GeoModel<ComposerGravestoneBlockEntity> {
    private static final ResourceLocation COMPOSER_TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/block/composer_gravestone.png");
    private static final ResourceLocation REGULAR_TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/block/composer_gravestone_clean.png");
    private static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "animations/block/composer_gravestone.animation.json");
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "geo/block/composer_gravestone.geo.json");
    @Override
    public ResourceLocation getModelResource(ComposerGravestoneBlockEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(ComposerGravestoneBlockEntity animatable) {
        return animatable.getBlockState().getBlock() == ModBlocks.GRAVESTONE.get() ? REGULAR_TEXTURE : COMPOSER_TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(ComposerGravestoneBlockEntity animatable) {
        return ANIMATIONS;
    }
}

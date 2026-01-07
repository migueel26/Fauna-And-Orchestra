package net.migueel26.faunaandorchestra.client.block;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.block.entity.ListenerContainerBlockEntity;
import net.migueel26.faunaandorchestra.block.entity.TheGreatHeadBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class TheGreatHeadModel extends GeoModel<TheGreatHeadBlockEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(FaunaAndOrchestra.MOD_ID, "textures/entity/the_great_composer.png");
    private static final ResourceLocation ANIMATIONS = new ResourceLocation(FaunaAndOrchestra.MOD_ID, "animations/block/the_great_head.animation.json");
    private static final ResourceLocation MODEL = new ResourceLocation(FaunaAndOrchestra.MOD_ID, "geo/block/the_great_head.geo.json");
    @Override
    public ResourceLocation getModelResource(TheGreatHeadBlockEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(TheGreatHeadBlockEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(TheGreatHeadBlockEntity animatable) {
        return ANIMATIONS;
    }
}

package net.migueel26.faunaandorchestra.client.item;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.item.custom.TheGreatHeadItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class TheGreatHeadItemModel extends GeoModel<TheGreatHeadItem> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(FaunaAndOrchestra.MOD_ID, "textures/entity/the_great_composer.png");
    private static final ResourceLocation ANIMATIONS = new ResourceLocation(FaunaAndOrchestra.MOD_ID, "animations/block/the_great_head.animation.json");
    private static final ResourceLocation MODEL = new ResourceLocation(FaunaAndOrchestra.MOD_ID, "geo/item/the_great_head.geo.json");
    @Override
    public ResourceLocation getModelResource(TheGreatHeadItem animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(TheGreatHeadItem animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(TheGreatHeadItem animatable) {
        return ANIMATIONS;
    }
}
package net.migueel26.faunaandorchestra.client.item;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.item.custom.MelomancyCauldronItem;
import net.migueel26.faunaandorchestra.item.custom.TheGreatHeadItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MelomancyCauldronItemModel extends GeoModel<MelomancyCauldronItem> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(FaunaAndOrchestra.MOD_ID, "textures/block/melomancy_cauldron.png");
    private static final ResourceLocation ANIMATIONS = new ResourceLocation(FaunaAndOrchestra.MOD_ID, "animations/block/melomancy_cauldron.animation.json");
    private static final ResourceLocation MODEL = new ResourceLocation(FaunaAndOrchestra.MOD_ID, "geo/item/melomancy_cauldron.geo.json");
    @Override
    public ResourceLocation getModelResource(MelomancyCauldronItem animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(MelomancyCauldronItem animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(MelomancyCauldronItem animatable) {
        return ANIMATIONS;
    }
}
package net.migueel26.faunaandorchestra.client.item;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.item.custom.MantisDaggerItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MantisDaggerItemModel extends GeoModel<MantisDaggerItem> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/item/mantis_dagger.png");
    private static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "animations/item/mantis_dagger.animation.json");
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "geo/item/mantis_dagger.geo.json");
    @Override
    public ResourceLocation getModelResource(MantisDaggerItem animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(MantisDaggerItem animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(MantisDaggerItem animatable) {
        return ANIMATIONS;
    }
}

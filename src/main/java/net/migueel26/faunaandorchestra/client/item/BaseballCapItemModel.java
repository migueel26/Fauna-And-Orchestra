package net.migueel26.faunaandorchestra.client.item;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.item.custom.clothing.CosmeticItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BaseballCapItemModel extends GeoModel<CosmeticItem> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/item/baseball_cap.png");
    private static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "animations/item/voice_vessel.animation.json");
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "geo/item/baseball_cap.geo.json");
    @Override
    public ResourceLocation getModelResource(CosmeticItem animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(CosmeticItem animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(CosmeticItem animatable) {
        return null;
    }
}

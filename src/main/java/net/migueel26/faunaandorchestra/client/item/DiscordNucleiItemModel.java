package net.migueel26.faunaandorchestra.client.item;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.item.custom.DiscordNucleiItem;
import net.migueel26.faunaandorchestra.item.custom.MelomancyCauldronItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class DiscordNucleiItemModel extends GeoModel<DiscordNucleiItem> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/block/discord_nuclei.png");
    private static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "animations/block/discord_nuclei.animation.json");
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "geo/block/discord_nuclei.geo.json");
    @Override
    public ResourceLocation getModelResource(DiscordNucleiItem animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(DiscordNucleiItem animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(DiscordNucleiItem animatable) {
        return ANIMATIONS;
    }
}
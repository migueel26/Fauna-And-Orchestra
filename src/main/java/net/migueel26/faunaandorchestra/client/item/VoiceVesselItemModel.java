package net.migueel26.faunaandorchestra.client.item;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.item.custom.MelomancyCauldronItem;
import net.migueel26.faunaandorchestra.item.custom.VoiceVesselItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class VoiceVesselItemModel extends GeoModel<VoiceVesselItem> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(FaunaAndOrchestra.MOD_ID, "textures/item/voice_vessel.png");
    private static final ResourceLocation ANIMATIONS = new ResourceLocation(FaunaAndOrchestra.MOD_ID, "animations/item/voice_vessel.animation.json");
    private static final ResourceLocation MODEL = new ResourceLocation(FaunaAndOrchestra.MOD_ID, "geo/item/voice_vessel.geo.json");
    @Override
    public ResourceLocation getModelResource(VoiceVesselItem animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(VoiceVesselItem animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(VoiceVesselItem animatable) {
        return ANIMATIONS;
    }
}

package net.migueel26.faunaandorchestra.client.item;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.item.custom.SoundSensorItem;
import net.migueel26.faunaandorchestra.item.custom.VoiceVesselItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SoundSensorItemModel extends GeoModel<SoundSensorItem> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/item/sound_sensor.png");
    private static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "animations/item/sound_sensor.animation.json");
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "geo/item/sound_sensor.geo.json");
    @Override
    public ResourceLocation getModelResource(SoundSensorItem animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(SoundSensorItem animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(SoundSensorItem animatable) {
        return ANIMATIONS;
    }
}

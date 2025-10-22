package net.migueel26.faunaandorchestra.client.block;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.block.entity.ListenerBlockEntity;
import net.migueel26.faunaandorchestra.block.entity.VoiceChamberBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class VoiceChamberModel extends GeoModel<VoiceChamberBlockEntity> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/block/voice_chamber.png");
    private static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "animations/block/voice_chamber.animation.json");
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "geo/block/voice_chamber.geo.json");
    @Override
    public ResourceLocation getModelResource(VoiceChamberBlockEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(VoiceChamberBlockEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(VoiceChamberBlockEntity animatable) {
        return ANIMATIONS;
    }
}

package net.migueel26.faunaandorchestra.client.block;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.block.entity.BambooTrapBlockEntity;
import net.migueel26.faunaandorchestra.block.entity.TheGreatHeadBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BambooTrapModel extends GeoModel<BambooTrapBlockEntity> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/block/bamboo_trap.png");
    private static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "animations/block/bamboo_trap.animation.json");
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "geo/block/bamboo_trap.geo.json");
    @Override
    public ResourceLocation getModelResource(BambooTrapBlockEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(BambooTrapBlockEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(BambooTrapBlockEntity animatable) {
        return ANIMATIONS;
    }
}

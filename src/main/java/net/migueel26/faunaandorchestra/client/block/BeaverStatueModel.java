package net.migueel26.faunaandorchestra.client.block;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.block.entity.BeaverStatueBlockEntity;
import net.migueel26.faunaandorchestra.block.entity.MotherStatueBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BeaverStatueModel extends GeoModel<BeaverStatueBlockEntity> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/block/beaver_statue.png");
    private static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "animations/block/beaver_statue.animation.json");
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "geo/block/beaver_statue.geo.json");
    @Override
    public ResourceLocation getModelResource(BeaverStatueBlockEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(BeaverStatueBlockEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(BeaverStatueBlockEntity animatable) {
        return ANIMATIONS;
    }
}

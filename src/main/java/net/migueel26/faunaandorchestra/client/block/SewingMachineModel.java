package net.migueel26.faunaandorchestra.client.block;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.block.entity.BeaverStatueBlockEntity;
import net.migueel26.faunaandorchestra.block.entity.SewingMachineBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SewingMachineModel extends GeoModel<SewingMachineBlockEntity> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/block/sewing_machine.png");
    private static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "animations/block/sewing_machine.animation.json");
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "geo/block/sewing_machine.geo.json");
    @Override
    public ResourceLocation getModelResource(SewingMachineBlockEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(SewingMachineBlockEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(SewingMachineBlockEntity animatable) {
        return ANIMATIONS;
    }
}

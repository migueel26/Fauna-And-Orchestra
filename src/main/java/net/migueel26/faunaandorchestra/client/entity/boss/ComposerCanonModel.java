package net.migueel26.faunaandorchestra.client.entity.boss;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.custom.boss.ComposerCanonEntity;
import net.migueel26.faunaandorchestra.entity.custom.boss.TheGreatComposer;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ComposerCanonModel extends GeoModel<ComposerCanonEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(FaunaAndOrchestra.MOD_ID, "textures/entity/the_great_composer_canon.png");
    private static final ResourceLocation ANIMATIONS = new ResourceLocation(FaunaAndOrchestra.MOD_ID, "animations/entity/the_great_composer_canon.animation.json");
    private static final ResourceLocation MODEL = new ResourceLocation(FaunaAndOrchestra.MOD_ID, "geo/entity/the_great_composer_canon.geo.json");

    @Override
    public ResourceLocation getModelResource(ComposerCanonEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(ComposerCanonEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(ComposerCanonEntity animatable) {
        return ANIMATIONS;
    }
}

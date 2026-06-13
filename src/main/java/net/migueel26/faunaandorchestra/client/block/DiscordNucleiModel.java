package net.migueel26.faunaandorchestra.client.block;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.block.custom.DiscordNucleiBlock;
import net.migueel26.faunaandorchestra.block.entity.DiscordNucleiBlockEntity;
import net.migueel26.faunaandorchestra.block.entity.ListenerContainerBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;

public class DiscordNucleiModel extends GeoModel<DiscordNucleiBlockEntity> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/block/discord_nuclei.png");
    private static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "animations/block/discord_nuclei.animation.json");
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "geo/block/discord_nuclei.geo.json");
    @Override
    public ResourceLocation getModelResource(DiscordNucleiBlockEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(DiscordNucleiBlockEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(DiscordNucleiBlockEntity animatable) {
        return ANIMATIONS;
    }

    @Override
    public void setCustomAnimations(DiscordNucleiBlockEntity animatable, long instanceId, AnimationState<DiscordNucleiBlockEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
        GeoBone cube1 = getAnimationProcessor().getBone("cube1");
        GeoBone cube2 = getAnimationProcessor().getBone("cube2");
        GeoBone cube3 = getAnimationProcessor().getBone("cube3");
        GeoBone cube4 = getAnimationProcessor().getBone("cube4");

        int essence = animatable.getEssence();

        cube3.setHidden(essence < 1);
        cube2.setHidden(essence < 7);
        cube4.setHidden(essence < 13);
        cube1.setHidden(essence < 20);
    }
}

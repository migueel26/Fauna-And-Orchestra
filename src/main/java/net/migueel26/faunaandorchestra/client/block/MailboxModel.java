package net.migueel26.faunaandorchestra.client.block;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.block.entity.BeaverStatueBlockEntity;
import net.migueel26.faunaandorchestra.block.entity.MailboxBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MailboxModel extends GeoModel<MailboxBlockEntity> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/block/mailbox.png");
    private static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "animations/block/mailbox.animation.json");
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "geo/block/mailbox.geo.json");
    @Override
    public ResourceLocation getModelResource(MailboxBlockEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(MailboxBlockEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(MailboxBlockEntity animatable) {
        return ANIMATIONS;
    }
}

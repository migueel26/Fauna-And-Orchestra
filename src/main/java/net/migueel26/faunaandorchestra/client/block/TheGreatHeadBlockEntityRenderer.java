package net.migueel26.faunaandorchestra.client.block;

import net.migueel26.faunaandorchestra.block.entity.ListenerBlockEntity;
import net.migueel26.faunaandorchestra.block.entity.TheGreatHeadBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class TheGreatHeadBlockEntityRenderer extends GeoBlockRenderer<TheGreatHeadBlockEntity> {
    public TheGreatHeadBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(new TheGreatHeadModel());
    }
}

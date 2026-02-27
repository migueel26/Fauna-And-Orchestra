package net.migueel26.faunaandorchestra.client.block;

import net.migueel26.faunaandorchestra.block.entity.BambooTrapBlockEntity;
import net.migueel26.faunaandorchestra.block.entity.TheGreatHeadBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class BambooTrapBlockEntityRenderer extends GeoBlockRenderer<BambooTrapBlockEntity> {
    public BambooTrapBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(new BambooTrapModel());
    }
}

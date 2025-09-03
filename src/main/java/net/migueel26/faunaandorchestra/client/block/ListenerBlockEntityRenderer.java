package net.migueel26.faunaandorchestra.client.block;

import net.migueel26.faunaandorchestra.block.entity.ListenerBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class ListenerBlockEntityRenderer extends GeoBlockRenderer<ListenerBlockEntity> {
    public ListenerBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(new ListenerModel());
    }
}

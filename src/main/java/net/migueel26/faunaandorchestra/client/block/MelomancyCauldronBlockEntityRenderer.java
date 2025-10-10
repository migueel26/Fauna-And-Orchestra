package net.migueel26.faunaandorchestra.client.block;

import net.migueel26.faunaandorchestra.block.entity.MelomancyCauldronBlockEntity;
import net.migueel26.faunaandorchestra.block.entity.TheGreatHeadBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class MelomancyCauldronBlockEntityRenderer extends GeoBlockRenderer<MelomancyCauldronBlockEntity> {
    public MelomancyCauldronBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(new MelomancyCauldronModel());
    }
}

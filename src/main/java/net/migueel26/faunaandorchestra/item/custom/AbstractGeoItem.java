package net.migueel26.faunaandorchestra.item.custom;

import net.migueel26.faunaandorchestra.client.item.MelomancyCauldronItemRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

import java.util.function.Consumer;

public abstract class AbstractGeoItem extends Item implements GeoItem {
    public AbstractGeoItem(Properties properties) {
        super(properties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private GeoItemRenderer<?> renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null) {
                    this.renderer = ModItemRenderers.getRenderer(AbstractGeoItem.this);
                }
                return this.renderer;
            }
        });
    }
}

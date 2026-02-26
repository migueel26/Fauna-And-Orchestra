package net.migueel26.faunaandorchestra.item.custom;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.Item;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class AbstractGeoItem extends Item implements GeoItem {
    private final Supplier<? extends GeoItemRenderer<?>> rendererFactory;
    public AbstractGeoItem(Properties properties, Supplier<? extends GeoItemRenderer<?>> renderer) {
        super(properties);
        this.rendererFactory = renderer;
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private GeoItemRenderer<?> renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (renderer == null) {
                    renderer = rendererFactory.get();
                }
                return renderer;
            }
        });
    }
}

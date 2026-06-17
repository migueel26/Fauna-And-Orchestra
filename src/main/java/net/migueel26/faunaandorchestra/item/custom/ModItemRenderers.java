package net.migueel26.faunaandorchestra.item.custom;

import net.minecraft.world.item.Item;
import software.bernie.geckolib.renderer.GeoItemRenderer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ModItemRenderers {
    private static final Map<Item, Supplier<GeoItemRenderer<?>>> RENDERERS = new HashMap<>();

    public static void register(Item item, Supplier<GeoItemRenderer<?>> rendererFactory) {
        RENDERERS.put(item, rendererFactory);
    }

    public static GeoItemRenderer<?> getRenderer(Item item) {
        Supplier<GeoItemRenderer<?>> factory = RENDERERS.get(item);
        if (factory == null) {
            throw new IllegalStateException("Couldn't find a renderer for: " + item);
        }
        return factory.get();
    }
}

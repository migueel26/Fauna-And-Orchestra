package net.migueel26.faunaandorchestra.item.custom.clothing;

import net.migueel26.faunaandorchestra.client.item.SantaHatItemRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class CosmeticItem extends Item implements GeoItem {
    private final Supplier<? extends GeoItemRenderer<?>> rendererFactory;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public CosmeticItem(Properties properties, Supplier<? extends GeoItemRenderer<?>> renderer) {
        super(properties);
        this.rendererFactory = renderer;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

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

    @Override
    public boolean canEquip(ItemStack stack, EquipmentSlot armorType, LivingEntity entity) {
        if (armorType == EquipmentSlot.HEAD) return true;
        return super.canEquip(stack, armorType, entity);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}

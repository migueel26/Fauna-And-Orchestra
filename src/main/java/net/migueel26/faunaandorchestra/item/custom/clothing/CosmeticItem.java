package net.migueel26.faunaandorchestra.item.custom.clothing;

import net.migueel26.faunaandorchestra.item.custom.AbstractGeoItem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class CosmeticItem extends AbstractGeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public CosmeticItem(Properties properties) {
        super(properties);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public boolean canEquip(ItemStack stack, EquipmentSlot armorType, Entity entity) {
        if (armorType == EquipmentSlot.HEAD) return true;
        return super.canEquip(stack, armorType, entity);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}

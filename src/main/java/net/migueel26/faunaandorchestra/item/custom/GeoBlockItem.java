package net.migueel26.faunaandorchestra.item.custom;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;

public class GeoBlockItem extends BlockItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final boolean tooltip;

    public GeoBlockItem(Block block, Properties settings) {
        super(block, settings);
        this.tooltip = false;
    }

    public GeoBlockItem(Block block, boolean tooltip, Properties settings) {
        super(block, settings);
        this.tooltip = tooltip;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, this::predicate));
    }

    private <T extends GeoItem> PlayState predicate(AnimationState<T> state) {
        return PlayState.STOP;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private GeoItemRenderer<?> renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null) {
                    this.renderer = ModItemRenderers.getRenderer(GeoBlockItem.this);
                }
                return this.renderer;
            }
        });
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (tooltip) {
            if (Screen.hasShiftDown()) {
                tooltipComponents.add(Component.translatable(stack.getDescriptionId() + ".tooltip"));
            } else {
                tooltipComponents.add(Component.translatable("tooltip.faunaandorchestra.shift"));
            }
        }
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }
}

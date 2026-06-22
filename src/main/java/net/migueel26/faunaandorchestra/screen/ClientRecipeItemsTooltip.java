package net.migueel26.faunaandorchestra.screen;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ClientRecipeItemsTooltip implements ClientTooltipComponent {
    private final List<ItemStack> items;

    public ClientRecipeItemsTooltip(RecipeItemsTooltip tooltip) {
        this.items = tooltip.items();
    }

    @Override
    public int getHeight() {
        return 20;
    }

    @Override
    public int getWidth(Font font) {
        return this.items.size() * 18 + 2;
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
        for (int i = 0; i < this.items.size(); i++) {
            ItemStack stack = this.items.get(i);
            int itemX = x + (i * 18);
            int itemY = y;

            guiGraphics.renderItem(stack, itemX, itemY);
            guiGraphics.renderItemDecorations(font, stack, itemX, itemY);
        }
    }

    public record RecipeItemsTooltip(List<ItemStack> items) implements TooltipComponent {
    }
}

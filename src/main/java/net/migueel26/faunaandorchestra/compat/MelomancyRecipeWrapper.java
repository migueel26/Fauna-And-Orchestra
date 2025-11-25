package net.migueel26.faunaandorchestra.compat;

import net.minecraft.world.item.ItemStack;

public class MelomancyRecipeWrapper {
    private final ItemStack result;
    private final String description;

    public MelomancyRecipeWrapper(ItemStack result, String description) {
        this.result = result;
        this.description = description;
    }

    public ItemStack getResult() {
        return result;
    }

    public String getDescription() {
        return description;
    }
}

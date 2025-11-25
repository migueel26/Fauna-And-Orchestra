package net.migueel26.faunaandorchestra.compat;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

public class MelomancyRecipeCategory implements IRecipeCategory<MelomancyRecipeWrapper> {
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "melomancy_display");
    public static final RecipeType<MelomancyRecipeWrapper> TYPE =
            new RecipeType<>(UID, MelomancyRecipeWrapper.class);
    private final IDrawable background;
    private final IDrawable icon;
    public MelomancyRecipeCategory(IGuiHelper helper) {
        this.background = helper.createBlankDrawable(150, 60);
        this.icon = helper.createDrawableIngredient(
                VanillaTypes.ITEM_STACK,
                new ItemStack(Items.BOOK));
    }
    @Override
    public RecipeType<MelomancyRecipeWrapper> getRecipeType() {
        return TYPE;
    }

    @Override
    public void draw(MelomancyRecipeWrapper recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        background.draw(guiGraphics);
        guiGraphics.drawWordWrap(Minecraft.getInstance().font,
                FormattedText.of(recipe.getDescription()),
                10, 10,
                100, 0xffffff);
    }

    @Override
    public Component getTitle() {
        return Component.translatable("symphonia.entry.melomancy_cauldron");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, MelomancyRecipeWrapper recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.OUTPUT, 104, 34).addItemStack(recipe.getResult());
    }
}

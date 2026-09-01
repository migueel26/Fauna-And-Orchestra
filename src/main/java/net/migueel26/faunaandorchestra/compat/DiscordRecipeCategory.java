package net.migueel26.faunaandorchestra.compat;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.recipe.DiscordRecipe;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class DiscordRecipeCategory implements IRecipeCategory<DiscordRecipe> {
    public final static ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "discord");
    public static final RecipeType<DiscordRecipe> RECIPE_TYPE = new RecipeType<>(DiscordRecipeCategory.UID, DiscordRecipe.class);
    public final static ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/gui/block/discord_gui_jei.png");
    protected final IDrawable background;
    protected final IDrawable icon;

    public DiscordRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 145, 126);
        this.icon = helper.createDrawableItemStack(new ItemStack(ModItems.DISCORD_NUCLEI_ITEM.get()));
    }

    @Override
    public RecipeType<DiscordRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.faunaandorchestra.discord");
    }

    @Override
    public int getWidth() {
        return 145;
    }

    @Override
    public int getHeight() {
        return 126;
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, DiscordRecipe recipe, IFocusGroup focuses) {
        builder.setShapeless();

        // OUTPUT
        builder.addOutputSlot(107, 31).addItemStack(recipe.output());

        // INPUT
        builder.addInputSlot(45, 31).addIngredients(recipe.ingredient().ingredient());
    }

    @Override
    public void draw(DiscordRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        background.draw(guiGraphics);
    }

}

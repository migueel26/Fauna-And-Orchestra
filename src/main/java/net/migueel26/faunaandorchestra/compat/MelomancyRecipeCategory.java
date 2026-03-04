package net.migueel26.faunaandorchestra.compat;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.recipe.MelomancyRecipe;
import net.migueel26.faunaandorchestra.recipe.SizedIngredient;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MelomancyRecipeCategory implements IRecipeCategory<MelomancyRecipe> {
    public final static ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "melomancy");
    public static final RecipeType<MelomancyRecipe> RECIPE_TYPE = new RecipeType<>(MelomancyRecipeCategory.UID, MelomancyRecipe.class);
    public final static ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/gui/block/melomancy_gui_jei.png");
    protected final IDrawable background;
    protected final IDrawable icon;

    public MelomancyRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 145, 126);
        this.icon = helper.createDrawableItemStack(new ItemStack(ModItems.MELOMANCY_CAULDRON_ITEM.get()));
    }

    @Override
    public RecipeType<MelomancyRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.faunaandorchestra.melomancy");
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
    public void setRecipe(IRecipeLayoutBuilder builder, MelomancyRecipe recipe, IFocusGroup focuses) {
        builder.setShapeless();

        // OUTPUT
        builder.addOutputSlot(108, 50).addItemStack(recipe.output());

        // INPUT
        List<SizedIngredient> ingredients = recipe.ingredients();
        int startX = 29;
        int startY = 34;

        for (int i = 0; i < ingredients.size(); i++) {
            SizedIngredient sizedIngredient = ingredients.get(i);

            int x = startX + (i % 3) * 18;
            int y = startY + (i / 3) * 18;

            List<ItemStack> stacks = sizedIngredient.getItems().toList();

            builder.addInputSlot(x, y).addIngredients(VanillaTypes.ITEM_STACK, stacks);
        }

        // LIQUID MUSIC
        builder.addInputSlot(47, 88)
                .addItemStack(new ItemStack(ModItems.MUSIC_BOTTLE.get(), 3)).addRichTooltipCallback((recipeSlotView, tooltip) -> {
                    tooltip.add(Component.translatable("jei.faunaandorchestra.requires_music_bottle"));
                });

        // CATALYST
        builder.addInputSlot(98, 30)
                .addItemStack(recipe.catalyst()).addRichTooltipCallback(((recipeSlotView, tooltip) ->
                        tooltip.add(getCatalystComponent(recipe))));

        // MOUSE
    }

    @Override
    public void draw(MelomancyRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        background.draw(guiGraphics);
        int xThreshold = recipe.catalyst().isEmpty() ? 98 : 114;
        if (mouseX >= xThreshold && mouseX <= 133 && mouseY >= 30 && mouseY <= 45) {
            Font font = Minecraft.getInstance().font;
            Component text = getCatalystComponent(recipe);
            List<FormattedCharSequence> wrappedTooltip = font.split(text, 150);

            guiGraphics.renderTooltip(font, wrappedTooltip, (int) mouseX, (int) mouseY);
        }
    }

    private static @NotNull MutableComponent getCatalystComponent(MelomancyRecipe recipe) {
        MutableComponent result;
        if (recipe.catalyst().isEmpty()) {
            result = Component.translatable("jei.faunaandorchestra.no_requires_catalyst");
        } else {
            result = Component.translatable("jei.faunaandorchestra.requires_catalyst")
                    .append("§d" + recipe.catalyst().getHoverName().getString());
        }
        return result;
    }
}

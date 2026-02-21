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
import net.migueel26.faunaandorchestra.block.ModBlocks;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.recipe.NaturalRecipe;
import net.migueel26.faunaandorchestra.recipe.SizedIngredient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NaturalRecipeCategory implements IRecipeCategory<NaturalRecipe> {
    public final static ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "natural_recipe");
    public static final RecipeType<NaturalRecipe> RECIPE_TYPE = new RecipeType<>(NaturalRecipeCategory.UID, NaturalRecipe.class);
    public final static ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "textures/gui/block/natural_recipe_gui_jei.png");
    protected final IDrawable background;
    protected final IDrawable icon;

    public NaturalRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 145, 149);
        this.icon = helper.createDrawableItemStack(new ItemStack(ModBlocks.JAR_RACK.get()));
    }

    @Override
    public RecipeType<NaturalRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.faunaandorchestra.natural_recipe");
    }

    @Override
    public int getWidth() {
        return 145;
    }

    @Override
    public int getHeight() {
        return 144;
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, NaturalRecipe recipe, IFocusGroup focuses) {
        builder.setShapeless();

        // OUTPUT
        builder.addOutputSlot(109, 58).addItemStack(recipe.output());

        // INPUT
        List<SizedIngredient> ingredients = recipe.ingredients();
        int startX = 35;
        int startY = 39;

        for (int i = 0; i < ingredients.size(); i++) {
            SizedIngredient sizedIngredient = ingredients.get(i);

            int x = startX + (i % 2) * 18;
            int y = startY + (i / 2) * 18;

            List<ItemStack> stacks = sizedIngredient.getItems().toList();

            builder.addInputSlot(x, y).addIngredients(VanillaTypes.ITEM_STACK, stacks);
        }

        // FUEL
        List<ItemStack> fuels = recipe.fuel().stream()
                .map(blockHolder -> {
                    Block block = blockHolder.value();

                    if (block == Blocks.FIRE) {
                        return new ItemStack(ModItems.FIRE_ICON.asItem());
                    } else if (block == Blocks.SOUL_FIRE) {
                        return new ItemStack(ModItems.SOUL_FIRE_ICON.asItem());
                    } else if (block == Blocks.WATER) {
                        return new ItemStack(ModItems.WATER_ICON.asItem());
                    } else if (block.asItem() == Items.AIR) {
                        return new ItemStack(Items.BARRIER);
                    }

                    return new ItemStack(block);
                })
                .toList();

        builder.addInputSlot(45, 109)
                .addIngredients(VanillaTypes.ITEM_STACK, fuels).addRichTooltipCallback((recipeSlotView, tooltip) -> {
                    tooltip.add(Component.translatable("jei.faunaandorchestra.requires_below"));
                });

    }

    @Override
    public void draw(NaturalRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        background.draw(guiGraphics);
    }
}

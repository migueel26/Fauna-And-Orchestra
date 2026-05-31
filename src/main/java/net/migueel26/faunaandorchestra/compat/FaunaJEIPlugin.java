package net.migueel26.faunaandorchestra.compat;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.block.ModBlocks;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.migueel26.faunaandorchestra.recipe.MelomancyRecipe;
import net.migueel26.faunaandorchestra.recipe.ModRecipes;
import net.migueel26.faunaandorchestra.recipe.NaturalRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;
@JeiPlugin
public class FaunaJEIPlugin implements IModPlugin {
    private static IJeiRuntime jeiRuntime;
    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "jei_plugin");
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime runtime) {
        jeiRuntime = runtime;
    }

    public static IJeiRuntime getRuntime() {
        return jeiRuntime;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new NaturalRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new MelomancyRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        // Natural Recipes
        List<NaturalRecipe> naturalRecipes = recipeManager.getAllRecipesFor(ModRecipes.NATURAL_TYPE.get())
                .stream()
                .map(RecipeHolder::value)
                .toList();

        registration.addRecipes(NaturalRecipeCategory.RECIPE_TYPE, naturalRecipes);

        // Melomancy Recipes
        List<MelomancyRecipe> melomancyRecipes = recipeManager.getAllRecipesFor(ModRecipes.MELOMANCY_TYPE.get())
                .stream()
                .map(RecipeHolder::value)
                .toList();

        registration.addRecipes(MelomancyRecipeCategory.RECIPE_TYPE, melomancyRecipes);

        // Melomancy Info
        registration.addIngredientInfo(ModItems.MUSIC_BOTTLE, symphoniaComponent("music_bottle"));
        registration.addIngredientInfo(ModItems.DISCORD_ESSENCE, symphoniaComponent("discord_essence"));
        //registration.addIngredientInfo(ModItems.AMPLIFIER_CRYSTAL, melomancyComponent("amplifier_crystal"));
        //registration.addIngredientInfo(ModItems.OFFERING, melomancyComponent("offering"));
        //registration.addIngredientInfo(ModItems.STEELSONIC_INGOT, melomancyComponent("steelsonic_ingot"));
        //registration.addIngredientInfo(ModItems.MUSICAL_INK, melomancyComponent("musical_ink"));
        //registration.addIngredientInfo(ModItems.SINGING_SEED, melomancyComponent("singing_seed"));
        //registration.addIngredientInfo(ModItems.BOOGIE_BOMB, melomancyComponent("boogie_bomb"));
        registration.addIngredientInfo(ModItems.RESURRECTION_SONG, melomancyComponent("resurrection_song"));

        registration.addIngredientInfo(ModItems.TRANSMUTED_VOICE, symphoniaComponent("transmuted_voice"));
        registration.addIngredientInfo(ModItems.DISCORD_BOMB, symphoniaComponent("discord_bomb"));
        registration.addIngredientInfo(ModBlocks.DISCORDED_FLOWER.asItem(), symphoniaComponent("discorded_flower"));

        registration.addIngredientInfo(ModItems.DISCORD_NUCLEI_ITEM, symphoniaDefaultText());
        //registration.addIngredientInfo(ModItems.WANDERING_NOTE, symphoniaDefaultText());
        registration.addIngredientInfo(ModItems.FRUIT_OF_LIFE, symphoniaDefaultText());
        registration.addIngredientInfo(ModItems.PETALS_OF_DEATH, symphoniaDefaultText());
        registration.addIngredientInfo(ModItems.GLOVE, symphoniaDefaultText());
        registration.addIngredientInfo(ModItems.GINKGO_BILOBA, symphoniaDefaultText());

    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.JAR_RACK.get()), NaturalRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModItems.MELOMANCY_CAULDRON_ITEM.get()), MelomancyRecipeCategory.RECIPE_TYPE);
    }

    public static Component melomancyComponent(String item) {
        return Component.translatable("jei.faunaandorchestra.melomancy.info")
                .append(Component.literal("\n\n")
                .append(Component.translatable("jei.faunaandorchestra." + item))
                .append(Component.literal("\n\n"))
                .append(Component.translatable("jei.faunaandorchestra.symphonia")));
    }

    public static Component symphoniaComponent(String item) {
        return Component.translatable("jei.faunaandorchestra."+item)
                .append(Component.literal("\n\n"))
                .append(Component.translatable("jei.faunaandorchestra.symphonia"));
    }

    public static Component symphoniaDefaultText() {
        return Component.translatable("jei.faunaandorchestra.symphonia");
    }
}

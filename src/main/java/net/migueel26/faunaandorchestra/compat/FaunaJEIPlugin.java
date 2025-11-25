package net.migueel26.faunaandorchestra.compat;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.block.ModBlocks;
import net.migueel26.faunaandorchestra.item.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;
@JeiPlugin
public class FaunaJEIPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {

    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        //TODO: IMPROVE
        registration.addIngredientInfo(ModItems.MUSIC_BOTTLE, symphoniaComponent("music_bottle"));
        registration.addIngredientInfo(ModItems.DISCORD_ESSENCE, symphoniaComponent("discord_essence"));
        registration.addIngredientInfo(ModItems.AMPLIFIER_CRYSTAL, melomancyComponent("amplifier_crystal"));
        registration.addIngredientInfo(ModItems.OFFERING, melomancyComponent("offering"));
        registration.addIngredientInfo(ModItems.STEELSONIC_INGOT, melomancyComponent("steelsonic_ingot"));
        registration.addIngredientInfo(ModItems.MUSICAL_INK, melomancyComponent("musical_ink"));
        registration.addIngredientInfo(ModItems.SINGING_SEED, melomancyComponent("singing_seed"));
        registration.addIngredientInfo(ModItems.BOOGIE_BOMB, melomancyComponent("boogie_bomb"));
        registration.addIngredientInfo(ModItems.RESURRECTION_SONG, melomancyComponent("resurrection_song"));

        registration.addIngredientInfo(ModItems.TRANSMUTED_VOICE, symphoniaComponent("transmuted_voice"));
        registration.addIngredientInfo(ModItems.DISCORD_BOMB, symphoniaComponent("discord_bomb"));
        registration.addIngredientInfo(ModBlocks.DISCORDED_FLOWER.asItem(), symphoniaComponent("discorded_flower"));

        registration.addIngredientInfo(ModItems.DISCORD_NUCLEI_ITEM, symphoniaDefaultText());
        registration.addIngredientInfo(ModItems.WANDERING_NOTE, symphoniaDefaultText());
        registration.addIngredientInfo(ModItems.FRUIT_OF_LIFE, symphoniaDefaultText());
        registration.addIngredientInfo(ModItems.PETALS_OF_DEATH, symphoniaDefaultText());
        registration.addIngredientInfo(ModItems.GLOVE, symphoniaDefaultText());
        registration.addIngredientInfo(ModItems.GINKGO_BILOBA, symphoniaDefaultText());
    }

    public static Component melomancyComponent(String item) {
        return Component.translatable("jei.faunaandorchestra.melomancy")
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

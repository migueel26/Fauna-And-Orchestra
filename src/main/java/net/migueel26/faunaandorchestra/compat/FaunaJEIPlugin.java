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
        registration.addIngredientInfo(ModItems.MUSIC_BOTTLE.get(), symphoniaComponent("music_bottle"));
        registration.addIngredientInfo(ModItems.DISCORD_ESSENCE.get(), symphoniaComponent("discord_essence"));
        registration.addIngredientInfo(ModItems.AMPLIFIER_CRYSTAL.get(), melomancyComponent("amplifier_crystal"));
        registration.addIngredientInfo(ModItems.OFFERING.get(), melomancyComponent("offering"));
        registration.addIngredientInfo(ModItems.STEELSONIC_INGOT.get(), melomancyComponent("steelsonic_ingot"));
        registration.addIngredientInfo(ModItems.MUSICAL_INK.get(), melomancyComponent("musical_ink"));
        registration.addIngredientInfo(ModItems.SINGING_SEED.get(), melomancyComponent("singing_seed"));
        registration.addIngredientInfo(ModItems.BOOGIE_BOMB.get(), melomancyComponent("boogie_bomb"));
        registration.addIngredientInfo(ModItems.RESURRECTION_SONG.get(), melomancyComponent("resurrection_song"));

        registration.addIngredientInfo(ModItems.TRANSMUTED_VOICE.get(), symphoniaComponent("transmuted_voice"));
        registration.addIngredientInfo(ModItems.DISCORD_BOMB.get(), symphoniaComponent("discord_bomb"));
        registration.addIngredientInfo(ModBlocks.DISCORDED_FLOWER.get().asItem(), symphoniaComponent("discorded_flower"));

        registration.addIngredientInfo(ModItems.DISCORD_NUCLEI_ITEM.get(), symphoniaDefaultText());
        registration.addIngredientInfo(ModItems.WANDERING_NOTE.get(), symphoniaDefaultText());
        registration.addIngredientInfo(ModItems.FRUIT_OF_LIFE.get(), symphoniaDefaultText());
        registration.addIngredientInfo(ModItems.PETALS_OF_DEATH.get(), symphoniaDefaultText());
        registration.addIngredientInfo(ModItems.GLOVE.get(), symphoniaDefaultText());
        registration.addIngredientInfo(ModItems.GINKGO_BILOBA.get(), symphoniaDefaultText());
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

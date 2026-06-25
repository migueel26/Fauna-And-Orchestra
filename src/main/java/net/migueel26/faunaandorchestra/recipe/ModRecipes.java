package net.migueel26.faunaandorchestra.recipe;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, FaunaAndOrchestra.MOD_ID);

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, FaunaAndOrchestra.MOD_ID);

    // SERIALIZERS
    public static final Supplier<RecipeSerializer<NaturalRecipe>> NATURAL_SERIALIZER =
            SERIALIZERS.register("natural_recipe", NaturalRecipe.Serializer::new);
    public static final Supplier<RecipeSerializer<MelomancyRecipe>> MELOMANCY_SERIALIZER =
            SERIALIZERS.register("melomancy", MelomancyRecipe.Serializer::new);
    public static final Supplier<RecipeSerializer<SewingRecipe>> SEWING_SERIALIZER =
            SERIALIZERS.register("sewing", SewingRecipe.Serializer::new);

    // RECIPE TYPES
    public static final Supplier<RecipeType<NaturalRecipe>> NATURAL_TYPE =
            RECIPE_TYPES.register("natural_recipe", () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return "natural_recipe";
                }
            });
    public static final Supplier<RecipeType<MelomancyRecipe>> MELOMANCY_TYPE =
            RECIPE_TYPES.register("melomancy", () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return "melomancy";
                }
            });
    public static final Supplier<RecipeType<SewingRecipe>> SEWING_TYPE =
            RECIPE_TYPES.register("sewing", () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return "sewing";
                }
            });

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
        RECIPE_TYPES.register(eventBus);
    }
}

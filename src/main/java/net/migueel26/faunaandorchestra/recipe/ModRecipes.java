package net.migueel26.faunaandorchestra.recipe;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, FaunaAndOrchestra.MOD_ID);

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, FaunaAndOrchestra.MOD_ID);

    // SERIALIZERS
    public static final Supplier<RecipeSerializer<NaturalRecipe>> NATURAL_SERIALIZER =
            SERIALIZERS.register("natural_crafting", NaturalRecipe.Serializer::new);

    // RECIPE TYPES
    public static final Supplier<RecipeType<NaturalRecipe>> NATURAL_TYPE =
            RECIPE_TYPES.register("natural_crafting", () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return "natural_crafting";
                }
            });

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
        RECIPE_TYPES.register(eventBus);
    }
}

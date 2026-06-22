package net.migueel26.faunaandorchestra.recipe;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModIngredientTypes {
    public static final DeferredRegister<IngredientType<?>> INGREDIENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.INGREDIENT_TYPES, FaunaAndOrchestra.MOD_ID);

    public static final Supplier<IngredientType<SizedIngredient>> SIZED =
            INGREDIENT_TYPES.register("sized", () -> new IngredientType<>(
                    SizedIngredient.CODEC,
                    SizedIngredient.STREAM_CODEC
            ));

    public static void register(IEventBus eventBus) {
        INGREDIENT_TYPES.register(eventBus);
    }
}
package net.migueel26.faunaandorchestra.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;

public record NaturalRecipe(List<SizedIngredient> ingredients, ItemStack output, Block fuel, int time) implements Recipe<NaturalRecipe.RecipeInput> {
    @Override
    public boolean matches(RecipeInput input, Level level) {
        if (input.fuel() != this.fuel) {
            return false;
        }

        List<ItemStack> inputs = new ArrayList<>(input.items());
        inputs.removeIf(ItemStack::isEmpty);

        if (inputs.size() != this.ingredients.size()) return false;

        for (SizedIngredient required : this.ingredients) {
            int amountNeeded = required.amount(); // Cuánto pide la receta (ej: 3)
            int amountFound = 0; // Cuánto hemos encontrado sumando slots

            for (ItemStack testStack : inputs) {
                if (required.ingredient().test(testStack)) {
                    int take = Math.min(amountNeeded - amountFound, testStack.getCount());

                    testStack.shrink(take);
                    amountFound += take;

                    if (amountFound >= amountNeeded) break;
                }
            }

            if (amountFound < amountNeeded) return false;
        }

        return true;
    }

    @Override
    public ItemStack assemble(RecipeInput recipeInput, HolderLookup.Provider provider) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return output;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.NATURAL_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.NATURAL_TYPE.get();
    }

    public record RecipeInput(List<ItemStack> items, Block fuel) implements net.minecraft.world.item.crafting.RecipeInput {
        @Override
        public ItemStack getItem(int index) {
            return items.get(index);
        }

        @Override
        public int size() {
            return items.size();
        }
    }

    public static class Serializer implements RecipeSerializer<NaturalRecipe> {
        public static final MapCodec<NaturalRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                SizedIngredient.CODEC.codec().listOf().fieldOf("ingredients").forGetter(NaturalRecipe::ingredients),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(NaturalRecipe::output),
                BuiltInRegistries.BLOCK.byNameCodec().fieldOf("fuel").forGetter(NaturalRecipe::fuel),
                Codec.INT.fieldOf("time").forGetter(NaturalRecipe::time)
        ).apply(inst, NaturalRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, NaturalRecipe> STREAM_CODEC = StreamCodec.composite(
                SizedIngredient.STREAM_CODEC.apply(ByteBufCodecs.list()), NaturalRecipe::ingredients,
                ItemStack.STREAM_CODEC, NaturalRecipe::output,
                ByteBufCodecs.registry(BuiltInRegistries.BLOCK.key()), NaturalRecipe::fuel,
                ByteBufCodecs.VAR_INT, NaturalRecipe::time,
                NaturalRecipe::new
        );

        @Override
        public MapCodec<NaturalRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, NaturalRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}

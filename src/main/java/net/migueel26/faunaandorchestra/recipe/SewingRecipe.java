package net.migueel26.faunaandorchestra.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public record SewingRecipe(List<SizedIngredient> ingredients, ItemStack output) implements Recipe<SewingRecipe.RecipeInput> {
    @Override
    public boolean matches(SewingRecipe.RecipeInput input, Level level) {
        List<ItemStack> inputs = new ArrayList<>(input.items());
        inputs.removeIf(ItemStack::isEmpty);

        for (SizedIngredient required : this.ingredients) {
            int amountNeeded = required.amount();
            int amountFound = 0;

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
        return ModRecipes.SEWING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.SEWING_TYPE.get();
    }

    public record RecipeInput(List<ItemStack> items) implements net.minecraft.world.item.crafting.RecipeInput {
        @Override
        public ItemStack getItem(int index) {
            return items.get(index);
        }

        @Override
        public int size() {
            return items.size();
        }
    }

    public static class Serializer implements RecipeSerializer<SewingRecipe> {
        public static final MapCodec<SewingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                SizedIngredient.CODEC.codec().listOf().fieldOf("ingredients").forGetter(SewingRecipe::ingredients),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(SewingRecipe::output)
        ).apply(inst, SewingRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, SewingRecipe> STREAM_CODEC = StreamCodec.composite(
                SizedIngredient.STREAM_CODEC.apply(ByteBufCodecs.list()), SewingRecipe::ingredients,
                ItemStack.STREAM_CODEC, SewingRecipe::output,
                SewingRecipe::new
        );

        @Override
        public MapCodec<SewingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SewingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}

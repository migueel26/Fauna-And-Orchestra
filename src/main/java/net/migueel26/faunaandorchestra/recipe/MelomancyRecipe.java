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

public record MelomancyRecipe(List<SizedIngredient> ingredients, ItemStack catalyst, ItemStack output) implements Recipe<MelomancyRecipe.RecipeInput> {
    @Override
    public boolean matches(MelomancyRecipe.RecipeInput input, Level level) {
        /* The catalyst matching is handled by the block

        if (input.catalyst().isEmpty() || !ItemStack.isSameItem(input.catalyst(), this.catalyst)) {
            return false;
        }*/

        return matchesIngredientsOnly(input);
    }

    public boolean matchesIngredientsOnly(MelomancyRecipe.RecipeInput input) {
        List<ItemStack> inputItems = input.items().stream().filter(s -> !s.isEmpty()).toList();

        if (inputItems.size() != this.ingredients.size()) return false;

        List<SizedIngredient> remainingIngredients = new ArrayList<>(this.ingredients);

        for (ItemStack inputItem : inputItems) {
            boolean matched = false;
            for (int i = 0; i < remainingIngredients.size(); i++) {
                SizedIngredient req = remainingIngredients.get(i);

                if (req.ingredient().test(inputItem) && inputItem.getCount() == req.amount()) {
                    remainingIngredients.remove(i);
                    matched = true;
                    break;
                }
            }
            if (!matched) return false;
        }

        return remainingIngredients.isEmpty();
    }

    @Override
    public ItemStack assemble(MelomancyRecipe.RecipeInput melomancyInput, HolderLookup.Provider provider) {
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
        return ModRecipes.MELOMANCY_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.MELOMANCY_TYPE.get();
    }

    public record RecipeInput(List<ItemStack> items, ItemStack catalyst) implements net.minecraft.world.item.crafting.RecipeInput {
        @Override
        public ItemStack getItem(int index) {
            return items.get(index);
        }

        @Override
        public int size() {
            return items.size();
        }
    }

    public static class Serializer implements RecipeSerializer<MelomancyRecipe> {
        public static final MapCodec<MelomancyRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                SizedIngredient.CODEC.codec().listOf().fieldOf("ingredients").forGetter(MelomancyRecipe::ingredients),
                ItemStack.STRICT_CODEC.optionalFieldOf("catalyst", ItemStack.EMPTY).forGetter(MelomancyRecipe::catalyst),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(MelomancyRecipe::output)
        ).apply(inst, MelomancyRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, MelomancyRecipe> STREAM_CODEC = StreamCodec.composite(
                SizedIngredient.STREAM_CODEC.apply(ByteBufCodecs.list()), MelomancyRecipe::ingredients,
                ItemStack.OPTIONAL_STREAM_CODEC, MelomancyRecipe::catalyst,
                ItemStack.STREAM_CODEC, MelomancyRecipe::output,
                MelomancyRecipe::new
        );

        @Override
        public MapCodec<MelomancyRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, MelomancyRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}

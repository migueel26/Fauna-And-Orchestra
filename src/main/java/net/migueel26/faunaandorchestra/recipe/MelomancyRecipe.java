package net.migueel26.faunaandorchestra.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public record MelomancyRecipe(ResourceLocation id, List<SizedIngredient> ingredients, ItemStack catalyst, ItemStack output) implements Recipe<MelomancyRecipe.RecipeInput> {
    @Override
    public boolean matches(MelomancyRecipe.RecipeInput input, Level level) {
        /* The catalyst matching is handled by the block

        if (input.catalyst().isEmpty() || !ItemStack.isSameItem(input.catalyst(), this.catalyst)) {
            return false;
        }*/

        return matchesIngredientsOnly(input);
    }

    public boolean matchesIngredientsOnly(MelomancyRecipe.RecipeInput input) {
        List<ItemStack> inputItems = input.getItems().stream().filter(s -> !s.isEmpty()).toList();

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
    public ItemStack assemble(RecipeInput recipeInput, RegistryAccess registryAccess) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return output;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.MELOMANCY_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.MELOMANCY_TYPE.get();
    }

    public static class RecipeInput extends SimpleContainer {
        public final ItemStack catalyst;

        public RecipeInput(List<ItemStack> items, ItemStack catalyst) {
            super(items.toArray(new ItemStack[0]));
            this.catalyst = catalyst;
        }

        public List<ItemStack> getItems() {
            List<ItemStack> list = new ArrayList<>();
            for (int i = 0; i < this.getContainerSize(); i++) {
                list.add(this.getItem(i));
            }
            return list;
        }
    }

    public static class Serializer implements RecipeSerializer<MelomancyRecipe> {
        @Override
        public MelomancyRecipe fromJson(ResourceLocation id, JsonObject json) {
            JsonArray ingredientsJson = GsonHelper.getAsJsonArray(json, "ingredients");
            List<SizedIngredient> ingredients = new ArrayList<>();
            for (JsonElement element : ingredientsJson) {
                ingredients.add(SizedIngredient.fromJson(element.getAsJsonObject()));
            }

            ItemStack catalyst = ItemStack.EMPTY;
            if (json.has("catalyst")) {
                catalyst = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "catalyst"));
            }

            ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));

            return new MelomancyRecipe(id, ingredients, catalyst, result);
        }

        @Override
        public MelomancyRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            int size = buf.readVarInt();
            List<SizedIngredient> ingredients = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                ingredients.add(SizedIngredient.fromNetwork(buf));
            }

            ItemStack catalyst = buf.readItem();
            ItemStack result = buf.readItem();

            return new MelomancyRecipe(id, ingredients, catalyst, result);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, MelomancyRecipe recipe) {
            buf.writeVarInt(recipe.ingredients().size());
            for (SizedIngredient ingredient : recipe.ingredients()) {
                ingredient.toNetwork(buf);
            }

            buf.writeItem(recipe.catalyst());
            buf.writeItem(recipe.output());
        }
    }
}

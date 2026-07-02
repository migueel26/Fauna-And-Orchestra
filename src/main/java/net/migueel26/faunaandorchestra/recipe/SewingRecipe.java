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

public record SewingRecipe(ResourceLocation id, List<SizedIngredient> ingredients, ItemStack output) implements Recipe<SewingRecipe.RecipeInput> {
    @Override
    public boolean matches(SewingRecipe.RecipeInput input, Level level) {
        List<ItemStack> inputs = new ArrayList<>();
        for (ItemStack stack : input.getItems()) {
            if (!stack.isEmpty()) {
                inputs.add(stack.copy());
            }
        }

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
        return ModRecipes.SEWING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.SEWING_TYPE.get();
    }

    public static class RecipeInput extends SimpleContainer {
        public RecipeInput(List<ItemStack> items) {
            super(items.toArray(new ItemStack[0]));
        }

        public List<ItemStack> getItems() {
            List<ItemStack> list = new ArrayList<>();
            for (int i = 0; i < this.getContainerSize(); i++) {
                list.add(this.getItem(i));
            }
            return list;
        }
    }

    public static class Serializer implements RecipeSerializer<SewingRecipe> {
        @Override
        public SewingRecipe fromJson(ResourceLocation id, JsonObject json) {
            JsonArray ingredientsJson = GsonHelper.getAsJsonArray(json, "ingredients");
            List<SizedIngredient> ingredients = new ArrayList<>();
            for (JsonElement element : ingredientsJson) {
                ingredients.add(SizedIngredient.fromJson(element.getAsJsonObject()));
            }

            ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));

            return new SewingRecipe(id, ingredients, result);
        }

        @Override
        public SewingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            int size = buf.readVarInt();
            List<SizedIngredient> ingredients = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                ingredients.add(SizedIngredient.fromNetwork(buf));
            }

            ItemStack result = buf.readItem();

            return new SewingRecipe(id, ingredients, result);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, SewingRecipe recipe) {
            buf.writeVarInt(recipe.ingredients().size());
            for (SizedIngredient ingredient : recipe.ingredients()) {
                ingredient.toNetwork(buf);
            }

            buf.writeItem(recipe.output());
        }
    }
}

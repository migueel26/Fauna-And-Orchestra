package net.migueel26.faunaandorchestra.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public record NaturalRecipe(ResourceLocation id, List<SizedIngredient> ingredients, ItemStack output, HolderSet<Block> fuel, int time) implements Recipe<NaturalRecipe.RecipeInput> {
    @Override
    public boolean matches(RecipeInput input, Level level) {
        if (!input.fuel.is(this.fuel)) {
            return false;
        }

        List<ItemStack> inputs = new ArrayList<>(input.getItems());
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
        return ModRecipes.NATURAL_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.NATURAL_TYPE.get();
    }

    public static class RecipeInput extends SimpleContainer {
        public final BlockState fuel;
        public RecipeInput(List<ItemStack> items, BlockState fuel) {
            super(items.toArray(new ItemStack[0]));
            this.fuel = fuel;
        }

        public List<ItemStack> getItems() {
            List<ItemStack> list = new ArrayList<>();
            for (int i = 0; i < this.getContainerSize(); i++) {
                list.add(this.getItem(i));
            }
            return list;
        }
    }

    public static class Serializer implements RecipeSerializer<NaturalRecipe> {
        @Override
        public NaturalRecipe fromJson(ResourceLocation id, JsonObject json) {
            JsonArray ingredientsJson = GsonHelper.getAsJsonArray(json, "ingredients");
            List<SizedIngredient> ingredients = new ArrayList<>();
            for (JsonElement element : ingredientsJson) {
                ingredients.add(SizedIngredient.fromJson(element.getAsJsonObject()));
            }

            JsonObject resultJson = GsonHelper.getAsJsonObject(json, "result");
            ItemStack result = ShapedRecipe.itemStackFromJson(resultJson);

            com.google.gson.JsonElement fuelJson = json.get("fuel");
            HolderSet<Block> fuel = BuiltInRegistries.BLOCK
                    .getOrCreateTag(TagKey.create(Registries.BLOCK,
                            ResourceLocation.parse(fuelJson.getAsString().replace("#", ""))));

            int time = GsonHelper.getAsInt(json, "time");

            return new NaturalRecipe(id, ingredients, result, fuel, time);
        }

        @Override
        public NaturalRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            int size = buf.readVarInt();
            List<SizedIngredient> ingredients = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                ingredients.add(SizedIngredient.fromNetwork(buf));
            }

            ItemStack result = buf.readItem();

            int fuelSize = buf.readVarInt();
            List<Holder<Block>> holders = new ArrayList<>(fuelSize);
            for (int i = 0; i < fuelSize; i++) {
                int blockId = buf.readVarInt();
                BuiltInRegistries.BLOCK.getHolder(blockId).ifPresent(holders::add);
            }
            HolderSet<Block> fuel = HolderSet.direct(holders);

            int time = buf.readVarInt();

            return new NaturalRecipe(id, ingredients, result, fuel, time);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, NaturalRecipe recipe) {
            buf.writeVarInt(recipe.ingredients().size());
            for (SizedIngredient ingredient : recipe.ingredients()) {
                ingredient.toNetwork(buf);
            }

            buf.writeItem(recipe.output());

            buf.writeVarInt(recipe.fuel().size());
            for (Holder<Block> holder : recipe.fuel()) {
                buf.writeVarInt(BuiltInRegistries.BLOCK.getId(holder.value()));
            }

            buf.writeVarInt(recipe.time());
        }
    }
}

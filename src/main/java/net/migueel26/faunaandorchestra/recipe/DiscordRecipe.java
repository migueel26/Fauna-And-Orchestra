package net.migueel26.faunaandorchestra.recipe;

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
import org.jetbrains.annotations.Nullable;

public record DiscordRecipe(ResourceLocation id, SizedIngredient ingredient, int essence, int baseInstability, float extraProportion, ItemStack output) implements Recipe<DiscordRecipe.RecipeInput> {
    @Override
    public boolean matches(DiscordRecipe.RecipeInput input, Level level) {
        return this.ingredient.ingredient().test(input.getItem(0)) && input.getItem(0).getCount() >= this.ingredient.amount();
    }

    @Override
    public ItemStack assemble(DiscordRecipe.RecipeInput melomancyInput, RegistryAccess registryAccess) {
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
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.DISCORD_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.DISCORD_TYPE.get();
    }

    public static class RecipeInput extends SimpleContainer {
        ItemStack ingredient;
        public RecipeInput(ItemStack ingredient) {
            super(ingredient);
            this.ingredient = ingredient;
        }

        @Override
        public ItemStack getItem(int index) {
            return index == 0 ? ingredient : ItemStack.EMPTY;
        }
    }

    public static class Serializer implements RecipeSerializer<DiscordRecipe> {
        /*
        public static final MapCodec<DiscordRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                SizedIngredient.CODEC.codec().fieldOf("ingredient").forGetter(DiscordRecipe::ingredient),
                Codec.INT.fieldOf("essence").forGetter(DiscordRecipe::essence),
                Codec.INT.fieldOf("base_instability").forGetter(DiscordRecipe::baseInstability),
                Codec.FLOAT.fieldOf("extra_proportion").forGetter(DiscordRecipe::extraProportion),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(DiscordRecipe::output)
        ).apply(inst, DiscordRecipe::new));
         */
        @Override
        public DiscordRecipe fromJson(ResourceLocation id, JsonObject json) {
            JsonObject ingredientJson = GsonHelper.getAsJsonObject(json, "ingredient");
            SizedIngredient ingredient1 = SizedIngredient.fromJson(ingredientJson);

            int essence = GsonHelper.getAsInt(json, "essence");
            int baseInstability = GsonHelper.getAsInt(json, "base_instability");
            float extraProportion = GsonHelper.getAsFloat(json, "extra_proportion");

            JsonObject resultJson = GsonHelper.getAsJsonObject(json, "result");
            ItemStack result = ShapedRecipe.itemStackFromJson(resultJson);

            return new DiscordRecipe(id, ingredient1, essence, baseInstability, extraProportion, result);
        }

        @Override
        public @Nullable DiscordRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            SizedIngredient ingredient1 = SizedIngredient.fromNetwork(buf);

            int essence = buf.readVarInt();
            int baseInstability = buf.readVarInt();
            float extraProportion = buf.readFloat();

            ItemStack result = buf.readItem();

            return new DiscordRecipe(id, ingredient1, essence, baseInstability, extraProportion, result);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, DiscordRecipe recipe) {
            recipe.ingredient().toNetwork(buf);

            buf.writeVarInt(recipe.essence());
            buf.writeVarInt(recipe.baseInstability());
            buf.writeFloat(recipe.extraProportion());

            buf.writeItem(recipe.output());
        }
    }
}
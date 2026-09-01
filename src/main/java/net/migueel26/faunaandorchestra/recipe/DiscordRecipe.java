package net.migueel26.faunaandorchestra.recipe;

import com.mojang.serialization.Codec;
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

public record DiscordRecipe(SizedIngredient ingredient, int essence, int baseInstability, float extraProportion, ItemStack output) implements Recipe<DiscordRecipe.RecipeInput> {
    @Override
    public boolean matches(DiscordRecipe.RecipeInput input, Level level) {
        return this.ingredient.ingredient().test(input.ingredient()) && input.ingredient().getCount() >= this.ingredient.amount();
    }

    @Override
    public ItemStack assemble(DiscordRecipe.RecipeInput melomancyInput, HolderLookup.Provider provider) {
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
        return ModRecipes.DISCORD_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.DISCORD_TYPE.get();
    }

    public record RecipeInput(ItemStack ingredient) implements net.minecraft.world.item.crafting.RecipeInput {
        @Override
        public ItemStack getItem(int index) {
            return index == 0 ? ingredient : ItemStack.EMPTY;
        }

        @Override
        public int size() {
            return 1;
        }
    }

    public static class Serializer implements RecipeSerializer<DiscordRecipe> {
        public static final MapCodec<DiscordRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                SizedIngredient.CODEC.codec().fieldOf("ingredient").forGetter(DiscordRecipe::ingredient),
                Codec.INT.fieldOf("essence").forGetter(DiscordRecipe::essence),
                Codec.INT.fieldOf("base_instability").forGetter(DiscordRecipe::baseInstability),
                Codec.FLOAT.fieldOf("extra_proportion").forGetter(DiscordRecipe::extraProportion),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(DiscordRecipe::output)
        ).apply(inst, DiscordRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, DiscordRecipe> STREAM_CODEC = StreamCodec.composite(
                SizedIngredient.STREAM_CODEC, DiscordRecipe::ingredient,
                ByteBufCodecs.INT, DiscordRecipe::essence,
                ByteBufCodecs.INT, DiscordRecipe::baseInstability,
                ByteBufCodecs.FLOAT, DiscordRecipe::extraProportion,
                ItemStack.STREAM_CODEC, DiscordRecipe::output,
                DiscordRecipe::new
        );

        @Override
        public MapCodec<DiscordRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, DiscordRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}

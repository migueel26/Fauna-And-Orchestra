package net.migueel26.faunaandorchestra.recipe;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

public record SizedIngredient(Ingredient ingredient, int amount) {
    public static SizedIngredient fromJson(JsonObject json) {
        Ingredient ingredient = Ingredient.fromJson(json.get("ingredient"));
        int amount = GsonHelper.getAsInt(json, "amount", 1);

        return new SizedIngredient(ingredient, amount);
    }

    public static SizedIngredient fromNetwork(FriendlyByteBuf buf) {
        Ingredient ingredient = Ingredient.fromNetwork(buf);
        int amount = buf.readVarInt();

        return new SizedIngredient(ingredient, amount);
    }

    public void toNetwork(FriendlyByteBuf buf) {
        this.ingredient().toNetwork(buf);
        buf.writeVarInt(this.amount());
    }

    public boolean test(@NotNull ItemStack stack) {
        return this.ingredient.test(stack) && stack.getCount() >= this.amount;
    }

    public Stream<ItemStack> getItems() {
        return Stream.of(this.ingredient.getItems()).map(stack -> {
            ItemStack copy = stack.copy();
            copy.setCount(this.amount);
            return copy;
        });
    }
}
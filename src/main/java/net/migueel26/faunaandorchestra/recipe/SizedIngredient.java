package net.migueel26.faunaandorchestra.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

public record SizedIngredient(Ingredient ingredient, int amount) implements ICustomIngredient {
    public static final MapCodec<SizedIngredient> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(SizedIngredient::ingredient),
            Codec.INT.optionalFieldOf("amount", 1).forGetter(SizedIngredient::amount)
    ).apply(inst, SizedIngredient::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SizedIngredient> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, SizedIngredient::ingredient,
            ByteBufCodecs.VAR_INT, SizedIngredient::amount,
            SizedIngredient::new
    );

    @Override
    public boolean test(@NotNull ItemStack stack) {
        return this.ingredient.test(stack) && stack.getCount() >= this.amount;
    }

    @Override
    public Stream<ItemStack> getItems() {
        return Stream.of(this.ingredient.getItems()).map(stack -> {
            ItemStack copy = stack.copy();
            copy.setCount(this.amount);
            return copy;
        });
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public IngredientType<?> getType() {
        return ModIngredientTypes.SIZED.get();
    }
}

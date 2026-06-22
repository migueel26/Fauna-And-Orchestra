package net.migueel26.faunaandorchestra.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Containers;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.Optional;

public class BlocksUtil {
    public static void dropContents(Level level, BlockPos pos, ItemStackHandler container) {
        NonNullList<ItemStack> contents = NonNullList.createWithCapacity(container.getSlots());

        for (int i = 0; i < container.getSlots(); i++) {
            ItemStack stack = container.getStackInSlot(i);
            contents.add(stack);
        }

        Containers.dropContents(level, pos, contents);
    }

    public static ItemStack getRandomItemFromTag(TagKey<Item> tag, Level level) {
        HolderLookup.RegistryLookup<Item> registry = level.registryAccess().lookupOrThrow(Registries.ITEM);

        Optional<HolderSet.Named<Item>> tagContents = registry.get(tag);

        return tagContents.flatMap(holders ->
                holders.getRandomElement(level.random)
        ).map(holder ->
                new ItemStack(holder.value())
        ).orElse(ItemStack.EMPTY);
    }
}

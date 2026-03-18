package net.migueel26.faunaandorchestra.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.ItemStackHandler;

public class BlocksUtil {
    public static void dropContents(Level level, BlockPos pos, ItemStackHandler container) {
        NonNullList<ItemStack> contents = NonNullList.createWithCapacity(container.getSlots());

        for (int i = 0; i < container.getSlots(); i++) {
            ItemStack stack = container.getStackInSlot(i);
            contents.add(stack);
        }

        Containers.dropContents(level, pos, contents);
    }
}

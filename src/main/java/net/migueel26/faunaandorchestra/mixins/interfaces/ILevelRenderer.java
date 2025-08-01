package net.migueel26.faunaandorchestra.mixins.interfaces;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;

public interface ILevelRenderer {
    void playOrchestraSong(Holder<Item> item, BlockPos pos);
}

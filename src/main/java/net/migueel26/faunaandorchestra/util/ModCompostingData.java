package net.migueel26.faunaandorchestra.util;

import net.migueel26.faunaandorchestra.item.ModItems;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.ComposterBlock;

import java.util.HashMap;
import java.util.Map;

public class ModCompostingData {
    public static Map<ItemLike, Float> COMPOSTABLES = new HashMap<>(
            Map.of(
                    ModItems.COTTON_PLANT_SEEDS.get(), 0.3f,
                    ModItems.COTTON.get(), 0.65f
            )
    );

    public static void register() {
        ComposterBlock.COMPOSTABLES.putAll(COMPOSTABLES);
    }
}

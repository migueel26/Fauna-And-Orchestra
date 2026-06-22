package net.migueel26.faunaandorchestra.worldgen.tree;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.worldgen.ModConfiguredFeatures;
import net.minecraft.world.level.block.grower.TreeGrower;

import java.util.Optional;

public class ModTreeGrowers {
    public static final TreeGrower GINGKO_BILOBA_TREE = new TreeGrower(FaunaAndOrchestra.MOD_ID + ":gingko_biloba",
            Optional.empty(), Optional.of(ModConfiguredFeatures.GINKGO_BILOBA_KEY), Optional.empty());
}

package net.migueel26.faunaandorchestra.worldgen.structures;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModStructuredProcessors {
    // DO NOT assign here directly!

    public static final DeferredRegister<StructureProcessorType<?>> PROCESSORS =
            DeferredRegister.create(BuiltInRegistries.STRUCTURE_PROCESSOR, FaunaAndOrchestra.MOD_ID);

    public static final DeferredHolder<StructureProcessorType<?>, GroundStructureProcessorType> GROUNDED =
            PROCESSORS.register("grounded",
                    GroundStructureProcessorType::new
            );

    public static void register(IEventBus bus) {
        PROCESSORS.register(bus);
    }
}

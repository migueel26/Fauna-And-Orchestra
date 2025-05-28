package net.migueel26.faunaandorchestra.component;

import com.mojang.serialization.Codec;
import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.UUID;
import java.util.function.UnaryOperator;

public class ModDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES =
            DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, FaunaAndOrchestra.MOD_ID);

        public static final DeferredHolder<DataComponentType<?>, DataComponentType<UUID>> MUSICIAN_UUID = register("musician_uuid",
            builder -> builder.persistent(UUIDUtil.CODEC));

        public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> OPENED = register("briefcase_opened",
                builder -> builder.persistent(Codec.BOOL));

        public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> BRIEFCASE_ANIMAL = register("briefcase_animal",
                builder -> builder.persistent(Codec.STRING));

        public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<String>>> BRIEFCASE_ANIMAL_1 = register("aa",
                builder -> builder.persistent(Codec.list(Codec.STRING, 0, 4)));

    private static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String name, UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
        return DATA_COMPONENT_TYPES.register(name, () -> builderOperator.apply(DataComponentType.builder()).build());
    }

    public static void register(IEventBus eventBus) {
        DATA_COMPONENT_TYPES.register(eventBus);
    }
}

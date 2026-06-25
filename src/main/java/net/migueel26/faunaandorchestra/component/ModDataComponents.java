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
    // Since the original mod is from Neoforge, I use this class as a substitute with the names of the tags
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES =
            DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, FaunaAndOrchestra.MOD_ID);
    public static final int MAX_SIZE = 6;
        public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> FAUNA_NAME = register("whistle_name",
                builder -> builder.persistent(Codec.STRING));

        public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> OPENED = register("briefcase_opened",
                builder -> builder.persistent(Codec.BOOL));

        public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<String>>> BRIEFCASE_ANIMAL_LIST = register("briefcase_animal_list",
                builder -> builder.persistent(Codec.list(Codec.STRING, 0, MAX_SIZE)));

        public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<Integer>>> PAN_FLUTE_LIST = register("pan_flute_list",
                builder -> builder.persistent(Codec.list(Codec.INT)));

        public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> LIST_INDEX = register("index_list",
                builder -> builder.persistent(Codec.INT));
    public static final String MUSICIAN_UUID = "MusicianUUID";
    public static String POSITION = "target_position";
    public static String SENDER = "sender";
    public static String RECEIVER = "receiver";
}

package net.migueel26.faunaandorchestra.entity;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.custom.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, FaunaAndOrchestra.MOD_ID);

    public  static final Supplier<EntityType<MantisEntity>> MANTIS = ENTITY_TYPES.register("mantis",
            () -> EntityType.Builder.of(MantisEntity::new, MobCategory.CREATURE).sized(1f, 2f).build("mantis"));
    public  static final Supplier<EntityType<ToadEntity>> TOAD = ENTITY_TYPES.register("toad",
            () -> EntityType.Builder.of(ToadEntity::new, MobCategory.CREATURE).sized(1f, 2f).build("toad"));
    public static final Supplier<EntityType<PenguinEntity>> PENGUIN = ENTITY_TYPES.register("penguin",
            () -> EntityType.Builder.of(PenguinEntity::new, MobCategory.CREATURE).sized(0.75f, 0.75f).build("penguin"));
    public static final Supplier<EntityType<RedPandaEntity>> RED_PANDA = ENTITY_TYPES.register("red_panda",
            () -> EntityType.Builder.of(RedPandaEntity::new, MobCategory.CREATURE).sized(0.75f, 1f).build("red_panda"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}

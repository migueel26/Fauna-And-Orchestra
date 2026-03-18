package net.migueel26.faunaandorchestra;

import java.util.Set;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
@EventBusSubscriber(modid = FaunaAndOrchestra.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.BooleanValue ANYA_SPAWN = BUILDER
            .comment("Should the mysterious character appear at the beginning?")
            .define("character_spawn", true);
    private static final ModConfigSpec.BooleanValue GIVE_BOOK = BUILDER
            .comment("Should the book be dropped at the beginning?")
            .define("give_book", true);

    static final ModConfigSpec SPEC = BUILDER.build();
    public static boolean anyaSpawn;
    public static boolean giveBook;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        anyaSpawn = ANYA_SPAWN.get();
        giveBook = GIVE_BOOK.get();
    }
}

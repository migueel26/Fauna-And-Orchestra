package net.migueel26.faunaandorchestra;

import net.minecraftforge.common.ForgeConfigSpec; // <--- Cambio importante
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = FaunaAndOrchestra.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.BooleanValue ANYA_SPAWN = BUILDER
            .comment("Should the mysterious character appear at the beginning?")
            .define("character_spawn", true);
    private static final ForgeConfigSpec.BooleanValue GIVE_BOOK = BUILDER
            .comment("Should the book be dropped at the beginning?")
            .define("give_book", true);

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean anyaSpawn;
    public static boolean giveBook;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        if (event.getConfig().getSpec() == SPEC) {
            anyaSpawn = ANYA_SPAWN.get();
            giveBook = GIVE_BOOK.get();
        }
    }
}
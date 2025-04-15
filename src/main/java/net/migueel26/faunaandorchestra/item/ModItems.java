package net.migueel26.faunaandorchestra.item;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.ModEntities;
import net.migueel26.faunaandorchestra.item.custom.InstrumentItem;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(FaunaAndOrchestra.MOD_ID);

    public static final DeferredItem<Item> VIOLIN = ITEMS.register("violin",
            () -> new InstrumentItem(new Item.Properties().stacksTo(1), ModSounds.VIOLIN_USE.get()));

    public static final DeferredItem<Item> FLUTE = ITEMS.register("flute",
            () -> new InstrumentItem(new Item.Properties().stacksTo(1), ModSounds.FLUTE_USE.get()));

    public static final DeferredItem<Item> KEYTAR = ITEMS.register("keytar",
            () -> new InstrumentItem(new Item.Properties().stacksTo(1), ModSounds.KEYTAR_USE.get()));

    public static final DeferredItem<Item> DOUBLE_BASS = ITEMS.register("double_bass",
            () -> new InstrumentItem(new Item.Properties().stacksTo(1), ModSounds.DOUBLE_BASS_USE.get()));

    public static final DeferredItem<Item> BACH_AIR_SHEET_MUSIC = ITEMS.register("bach_air_sheet_music",
            () -> new Item(new Item.Properties().rarity(Rarity.RARE)));

    public static final DeferredItem<Item> MANTIS_SPAWN_EGG = ITEMS.register("mantis_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.MANTIS, 0x46eb4c, 0x23a628,
                    new Item.Properties()));

    public static final DeferredItem<Item> PENGUIN_SPAWN_EGG = ITEMS.register("penguin_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.PENGUIN, 0xd7d7d9, 0x0e0e1a,
                    new Item.Properties()));

    public static final DeferredItem<Item> RED_PANDA_SPAWN_EGG = ITEMS.register("red_panda_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.RED_PANDA, 0xd63200, 0xd1d0cf,
                    new Item.Properties()));

    public static final DeferredItem<Item> MACAW_SPAWN_EGG = ITEMS.register("macaw_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.MACAW, 0x002196, 0xffea00,
                    new Item.Properties()));
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}

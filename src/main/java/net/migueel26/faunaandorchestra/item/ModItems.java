package net.migueel26.faunaandorchestra.item;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.item.custom.InstrumentItem;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(FaunaAndOrchestra.MOD_ID);

    public static final DeferredItem<Item> VIOLIN = ITEMS.register("violin",
            () -> new InstrumentItem(new Item.Properties().stacksTo(1), ModSounds.VIOLIN_USE.get()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}

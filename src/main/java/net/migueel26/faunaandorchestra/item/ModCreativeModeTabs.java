package net.migueel26.faunaandorchestra.item;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.checkerframework.checker.units.qual.C;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, FaunaAndOrchestra.MOD_ID);

    public static final Supplier<CreativeModeTab> FAUNA_AND_ORCHESTRA = CREATIVE_MODE_TAB.register("fauna_and_orchestra",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(Items.LILY_PAD))
                    .title(Component.literal("Fauna and Orchestra"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(Items.LILY_PAD);
                    }).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}

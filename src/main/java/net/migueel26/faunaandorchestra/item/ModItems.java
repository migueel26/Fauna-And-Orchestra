package net.migueel26.faunaandorchestra.item;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.ModEntities;
import net.migueel26.faunaandorchestra.item.custom.BatonItem;
import net.migueel26.faunaandorchestra.item.custom.BriefcaseItem;
import net.migueel26.faunaandorchestra.item.custom.InstrumentItem;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.migueel26.faunaandorchestra.util.MusicUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

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

    public static final DeferredItem<Item> BATON = ITEMS.register("baton",
            () -> new BatonItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> BRIEFCASE = ITEMS.register("briefcase",
            () -> new BriefcaseItem(new Item.Properties().stacksTo(1)));

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

    public static final DeferredItem<Item> QUIRKY_FROG_SPAWN_EGG = ITEMS.register("quirky_frog_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.QUIRKY_FROG, 0x245715, 0xbfbd82,
                    new Item.Properties()));

    public static final DeferredItem<Item> BACH_AIR_SHEET_MUSIC = ITEMS.register("bach_air_sheet_music",
            createSheetMusic());

    public static final DeferredItem<Item> GREENSLEEVES_SHEET_MUSIC = ITEMS.register("greensleeves_sheet_music",
            createSheetMusic());

    public static final DeferredItem<Item> ICON = ITEMS.register("icon",
            () -> new Item(new Item.Properties().stacksTo(1)));

    @NotNull
    private static Supplier<Item> createSheetMusic() {
        return () -> new Item(new Item.Properties().rarity(Rarity.RARE)) {
            @Override
            public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                MutableComponent instruments = Component.empty();
                Iterator<Item> iterator = MusicUtil.getInstruments(this.asItem()).iterator();
                while (iterator.hasNext()) {
                    instruments.append(Component.translatable(iterator.next().getDescriptionId()));
                    if (iterator.hasNext()) {
                        instruments.append(Component.literal(", "));
                    }
                }
                tooltipComponents.add(instruments.withStyle(ChatFormatting.GRAY));
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
            }
        };
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}

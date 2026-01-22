package net.migueel26.faunaandorchestra.item;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.advancements.ModAdvancements;
import net.migueel26.faunaandorchestra.block.ModBlocks;
import net.migueel26.faunaandorchestra.component.ModDataComponents;
import net.migueel26.faunaandorchestra.entity.ModEntities;
import net.migueel26.faunaandorchestra.item.custom.*;
import net.migueel26.faunaandorchestra.item.custom.InstrumentItem;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.migueel26.faunaandorchestra.util.ModTags;
import net.migueel26.faunaandorchestra.util.MusicUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
    public static final DeferredItem<Item> SAXOPHONE = ITEMS.register("saxophone",
            () -> new InstrumentItem(new Item.Properties().stacksTo(1), ModSounds.SAXOPHONE_USE.get()));
    public static final DeferredItem<Item> OBOE = ITEMS.register("oboe",
            () -> new InstrumentItem(new Item.Properties().stacksTo(1), ModSounds.OBOE_USE.get()));
    public static final DeferredItem<Item> CELLO = ITEMS.register("cello",
            () -> new InstrumentItem(new Item.Properties().stacksTo(1), ModSounds.CELLO_USE.get()));
    public static final DeferredItem<Item> PAN_FLUTE = ITEMS.register("pan_flute",
            () -> new PanFluteItem(new Item.Properties()
                    .stacksTo(1)
                    .durability(120)
                    .component(ModDataComponents.PAN_FLUTE_SOUND, 0)
                    .component(ModDataComponents.PAN_FLUTE_LIST, List.of())));

    public static final DeferredItem<Item> PAN_FLUTE_CREATIVE = ITEMS.register("pan_flute_creative",
            () -> new PanFluteItem(new Item.Properties()
                    .stacksTo(1)
                    .component(ModDataComponents.PAN_FLUTE_SOUND, 0)
                    .component(ModDataComponents.PAN_FLUTE_LIST, new ArrayList<>(List.of(1,2,3,4,5)))
                    .rarity(Rarity.EPIC)));
    public static final DeferredItem<Item> BATON = ITEMS.register("baton",
            () -> new BatonItem(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> LEGENDARY_BATON = ITEMS.register("legendary_baton",
            () -> new BatonItem(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));

    public static final DeferredItem<Item> BRIEFCASE = ITEMS.register("briefcase",
            () -> new BriefcaseItem(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> SINGING_SEED = ITEMS.register("singing_seed",
            () -> new ItemNameBlockItem(ModBlocks.SINGING_CROP.get(), new Item.Properties()));
    public static final DeferredItem<Item> BOOGIE_FRUIT = ITEMS.register("boogie_fruit",
            () -> new Item(new Item.Properties().food(ModFoodProperties.BOOGIE_FRUIT)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("item.faunaandorchestra.boogie_fruit.desc")
                            .withStyle(ChatFormatting.GRAY));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });

    public static final DeferredItem<Item> OFFERING = createRegularDescriptionItem("offering");
    public static final DeferredItem<Item> WANDERING_NOTE = ITEMS.register("wandering_note",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> AMPLIFIER_CRYSTAL = ITEMS.register("amplifier_crystal",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> STEELSONIC_INGOT = createRegularDescriptionItem("steelsonic_ingot");
    public static final DeferredItem<Item> GINKGO_BILOBA = createRegularDescriptionItem("gingko_biloba");
    public static final DeferredItem<Item> BUTTERFLY_NET = ITEMS.register("butterfly_net",
            () -> new Item(new Item.Properties().stacksTo(1).durability(48)) {
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("item.faunaandorchestra.butterfly_net.desc")
                            .withStyle(ChatFormatting.GRAY));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> WHISTLE = ITEMS.register("whistle",
            () -> new WhistleItem(new Item.Properties().stacksTo(1).durability(20)));
    public static final DeferredItem<Item> BOOGIE_BOMB = ITEMS.register("boogie_bomb",
            () -> new BoogieBombItem(new Item.Properties()));
    public static final DeferredItem<Item> VOICE_VESSEL = ITEMS.register("voice_vessel",
            () -> new VoiceVesselItem(new Item.Properties()
                    .stacksTo(1)
                    .component(ModDataComponents.OPENED, false)));
    public static final DeferredItem<Item> DISCORD_ESSENCE = createRegularDescriptionItem("discord_essence");
    public static final DeferredItem<Item> ACTIVATOR_CLEF = ITEMS.register("activator_clef",
            () -> new ActivatorClefItem(new Item.Properties()));
    public static final DeferredItem<Item> DISCORD_BOMB = ITEMS.register("discord_bomb",
            () -> new DiscordBombItem(new Item.Properties()));
    public static final DeferredItem<Item> TRANSMUTED_VOICE = ITEMS.register("transmuted_voice",
            () -> new TransmutedVoiceItem(new Item.Properties()));
    public static final DeferredItem<Item> MUSIC_BOTTLE = createRegularDescriptionItem("music_bottle");
    public static final DeferredItem<Item> FRUIT_OF_LIFE = ITEMS.register("fruit_of_life",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("item.faunaandorchestra.fruit_of_life.desc").withStyle(ChatFormatting.GRAY));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> PETALS_OF_DEATH = ITEMS.register("petals_of_death",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("item.faunaandorchestra.petals_of_death.desc").withStyle(ChatFormatting.GRAY));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> SHEET_FRAGMENTS = ITEMS.register("sheet_fragments",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("item.faunaandorchestra.sheet_fragments.desc")
                            .withStyle(ChatFormatting.GRAY));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> RINGTAILS_POSTER = ITEMS.register("ringtails_poster",
            () -> new RingtailsPosterItem(new Item.Properties().rarity(Rarity.RARE)));
    public static final DeferredItem<Item> MUSICAL_INK = ITEMS.register("musical_ink",
            () -> new MusicalInkItem(new Item.Properties()));
    public static final DeferredItem<Item> GLOVE = ITEMS.register("glove",
            () -> new Item(new Item.Properties().durability(3)));
    public static final DeferredItem<Item> PROP_CASE = ITEMS.register("prop_case",
            () -> new Item(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> TUXEDO = createClothingItem("tuxedo", ModTags.EntityTypes.WEARS_TUXEDO);
    public static final DeferredItem<Item> TAILCOAT = createClothingItem("tailcoat", ModTags.EntityTypes.WEARS_TAILCOAT);
    public static final DeferredItem<Item> SILVER_TINT = createClothingItem("silver_tint", ModTags.EntityTypes.WEARS_SILVER_TINT);

    public static final DeferredItem<Item> MANTIS_SPAWN_EGG = ITEMS.register("mantis_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.MANTIS, 0x46eb4c, 0x23a628,
                    new Item.Properties()));
    public static final DeferredItem<Item> PENGUIN_SPAWN_EGG = ITEMS.register("penguin_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.PENGUIN, 0xd7d7d9, 0x0e0e1a,
                    new Item.Properties()));
    public static final DeferredItem<Item> EMPEROR_PENGUIN_SPAWN_EGG = ITEMS.register("emperor_penguin_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.EMPEROR_PENGUIN, 0x0e0e1a, 0xd7d7d9,
                    new Item.Properties()));
    public static final DeferredItem<Item> RED_PANDA_SPAWN_EGG = ITEMS.register("red_panda_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.RED_PANDA, 0xd63200, 0xd1d0cf,
                    new Item.Properties()));
    public static final DeferredItem<Item> MACAW_SPAWN_EGG = ITEMS.register("macaw_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.MACAW, 0x002196, 0xffea00,
                    new Item.Properties()));
    public static final DeferredItem<Item> BEAVER_SPAWN_EGG = ITEMS.register("beaver_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.BEAVER, 0x170a00, 0x401e03,
                    new Item.Properties()));
    public static final DeferredItem<Item> LEMUR_SPAWN_EGG = ITEMS.register("lemur_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.LEMUR, 0x322a29, 0xc1c1c1,
                    new Item.Properties()));
    public static final DeferredItem<Item> MADAME_BUTTERFLY_SPAWN_EGG = ITEMS.register("madame_butterfly_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.MADAME_BUTTERFLY, 0x23a2e3, 0x02050d,
                    new Item.Properties()));
    public static final DeferredItem<Item> QUIRKY_FROG_SPAWN_EGG = ITEMS.register("quirky_frog_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.QUIRKY_FROG, 0x245715, 0xbfbd82,
                    new Item.Properties()));
    public static final DeferredItem<Item> WANDERING_KOALA_SPAWN_EGG = ITEMS.register("wandering_koala_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.WANDERING_KOALA, 0xb5b5b5, 0x707070,
                    new Item.Properties()));
    public static final DeferredItem<Item> SPROUTLING_SPAWN_EGG = ITEMS.register("sproutling_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SINGING_SPROUTLING, 0xd1793a, 0x33a03e,
                    new Item.Properties()));
    public static final DeferredItem<Item> BUTTERFLY_SPAWN_EGG = ITEMS.register("butterfly_spawn_egg",
            () -> new CustomSpawnEggItem(new Item.Properties(), ModEntities.BUTTERFLY.get()));
    public static final DeferredItem<Item> WISE_TREE_SPAWN_EGG = ITEMS.register("wise_tree_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.WISE_TREE, 0x5f4a2b, 0x567e22,
                    new Item.Properties()));
    public static final DeferredItem<Item> RINGTAILS_SPAWN_EGG = ITEMS.register("ringtails_spawn_egg",
            () -> new CustomSpawnEggItem(new Item.Properties(), ModEntities.FAUST.get(), ModEntities.ORION.get()) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("entity.faunaandorchestra.faust")
                            .append(Component.literal(" & "))
                            .append(Component.translatable("entity.faunaandorchestra.orion"))
                            .withStyle(ChatFormatting.GRAY));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });
    public static final DeferredItem<Item> THE_GREAT_COMPOSER_SPAWN_EGG = ITEMS.register("the_great_composer_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.THE_GREAT_COMPOSER, 0xe2d7a8, 0x23ab53, new Item.Properties()) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("item.faunaandorchestra.the_great_composer_spawn_egg.desc")
                            .withStyle(ChatFormatting.RED));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });

    public static final DeferredItem<Item> BACH_AIR_SHEET_MUSIC = createSheetMusic("bach_air_sheet_music");
    public static final DeferredItem<Item> GREENSLEEVES_SHEET_MUSIC = createSheetMusic("greensleeves_sheet_music");
    public static final DeferredItem<Item> BLUES_SHEET_MUSIC = createSheetMusic("blues_sheet_music");
    public static final DeferredItem<Item> JAZZY_FUR_ELISE_SHEET_MUSIC = createSheetMusic("jazzy_fur_elise_sheet_music");
    public static final DeferredItem<Item> DANCE_OF_THE_LITTLE_SWANS = createSheetMusic("dance_of_the_little_swans_sheet_music");
    public static final DeferredItem<Item> LA_BAMBA_SHEET_MUSIC = createSheetMusic("la_bamba_sheet_music");
    public static final DeferredItem<Item> RESURRECTION_SONG = createLegendarySheetMusic("resurrection_song");

    public static final DeferredItem<Item> THE_GREAT_HEAD_ITEM = ITEMS.register("the_great_head_item",
            () -> new TheGreatHeadItem(ModBlocks.THE_GREAT_HEAD.get(), new Item.Properties().rarity(Rarity.EPIC)));
    public static final DeferredItem<Item> MELOMANCY_CAULDRON_ITEM = ITEMS.register("melomancy_cauldron_item",
            () -> new MelomancyCauldronItem(ModBlocks.MELOMANCY_CAULDRON.get(), new Item.Properties()));
    public static final DeferredItem<Item> DISCORD_NUCLEI_ITEM = ITEMS.register("discord_nuclei_item",
            () -> new DiscordNucleiItem(ModBlocks.DISCORD_NUCLEI.get(), new Item.Properties()));

    // MISC
    public static final DeferredItem<Item> VOICE = ITEMS.register("voice",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> UNLOCKER = ITEMS.register("unlocker",
            () -> new Item(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("item.faunaandorchestra.unlocker.desc"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }

                @Override
                public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
                    if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
                        player.addItem(new ItemStack(ModItems.SHEET_FRAGMENTS.get()));
                        player.addItem(new ItemStack(ModItems.FRUIT_OF_LIFE.get()));
                        player.addItem(new ItemStack(ModItems.PETALS_OF_DEATH.get()));
                        player.addItem(new ItemStack(ModItems.RESURRECTION_SONG.get()));
                        player.addItem(new ItemStack(ModItems.MUSIC_BOTTLE.get()));
                        player.addItem(new ItemStack(ModItems.BOOGIE_FRUIT.get()));
                        ModAdvancements.KILL_COMPOSER.get().trigger(serverPlayer);
                        ModAdvancements.WISE_TREE.get().trigger(serverPlayer);
                        ModAdvancements.MEET_RINGTAILS.get().trigger(serverPlayer);
                        ModAdvancements.USE_DISCORD_BOMB.get().trigger(serverPlayer);
                    }
                    return super.use(level, player, usedHand);
                }
            });
    public static final DeferredItem<Item> ICON = ITEMS.register("icon",
            () -> new Item(new Item.Properties().stacksTo(1)));

    private static DeferredItem<Item> createSheetMusic(String name) {
        return ITEMS.register(name, () -> new Item(new Item.Properties().rarity(Rarity.RARE)) {
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
        });
    }

    private static DeferredItem<Item> createLegendarySheetMusic(String name) {
        return ITEMS.register(name,
                () -> new Item(new Item.Properties().rarity(Rarity.EPIC)) {
                    @Override
                    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                        tooltipComponents.add(Component.translatable("item.faunaandorchestra." + name + ".desc"));

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
                });
    }

    private static DeferredItem<Item> createRegularDescriptionItem(String name) {
        return ITEMS.register(name,
                () -> new Item(new Item.Properties()) {
                    @Override
                    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                        tooltipComponents.add(Component.translatable("item.faunaandorchestra." + name + ".desc")
                                .withStyle(ChatFormatting.GRAY));
                        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                    }
                });
    }

    private static DeferredItem<Item> createClothingItem(String name, TagKey<EntityType<?>> tag) {
        return ITEMS.register(name,
                () -> new Item(new Item.Properties()) {
                    @Override
                    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                        tooltipComponents.add(Component.translatable("item.faunaandorchestra_clothing.desc").withStyle(ChatFormatting.LIGHT_PURPLE));

                        MutableComponent musicians = Component.empty();

                        Iterator<Holder<EntityType<?>>> iterator = BuiltInRegistries.ENTITY_TYPE.getTagOrEmpty(tag).iterator();
                        while (iterator.hasNext()) {
                            musicians.append(Component.translatable(iterator.next().value().getDescriptionId()));
                            if (iterator.hasNext()) {
                                musicians.append(Component.literal(", "));
                            }
                        }

                        tooltipComponents.add(musicians.withStyle(ChatFormatting.DARK_GRAY));
                    }
                });
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}

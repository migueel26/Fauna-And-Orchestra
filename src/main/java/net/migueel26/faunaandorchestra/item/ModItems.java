package net.migueel26.faunaandorchestra.item;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.advancements.ModAdvancements;
import net.migueel26.faunaandorchestra.block.ModBlocks;
import net.migueel26.faunaandorchestra.entity.ModEntities;
import net.migueel26.faunaandorchestra.item.custom.*;
import net.migueel26.faunaandorchestra.item.custom.InstrumentItem;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.migueel26.faunaandorchestra.util.MusicUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, FaunaAndOrchestra.MOD_ID);

    public static final RegistryObject<Item> VIOLIN = ITEMS.register("violin",
            () -> new InstrumentItem(new Item.Properties().stacksTo(1), ModSounds.VIOLIN_USE.get()));
    public static final RegistryObject<Item> FLUTE = ITEMS.register("flute",
            () -> new InstrumentItem(new Item.Properties().stacksTo(1), ModSounds.FLUTE_USE.get()));
    public static final RegistryObject<Item> KEYTAR = ITEMS.register("keytar",
            () -> new InstrumentItem(new Item.Properties().stacksTo(1), ModSounds.KEYTAR_USE.get()));
    public static final RegistryObject<Item> DOUBLE_BASS = ITEMS.register("double_bass",
            () -> new InstrumentItem(new Item.Properties().stacksTo(1), ModSounds.DOUBLE_BASS_USE.get()));
    public static final RegistryObject<Item> SAXOPHONE = ITEMS.register("saxophone",
            () -> new InstrumentItem(new Item.Properties().stacksTo(1), ModSounds.SAXOPHONE_USE.get()));
    public static final RegistryObject<Item> OBOE = ITEMS.register("oboe",
            () -> new InstrumentItem(new Item.Properties().stacksTo(1), ModSounds.OBOE_USE.get()));
    public static final RegistryObject<Item> CELLO = ITEMS.register("cello",
            () -> new InstrumentItem(new Item.Properties().stacksTo(1), ModSounds.CELLO_USE.get()));
    public static final RegistryObject<Item> PAN_FLUTE = ITEMS.register("pan_flute",
            () -> new PanFluteItem(new Item.Properties()
                    .stacksTo(1)
                    .durability(120)));

    public static final RegistryObject<Item> PAN_FLUTE_CREATIVE = ITEMS.register("pan_flute_creative",
            () -> new PanFluteItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.EPIC)) {
                @Override
                public ItemStack getDefaultInstance() {
                    ItemStack stack = super.getDefaultInstance();

                    stack.getOrCreateTag().putIntArray("PanFluteList", new int[]{1, 2, 3, 4, 5});
                    stack.getOrCreateTag().putInt("PanFluteSound", 0);

                    return stack;
                }
            });
    public static final RegistryObject<Item> BATON = ITEMS.register("baton",
            () -> new BatonItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> LEGENDARY_BATON = ITEMS.register("legendary_baton",
            () -> new BatonItem(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));

    public static final RegistryObject<Item> BRIEFCASE = ITEMS.register("briefcase",
            () -> new BriefcaseItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> SINGING_SEED = ITEMS.register("singing_seed",
            () -> new ItemNameBlockItem(ModBlocks.SINGING_CROP.get(), new Item.Properties()));
    public static final RegistryObject<Item> BOOGIE_FRUIT = ITEMS.register("boogie_fruit",
            () -> new Item(new Item.Properties().food(ModFoodProperties.BOOGIE_FRUIT)) {
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("item.faunaandorchestra.boogie_fruit.desc")
                            .withStyle(ChatFormatting.GRAY));
                    super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
                }
            });
    public static final RegistryObject<Item> OFFERING = createRegularDescriptionItem("offering");
    public static final RegistryObject<Item> WANDERING_NOTE = ITEMS.register("wandering_note",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> AMPLIFIER_CRYSTAL = ITEMS.register("amplifier_crystal",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> STEELSONIC_INGOT = createRegularDescriptionItem("steelsonic_ingot");
    public static final RegistryObject<Item> GINKGO_BILOBA = createRegularDescriptionItem("gingko_biloba");
    public static final RegistryObject<Item> BUTTERFLY_NET = ITEMS.register("butterfly_net",
            () -> new Item(new Item.Properties().stacksTo(1).durability(48)) {
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("item.faunaandorchestra.butterfly_net.desc")
                            .withStyle(ChatFormatting.GRAY));
                    super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
                }
            });
    public static final RegistryObject<Item> WHISTLE = ITEMS.register("whistle",
            () -> new WhistleItem(new Item.Properties().stacksTo(1).durability(20)));
    public static final RegistryObject<Item> BOOGIE_BOMB = ITEMS.register("boogie_bomb",
            () -> new BoogieBombItem(new Item.Properties()));
    public static final RegistryObject<Item> VOICE_VESSEL = ITEMS.register("voice_vessel",
            () -> new VoiceVesselItem(new Item.Properties()
                    .stacksTo(1)));
    public static final RegistryObject<Item> DISCORD_ESSENCE = createRegularDescriptionItem("discord_essence");
    public static final RegistryObject<Item> ACTIVATOR_CLEF = ITEMS.register("activator_clef",
            () -> new ActivatorClefItem(new Item.Properties()));
    public static final RegistryObject<Item> DISCORD_BOMB = ITEMS.register("discord_bomb",
            () -> new DiscordBombItem(new Item.Properties()));
    public static final RegistryObject<Item> TRANSMUTED_VOICE = ITEMS.register("transmuted_voice",
            () -> new TransmutedVoiceItem(new Item.Properties()));
    public static final RegistryObject<Item> MUSIC_BOTTLE = createRegularDescriptionItem("music_bottle");
    public static final RegistryObject<Item> FRUIT_OF_LIFE = ITEMS.register("fruit_of_life",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)) {
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("item.faunaandorchestra.fruit_of_life.desc").withStyle(ChatFormatting.GRAY));
                    super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
                }

            });
    public static final RegistryObject<Item> PETALS_OF_DEATH = ITEMS.register("petals_of_death",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)) {
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("item.faunaandorchestra.petals_of_death.desc").withStyle(ChatFormatting.GRAY));
                    super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
                }
            });
    public static final RegistryObject<Item> SHEET_FRAGMENTS = ITEMS.register("sheet_fragments",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)) {
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("item.faunaandorchestra.sheet_fragments.desc")
                            .withStyle(ChatFormatting.GRAY));
                    super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
                }
            });
    public static final RegistryObject<Item> RINGTAILS_POSTER = ITEMS.register("ringtails_poster",
            () -> new RingtailsPosterItem(new Item.Properties().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> MUSICAL_INK = ITEMS.register("musical_ink",
            () -> new MusicalInkItem(new Item.Properties()));
    public static final RegistryObject<Item> GLOVE = ITEMS.register("glove",
            () -> new Item(new Item.Properties().durability(3)));

    public static final RegistryObject<Item> MANTIS_SPAWN_EGG = ITEMS.register("mantis_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.MANTIS, 0x46eb4c, 0x23a628,
                    new Item.Properties()));
    public static final RegistryObject<Item> PENGUIN_SPAWN_EGG = ITEMS.register("penguin_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.PENGUIN, 0xd7d7d9, 0x0e0e1a,
                    new Item.Properties()));
    public static final RegistryObject<Item> RED_PANDA_SPAWN_EGG = ITEMS.register("red_panda_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.RED_PANDA, 0xd63200, 0xd1d0cf,
                    new Item.Properties()));
    public static final RegistryObject<Item> MACAW_SPAWN_EGG = ITEMS.register("macaw_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.MACAW, 0x002196, 0xffea00,
                    new Item.Properties()));
    public static final RegistryObject<Item> BEAVER_SPAWN_EGG = ITEMS.register("beaver_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.BEAVER, 0x170a00, 0x401e03,
                    new Item.Properties()));
    public static final RegistryObject<Item> LEMUR_SPAWN_EGG = ITEMS.register("lemur_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.LEMUR, 0x322a29, 0xc1c1c1,
                    new Item.Properties()));
    public static final RegistryObject<Item> MADAME_BUTTERFLY_SPAWN_EGG = ITEMS.register("madame_butterfly_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.MADAME_BUTTERFLY, 0x23a2e3, 0x02050d,
                    new Item.Properties()));
    public static final RegistryObject<Item> QUIRKY_FROG_SPAWN_EGG = ITEMS.register("quirky_frog_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.QUIRKY_FROG, 0x245715, 0xbfbd82,
                    new Item.Properties()));
    public static final RegistryObject<Item> WANDERING_KOALA_SPAWN_EGG = ITEMS.register("wandering_koala_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.WANDERING_KOALA, 0xb5b5b5, 0x707070,
                    new Item.Properties()));
    public static final RegistryObject<Item> SPROUTLING_SPAWN_EGG = ITEMS.register("sproutling_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.SINGING_SPROUTLING, 0xd1793a, 0x33a03e,
                    new Item.Properties()));
    public static final RegistryObject<Item> BUTTERFLY_SPAWN_EGG = ITEMS.register("butterfly_spawn_egg",
            () -> new CustomSpawnEggItem(new Item.Properties(), ModEntities.BUTTERFLY));
    public static final RegistryObject<Item> WISE_TREE_SPAWN_EGG = ITEMS.register("wise_tree_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.WISE_TREE, 0x5f4a2b, 0x567e22,
                    new Item.Properties()));
    public static final RegistryObject<Item> RINGTAILS_SPAWN_EGG = ITEMS.register("ringtails_spawn_egg",
            () -> new CustomSpawnEggItem(new Item.Properties(), ModEntities.FAUST, ModEntities.ORION) {
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("entity.faunaandorchestra.faust")
                            .append(Component.literal(" & "))
                            .append(Component.translatable("entity.faunaandorchestra.orion"))
                            .withStyle(ChatFormatting.GRAY));
                    super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
                }
            });
    public static final RegistryObject<Item> THE_GREAT_COMPOSER_SPAWN_EGG = ITEMS.register("the_great_composer_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.THE_GREAT_COMPOSER, 0xe2d7a8, 0x23ab53, new Item.Properties()) {
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("item.faunaandorchestra.the_great_composer_spawn_egg.desc")
                            .withStyle(ChatFormatting.RED));
                    super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
                }
            });

    public static final RegistryObject<Item> BACH_AIR_SHEET_MUSIC = createSheetMusic("bach_air_sheet_music");
    public static final RegistryObject<Item> GREENSLEEVES_SHEET_MUSIC = createSheetMusic("greensleeves_sheet_music");
    public static final RegistryObject<Item> BLUES_SHEET_MUSIC = createSheetMusic("blues_sheet_music");
    public static final RegistryObject<Item> JAZZY_FUR_ELISE_SHEET_MUSIC = createSheetMusic("jazzy_fur_elise_sheet_music");
    public static final RegistryObject<Item> DANCE_OF_THE_LITTLE_SWANS = createSheetMusic("dance_of_the_little_swans_sheet_music");
    public static final RegistryObject<Item> LA_BAMBA_SHEET_MUSIC = createSheetMusic("la_bamba_sheet_music");
    public static final RegistryObject<Item> RESURRECTION_SONG = createLegendarySheetMusic("resurrection_song");

    public static final RegistryObject<Item> THE_GREAT_HEAD_ITEM = ITEMS.register("the_great_head_item",
            () -> new TheGreatHeadItem(ModBlocks.THE_GREAT_HEAD.get(), new Item.Properties().rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> MELOMANCY_CAULDRON_ITEM = ITEMS.register("melomancy_cauldron_item",
            () -> new MelomancyCauldronItem(ModBlocks.MELOMANCY_CAULDRON.get(), new Item.Properties()));
    public static final RegistryObject<Item> DISCORD_NUCLEI_ITEM = ITEMS.register("discord_nuclei_item",
            () -> new DiscordNucleiItem(ModBlocks.DISCORD_NUCLEI.get(), new Item.Properties()));

    // MISC
    public static final RegistryObject<Item> VOICE = ITEMS.register("voice",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> UNLOCKER = ITEMS.register("unlocker",
            () -> new Item(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)) {
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("item.faunaandorchestra.unlocker.desc"));
                    super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
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
                        ModAdvancements.KILL_COMPOSER.trigger(serverPlayer);
                        ModAdvancements.WISE_TREE.trigger(serverPlayer);
                        ModAdvancements.MEET_RINGTAILS.trigger(serverPlayer);
                        ModAdvancements.USE_DISCORD_BOMB.trigger(serverPlayer);
                    }
                    return super.use(level, player, usedHand);
                }
            });
    public static final RegistryObject<Item> ICON = ITEMS.register("icon",
            () -> new Item(new Item.Properties().stacksTo(1)));

    private static RegistryObject<Item> createSheetMusic(String name) {
        return ITEMS.register(name, () -> new Item(new Item.Properties().rarity(Rarity.RARE)) {
            @Override
            public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                MutableComponent instruments = Component.empty();
                Iterator<Item> iterator = MusicUtil.getInstruments(this.asItem()).iterator();
                while (iterator.hasNext()) {
                    instruments.append(Component.translatable(iterator.next().getDescriptionId()));
                    if (iterator.hasNext()) {
                        instruments.append(Component.literal(", "));
                    }
                }
                tooltipComponents.add(instruments.withStyle(ChatFormatting.GRAY));
                super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
            }
        });
    }

    private static RegistryObject<Item> createLegendarySheetMusic(String name) {
        return ITEMS.register(name,
                () -> new Item(new Item.Properties().rarity(Rarity.EPIC)) {
                    @Override
                    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
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

                        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
                    }
                });
    }

    private static RegistryObject<Item> createRegularDescriptionItem(String name) {
        return ITEMS.register(name,
                () -> new Item(new Item.Properties()) {
                    @Override
                    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                        tooltipComponents.add(Component.translatable("item.faunaandorchestra." + name + ".desc")
                                .withStyle(ChatFormatting.GRAY));
                        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
                    }
                });
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}

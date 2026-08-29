package net.migueel26.faunaandorchestra.item;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.block.ModBlocks;
import net.migueel26.faunaandorchestra.entity.ModEntities;
import net.migueel26.faunaandorchestra.item.custom.*;
import net.migueel26.faunaandorchestra.item.custom.InstrumentItem;
import net.migueel26.faunaandorchestra.item.custom.armor.FloralBootsItem;
import net.migueel26.faunaandorchestra.item.custom.armor.FluffyBootsItem;
import net.migueel26.faunaandorchestra.item.custom.armor.ModArmorMaterials;
import net.migueel26.faunaandorchestra.item.custom.clothing.CosmeticItem;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.migueel26.faunaandorchestra.util.AdvancementUtil;
import net.migueel26.faunaandorchestra.util.ModTags;
import net.migueel26.faunaandorchestra.util.MusicUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.Foods;
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
import java.util.function.Supplier;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, FaunaAndOrchestra.MOD_ID);

    // INSTRUMENTS
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
    public static final RegistryObject<Item> DRUM = ITEMS.register("drum",
            () -> new GeoInstrumentItem(new Item.Properties().stacksTo(1), ModSounds.DRUM_USE.get()));
    public static final RegistryObject<Item> PAN_FLUTE = ITEMS.register("pan_flute",
            () -> new PanFluteItem(new Item.Properties()
                    .stacksTo(1)
                    .durability(120)));

    public static final RegistryObject<Item> PAN_FLUTE_CREATIVE = ITEMS.register("pan_flute_creative",
            () -> new PanFluteItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> BATON = ITEMS.register("baton",
            () -> new BatonItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> LEGENDARY_BATON = ITEMS.register("legendary_baton",
            () -> new BatonItem(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));

    // REGULAR MUSICAL ITEMS
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
    public static final RegistryObject<Item> BIOSONIC_INGOT = ITEMS.register("biosonic_ingot",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BIOSONIC_DIRK = ITEMS.register("biosonic_dirk",
            () -> new BiosonicDirkItem(new Item.Properties().durability(16)));
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
    public static final RegistryObject<Item> SOUND_SENSOR = ITEMS.register("sound_sensor",
            () -> new SoundSensorItem(new Item.Properties().stacksTo(1).durability(64)));
    public static final RegistryObject<Item> FROG_FLUTE = ITEMS.register("frog_flute",
            () -> new FrogFluteItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> FORTUNE_COOKIE = ITEMS.register("fortune_cookie",
            () -> new FortuneCookieItem(new Item.Properties().rarity(Rarity.UNCOMMON)
                    .food(ModFoodProperties.FORTUNE_COOKIE)));
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
    public static final RegistryObject<Item> EXTRACT_OF_LIVING_MUSIC = createRegularDescriptionItem("extract_of_living_music");
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

    // ANIMAL DROPS AND NATURAL RECIPES
    public static final RegistryObject<Item> MANTIS_CLAW = ITEMS.register("mantis_claw",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> MANTIS_DAGGER = ITEMS.register("mantis_dagger",
            () -> new MantisDaggerItem(new Item.Properties().durability(250)));
    public static final RegistryObject<Item> MANTIS_FOOD = createRegularDescriptionItem("mantis_food");
    public static final RegistryObject<Item> WORM = ITEMS.register("worm",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> INSECT = ITEMS.register("insect",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> NATURE_FUMES = ITEMS.register("nature_fumes",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ARCTIC_FUMES = ITEMS.register("arctic_fumes",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> FLORAL_FUMES = ITEMS.register("floral_fumes",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SONIC_FUMES = ITEMS.register("sonic_fumes",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BADGRASS_MEAL = ITEMS.register("badgrass_meal",
            () -> new BadgrassMealItem(new Item.Properties()));
    public static final RegistryObject<Item> EVERFRUIT = ITEMS.register("everfruit",
            () -> new EverfruitItem(new Item.Properties().rarity(Rarity.RARE).food(Foods.GOLDEN_APPLE)));
    public static final RegistryObject<Item> EVERJELLY = ITEMS.register("everjelly",
            () -> new Item(new Item.Properties().rarity(Rarity.RARE).food(Foods.GOLDEN_APPLE)));
    public static final RegistryObject<Item> FLOATING_BLOSSOM = ITEMS.register("floating_blossom",
            () -> new FloatingBlossomItem(new Item.Properties()));
    public static final RegistryObject<Item> SHARP_BAMBOO = ITEMS.register("sharp_bamboo",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PERFUMED_BAMBOO = createRegularDescriptionItem("perfumed_bamboo");
    public static final RegistryObject<Item> PENGUIN_FEATHER = ITEMS.register("penguin_feather",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TAIYAKI = createRegularDescriptionItem("taiyaki", new Item.Properties().food(Foods.PUMPKIN_PIE));
    public static final RegistryObject<Item> MACAW_FEATHER = createRegularDescriptionItem("macaw_feather");
    public static final RegistryObject<Item> WRITING_INK = ITEMS.register("writing_ink",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BUSINESS_CARD = createRegularDescriptionItem("business_card", new Item.Properties().stacksTo(1).rarity(Rarity.EPIC));
    public static final RegistryObject<Item> PROP_CASE = ITEMS.register("prop_case",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    if (Screen.hasShiftDown()) {
                        tooltipComponents.add(Component.translatable("item.faunaandorchestra.prop_case.desc"));
                    } else {
                        tooltipComponents.add(Component.translatable("tooltip.faunaandorchestra.shift"));
                    }

                    super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
                }
            });
    public static final RegistryObject<Item> COTTON = ITEMS.register("cotton",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> COTTON_PLANT_SEEDS = ITEMS.register("cotton_seeds",
            () -> new ItemNameBlockItem(ModBlocks.COTTON_PLANT.get(), new Item.Properties()));
    public static final RegistryObject<Item> SEEDY_APPLE = ITEMS.register("seedy_apple",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> FLORA_FORTA = createRegularDescriptionItem("flora_forta");
    public static final RegistryObject<Item> UNLIT_MASK = createRegularDescriptionItem("unlit_mask", new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
    public static final RegistryObject<Item> REALLY_GOOD_STICK = createRegularDescriptionItem("really_good_stick");
    public static final RegistryObject<Item> MUSIC_JAM = createRegularDescriptionItem("music_jam", new Item.Properties().food(Foods.MUSHROOM_STEW));

    // ARMOR
    // ARMOR
    public static final RegistryObject<Item> FLUFFY_BOOTS = ITEMS.register("fluffy_boots",
            () -> new FluffyBootsItem(ModArmorMaterials.PENGUIN_FEATHER, ArmorItem.Type.BOOTS, new Item.Properties()));
    public static final RegistryObject<Item> FLORAL_BOOTS = ITEMS.register("floral_boots",
            () -> new FloralBootsItem(ModArmorMaterials.FLORA_FORTA, ArmorItem.Type.BOOTS, new Item.Properties().rarity(Rarity.RARE)));

    // KITS
    public static final RegistryObject<Item> SEWING_KIT =  createKoalaKit("sewing_kit", ModEntities.TAILOR_KOALA);
    public static final RegistryObject<Item> MELOMANCY_KIT =  createKoalaKit("melomancy_kit", ModEntities.MELOMANCER_KOALA);
    public static final RegistryObject<Item> FARMING_KIT =  createKoalaKit("farming_kit", ModEntities.FARMER_KOALA);

    // CLOTHING
    public static final RegistryObject<Item> WHITE_TUXEDO = createClothingItem("white_tuxedo", ModTags.EntityTypes.WEARS_WHITE_TUXEDO);
    public static final RegistryObject<Item> TUXEDO = createClothingItem("tuxedo", ModTags.EntityTypes.WEARS_TUXEDO);
    public static final RegistryObject<Item> TAILCOAT = createClothingItem("tailcoat", ModTags.EntityTypes.WEARS_TAILCOAT);
    public static final RegistryObject<Item> BASEBALL_JACKET = createClothingItem("baseball_jacket", ModTags.EntityTypes.WEARS_BASEBALL_JACKET);
    public static final RegistryObject<Item> SANTA_COSTUME = createClothingItem("santa_costume", ModTags.EntityTypes.WEARS_SANTA_COSTUME);
    public static final RegistryObject<Item> RIGHT_MONOCLE = createHeadwearItem("right_monocle", ModTags.EntityTypes.WEARS_RIGHT_MONOCLE);
    public static final RegistryObject<Item> LEFT_MONOCLE = createHeadwearItem("left_monocle", ModTags.EntityTypes.WEARS_LEFT_MONOCLE);
    public static final RegistryObject<Item> FAKE_MOUSTACHE = createHeadwearItem("fake_moustache", ModTags.EntityTypes.WEARS_FAKE_MOUSTACHE);
    public static final RegistryObject<Item> ROSE = createHeadwearItem("rose", ModTags.EntityTypes.WEARS_ROSE);
    public static final RegistryObject<Item> IMAGINAL_DISK = createHeadwearItem("imaginal_disk", ModTags.EntityTypes.WEARS_IMAGINAL_DISK, Rarity.RARE);
    public static final RegistryObject<Item> MASK_OF_THE_ENLIGHTENED = createHeadwearItem("mask_of_the_enlightened", ModTags.EntityTypes.WEARS_MASK_OF_THE_ENLIGHTENED, Rarity.RARE);
    public static final RegistryObject<Item> PROPELLER_HAT = create3dHeadwearItem("propeller_hat", ModTags.EntityTypes.WEARS_PROPELLER_HAT, Rarity.RARE);
    public static final RegistryObject<Item> PHANTOM_MASK = createHeadwearItem("phantom_mask", ModTags.EntityTypes.WEARS_PHANTOM_MASK, Rarity.RARE);
    public static final RegistryObject<Item> TOP_HAT = create3dHeadwearItem("top_hat", ModTags.EntityTypes.WEARS_TOP_HAT);
    public static final RegistryObject<Item> SANTA_HAT = create3dHeadwearItem("santa_hat", ModTags.EntityTypes.WEARS_SANTA_HAT);
    public static final RegistryObject<Item> BASEBALL_CAP = create3dHeadwearItem("baseball_cap", ModTags.EntityTypes.WEARS_BASEBALL_CAP);
    public static final RegistryObject<Item> SILVER_TINT = createClothingItem("silver_tint", ModTags.EntityTypes.WEARS_SILVER_TINT);
    public static final RegistryObject<Item> GOLDEN_TINT = createClothingItem("golden_tint", ModTags.EntityTypes.WEARS_GOLDEN_TINT);
    public static final RegistryObject<Item> COLORFUL_TINT = createClothingItem("colorful_tint", ModTags.EntityTypes.WEARS_COLORFUL_TINT);

    // SPAWN EGGS
    public static final RegistryObject<Item> MANTIS_SPAWN_EGG = ITEMS.register("mantis_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.MANTIS, 0x46eb4c, 0x23a628,
                    new Item.Properties()));
    public static final RegistryObject<Item> PENGUIN_SPAWN_EGG = ITEMS.register("penguin_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.PENGUIN, 0xd7d7d9, 0x0e0e1a,
                    new Item.Properties()));
    public static final RegistryObject<Item> EMPEROR_PENGUIN_SPAWN_EGG = ITEMS.register("emperor_penguin_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.EMPEROR_PENGUIN, 0x0e0e1a, 0xd7d7d9,
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
    public static final RegistryObject<Item> SEA_LION_SPAWN_EGG = ITEMS.register("sea_lion_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.SEA_LION, 0x463627, 0x977d6b,
                    new Item.Properties()));
    public static final RegistryObject<Item> QUIRKY_FROG_SPAWN_EGG = ITEMS.register("quirky_frog_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.QUIRKY_FROG, 0x245715, 0xbfbd82,
                    new Item.Properties()));
    public static final RegistryObject<Item> WANDERING_KOALA_SPAWN_EGG = ITEMS.register("wandering_koala_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.WANDERING_KOALA, 0xb5b5b5, 0x707070,
                    new Item.Properties()));
    public static final RegistryObject<Item> BUTLER_KOALA_SPAWN_EGG = ITEMS.register("butler_koala_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.BUTLER_KOALA, 0xb5b5b5, 0x707070,
                    new Item.Properties()));
    public static final RegistryObject<Item> WORKER_KOALA_SPAWN_EGG = ITEMS.register("worker_koala_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.WORKER_KOALA, 0xb5b5b5, 0x707070,
                    new Item.Properties()));
    public static final RegistryObject<Item> SPROUTLING_SPAWN_EGG = ITEMS.register("sproutling_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.SINGING_SPROUTLING, 0xd1793a, 0x33a03e,
                    new Item.Properties()));
    public static final RegistryObject<Item> LIVING_MUSIC_SPAWN_EGG = ITEMS.register("living_music_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.LIVING_MUSIC, 0xA375FF, 0x6986FF,
                    new Item.Properties()));
    public static final RegistryObject<Item> BUTTERFLY_SPAWN_EGG = ITEMS.register("butterfly_spawn_egg",
            () -> new CustomSpawnEggItem(new Item.Properties(), ModEntities.BUTTERFLY));
    public static final RegistryObject<Item> WISE_TREE_SPAWN_EGG = ITEMS.register("wise_tree_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.WISE_TREE, 0x5f4a2b, 0x567e22,
                    new Item.Properties()));
    public static final RegistryObject<Item> RINGTAILS_SPAWN_EGG = ITEMS.register("ringtails_spawn_egg",
            () -> new CustomSpawnEggItem(new Item.Properties(), ModEntities.FAUST, ModEntities.ORION));
    public static final RegistryObject<Item> JAZZY_DAMMYS_SPAWN_EGG = ITEMS.register("jazzy_dammys_spawn_egg",
            () -> new CustomSpawnEggItem(new Item.Properties(), ModEntities.DENZEL, ModEntities.DENISE, ModEntities.DELROY, ModEntities.DAN_B));
    public static final RegistryObject<Item> THE_GREAT_COMPOSER_SPAWN_EGG = ITEMS.register("the_great_composer_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.THE_GREAT_COMPOSER, 0xe2d7a8, 0x23ab53, new Item.Properties()) {
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("item.faunaandorchestra.the_great_composer_spawn_egg.desc")
                            .withStyle(ChatFormatting.RED));
                    super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
                }
            });

    // SHEET MUSIC
    public static final RegistryObject<Item> BACH_AIR_SHEET_MUSIC = createSheetMusic("bach_air_sheet_music");
    public static final RegistryObject<Item> GREENSLEEVES_SHEET_MUSIC = createSheetMusic("greensleeves_sheet_music");
    public static final RegistryObject<Item> BLUES_SHEET_MUSIC = createSheetMusic("blues_sheet_music");
    public static final RegistryObject<Item> JAZZY_FUR_ELISE_SHEET_MUSIC = createSheetMusic("jazzy_fur_elise_sheet_music");
    public static final RegistryObject<Item> DANCE_OF_THE_LITTLE_SWANS = createSheetMusic("dance_of_the_little_swans_sheet_music");
    public static final RegistryObject<Item> LA_BAMBA_SHEET_MUSIC = createSheetMusic("la_bamba_sheet_music");
    public static final RegistryObject<Item> SAINTS_SHEET_MUSIC = createSheetMusic("saints_sheet_music");
    public static final RegistryObject<Item> OH_SUSANNA_SHEET_MUSIC = createSheetMusic("oh_susanna_sheet_music");
    public static final RegistryObject<Item> THE_ENTERTAINER_SHEET_MUSIC = createSheetMusic("the_entertainer_sheet_music");
    public static final RegistryObject<Item> RESURRECTION_SONG = createLegendarySheetMusic("resurrection_song");

    // BLOCK ITEMS
    public static final RegistryObject<Item> THE_GREAT_HEAD_ITEM = ITEMS.register("the_great_head_item",
            () -> new TheGreatHeadItem(ModBlocks.THE_GREAT_HEAD.get(), new Item.Properties().rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> MELOMANCY_CAULDRON_ITEM = ITEMS.register("melomancy_cauldron_item",
            () -> new MelomancyCauldronItem(ModBlocks.MELOMANCY_CAULDRON.get(), new Item.Properties()));
    public static final RegistryObject<Item> DISCORD_NUCLEI_ITEM = ITEMS.register("discord_nuclei_item",
            () -> new DiscordNucleiItem(ModBlocks.DISCORD_NUCLEI.get(), new Item.Properties()));
    public static final RegistryObject<Item> MOTHER_STATUE_ITEM = ITEMS.register("mother_statue_item",
            () -> new GeoBlockItem(ModBlocks.MOTHER_STATUE.get(), new Item.Properties()));
    public static final RegistryObject<Item> BAMBOO_TRAP_ITEM = ITEMS.register("bamboo_trap_item",
            () -> new GeoBlockItem(ModBlocks.BAMBOO_TRAP.get(), new Item.Properties()));
    public static final RegistryObject<Item> BEAVER_STATUE_ITEM = ITEMS.register("beaver_statue_item",
            () -> new GeoBlockItem(ModBlocks.BEAVER_STATUE.get(), new Item.Properties()));
    public static final RegistryObject<Item> SEWING_MACHINE_ITEM = ITEMS.register("sewing_machine_item",
            () -> new GeoBlockItem(ModBlocks.SEWING_MACHINE.get(), new Item.Properties()));
    public static final RegistryObject<Item> MAILBOX_ITEM = ITEMS.register("mailbox_item",
            () -> new GeoBlockItem(ModBlocks.MAILBOX.get(), true, new Item.Properties()));

    // MISC
    public static final RegistryObject<Item> VOICE = ITEMS.register("voice",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SEWING_RECIPE = createRegularDescriptionItem("sewing_recipe", new Item.Properties().rarity(Rarity.UNCOMMON));
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
                        AdvancementUtil.unlock(serverPlayer);
                        return InteractionResultHolder.success(player.getItemInHand(usedHand));
                    }
                    return super.use(level, player, usedHand);
                }
            });

    // ICONS
    public static final RegistryObject<Item> ICON = ITEMS.register("icon",
            () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> FIRE_ICON = ITEMS.register("fire_icon",
            () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> SOUL_FIRE_ICON = ITEMS.register("soul_fire_icon",
            () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> WATER_ICON = ITEMS.register("water_icon",
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

    private static RegistryObject<Item> createRegularDescriptionItem(String name, Item.Properties properties) {
        return ITEMS.register(name,
                () -> new Item(properties) {
                    @Override
                    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                        tooltipComponents.add(Component.translatable("item.faunaandorchestra." + name + ".desc")
                                .withStyle(ChatFormatting.GRAY));
                        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
                    }
                });
    }

    private static RegistryObject<Item> createClothingItem(String name, TagKey<EntityType<?>> tag) {
        return ITEMS.register(name,
                () -> new Item(new Item.Properties()) {
                    @Override
                    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
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

    private static RegistryObject<Item> create3dHeadwearItem(String name, TagKey<EntityType<?>> tag) {
        return ITEMS.register(name,
                () -> new CosmeticItem(new Item.Properties()) {
                    @Override
                    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                        tooltipComponents.add(Component.translatable("item.faunaandorchestra_headwear.desc").withStyle(ChatFormatting.LIGHT_PURPLE));

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

    private static RegistryObject<Item> createHeadwearItem(String name, TagKey<EntityType<?>> tag) {
        return ITEMS.register(name,
                () -> new Item(new Item.Properties()) {
                    @Override
                    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                        tooltipComponents.add(Component.translatable("item.faunaandorchestra_headwear.desc").withStyle(ChatFormatting.LIGHT_PURPLE));

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

    private static RegistryObject<Item> createHeadwearItem(String name, TagKey<EntityType<?>> tag, Rarity rarity) {
        return ITEMS.register(name,
                () -> new Item(new Item.Properties().rarity(rarity)) {
                    @Override
                    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                        tooltipComponents.add(Component.translatable("item.faunaandorchestra_headwear.desc").withStyle(ChatFormatting.LIGHT_PURPLE));

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

                    @Override
                    public boolean canEquip(ItemStack stack, EquipmentSlot armorType, Entity entity) {
                        if (armorType == EquipmentSlot.HEAD) return true;
                        return super.canEquip(stack, armorType, entity);
                    }
                });
    }

    private static RegistryObject<Item> create3dHeadwearItem(String name, TagKey<EntityType<?>> tag, Rarity rarity) {
        return ITEMS.register(name,
                () -> new CosmeticItem(new Item.Properties().rarity(rarity)) {
                    @Override
                    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                        tooltipComponents.add(Component.translatable("item.faunaandorchestra_headwear.desc").withStyle(ChatFormatting.LIGHT_PURPLE));

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

    private static RegistryObject<Item> createKoalaKit(String name, Supplier<? extends EntityType<?>> entityType) {
        return ITEMS.register(name,
                () -> new Item(new Item.Properties()) {
                    @Override
                    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                        tooltipComponents.add(Component.translatable("item.faunaandorchestra.kit.desc").withStyle(ChatFormatting.GRAY)
                                .append(" ")
                                .append("§e" + entityType.get().getDescription().getString()));
                        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
                    }
                });
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}

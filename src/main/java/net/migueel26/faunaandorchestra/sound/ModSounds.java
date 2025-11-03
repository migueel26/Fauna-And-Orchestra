package net.migueel26.faunaandorchestra.sound;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, FaunaAndOrchestra.MOD_ID);

    public static final Supplier<SoundEvent> VIOLIN_USE = registerSoundEvent("violin_use");
    public static final Supplier<SoundEvent> FLUTE_USE = registerSoundEvent("flute_use");
    public static final Supplier<SoundEvent> KEYTAR_USE = registerSoundEvent("keytar_use");
    public static final Supplier<SoundEvent> DOUBLE_BASS_USE = registerSoundEvent("double_bass_use");
    public static final Supplier<SoundEvent> SAXOPHONE_USE = registerSoundEvent("saxophone_use");
    public static final Supplier<SoundEvent> OBOE_USE = registerSoundEvent("oboe_use");
    public static final Supplier<SoundEvent> CELLO_USE = registerSoundEvent("cello_use");
    public static final Supplier<SoundEvent> PAN_FLUTE_USE = registerSoundEvent("pan_flute_use");

    public static final Supplier<SoundEvent> PAN_FLUTE_NOTES = registerSoundEvent("pan_flute_notes");
    public static final Supplier<SoundEvent> PAN_FLUTE_PUSH = registerSoundEvent("pan_flute_push");
    public static final Supplier<SoundEvent> PAN_FLUTE_PUSH_WIND = registerSoundEvent("pan_flute_push_wind");
    public static final Supplier<SoundEvent> PAN_FLUTE_HEALTH = registerSoundEvent("pan_flute_health");
    public static final Supplier<SoundEvent> PAN_FLUTE_WIND = registerSoundEvent("pan_flute_wind");
    public static final Supplier<SoundEvent> PAN_FLUTE_WIND_IMPULSE = registerSoundEvent("pan_flute_wind_impulse");
    public static final Supplier<SoundEvent> PAN_FLUTE_NATURE = registerSoundEvent("pan_flute_nature");
    public static final Supplier<SoundEvent> PAN_FLUTE_CHANGE = registerSoundEvent("pan_flute_change");

    public static final Supplier<SoundEvent> BACH_AIR_VIOLIN = registerSoundEvent("bach_air_violin");
    public static final Supplier<SoundEvent> BACH_AIR_FLUTE = registerSoundEvent("bach_air_flute");
    public static final Supplier<SoundEvent> BACH_AIR_KEYTAR = registerSoundEvent("bach_air_keytar");
    public static final Supplier<SoundEvent> BACH_AIR_DOUBLE_BASS = registerSoundEvent("bach_air_double_bass");

    public static final Supplier<SoundEvent> GREENSLEEVES_VIOLIN = registerSoundEvent("greensleeves_violin");
    public static final Supplier<SoundEvent> GREENSLEEVES_FLUTE = registerSoundEvent("greensleeves_flute");
    public static final Supplier<SoundEvent> GREENSLEEVES_KEYTAR = registerSoundEvent("greensleeves_keytar");

    public static final Supplier<SoundEvent> BLUES_SAXOPHONE = registerSoundEvent("blues_saxophone");
    public static final Supplier<SoundEvent> BLUES_DOUBLE_BASS = registerSoundEvent("blues_double_bass");
    public static final Supplier<SoundEvent> BLUES_KEYTAR = registerSoundEvent("blues_keytar");

    public static final Supplier<SoundEvent> FUR_ELISE_SAXOPHONE = registerSoundEvent("fur_elise_saxophone");
    public static final Supplier<SoundEvent> FUR_ELISE_FLUTE = registerSoundEvent("fur_elise_flute");
    public static final Supplier<SoundEvent> FUR_ELISE_KEYTAR = registerSoundEvent("fur_elise_keytar");

    public static final Supplier<SoundEvent> SWANS_OBOE = registerSoundEvent("swans_oboe");
    public static final Supplier<SoundEvent> SWANS_FLUTE = registerSoundEvent("swans_flute");
    public static final Supplier<SoundEvent> SWANS_KEYTAR = registerSoundEvent("swans_keytar");

    public static final Supplier<SoundEvent> RESURRECTION_KEYTAR = registerSoundEvent("resurrection_keytar");
    public static final Supplier<SoundEvent> RESURRECTION_OBOE = registerSoundEvent("resurrection_oboe");
    public static final Supplier<SoundEvent> RESURRECTION_CELLO = registerSoundEvent("resurrection_cello");
    public static final Supplier<SoundEvent> RESURRECTION_VIOLIN = registerSoundEvent("resurrection_violin");
    public static final Supplier<SoundEvent> RESURRECTION_FLUTE = registerSoundEvent("resurrection_flute");

    public static final Supplier<SoundEvent> BAMBA_CELLO = registerSoundEvent("bamba_cello");
    public static final Supplier<SoundEvent> BAMBA_DOUBLE_BASS = registerSoundEvent("bamba_double_bass");
    public static final Supplier<SoundEvent> BAMBA_FLUTE = registerSoundEvent("bamba_flute");
    public static final Supplier<SoundEvent> BAMBA_KEYTAR = registerSoundEvent("bamba_keytar");
    public static final Supplier<SoundEvent> BAMBA_SAXOPHONE = registerSoundEvent("bamba_saxophone");
    public static final Supplier<SoundEvent> BAMBA_VIOLIN = registerSoundEvent("bamba_violin");

    public static final Supplier<SoundEvent> FROG_SONG = registerSoundEvent("frog_song");
    public static final Supplier<SoundEvent> SPROUTLING_SONG = registerSoundEvent("sproutling_song");
    public static final Supplier<SoundEvent> RINGTAILS_SONG = registerSoundEvent("hona_bildots_eztia");
    public static final Supplier<SoundEvent> THE_GREAT_COMPOSER_THEME = registerSoundEvent("the_great_composer_theme");
    public static final Supplier<SoundEvent> THE_GREAT_COMPOSER_FINAL_THEME = registerSoundEvent("the_great_composer_theme2");

    public static final Supplier<SoundEvent> MANTIS_AMBIENT = registerSoundEvent("mantis_ambient");
    public static final Supplier<SoundEvent> MANTIS_ANGRY = registerSoundEvent("mantis_angry");

    public static final Supplier<SoundEvent> BABY_PENGUIN_AMBIENT = registerSoundEvent("baby_penguin_ambient");

    public static final Supplier<SoundEvent> WISE_TREE_AMBIENT = registerSoundEvent("wise_tree_ambient");
    public static final Supplier<SoundEvent> WISE_TREE_DROP = registerSoundEvent("wise_tree_drop");

    public static final Supplier<SoundEvent> CANON_ATTACK = registerSoundEvent("canon_attack");
    public static final Supplier<SoundEvent> CANON_DEATH = registerSoundEvent("canon_death");
    public static final Supplier<SoundEvent> CANON_HURT = registerSoundEvent("canon_hurt");
    public static final Supplier<SoundEvent> CANON_SPAWN = registerSoundEvent("canon_spawn");

    public static final Supplier<SoundEvent> ATTACK_CANON = registerSoundEvent("attack_canon");
    public static final Supplier<SoundEvent> ATTACK_HEADLESS = registerSoundEvent("attack_headless");
    public static final Supplier<SoundEvent> ATTACK_LAUGH = registerSoundEvent("attack_laugh");
    public static final Supplier<SoundEvent> ATTACK_MELEE = registerSoundEvent("attack_melee");
    public static final Supplier<SoundEvent> ATTACK_NORMAL = registerSoundEvent("attack_normal");
    public static final Supplier<SoundEvent> ATTACK_POISON = registerSoundEvent("attack_poison");
    public static final Supplier<SoundEvent> ATTACK_SUMMON = registerSoundEvent("attack_summon");
    public static final Supplier<SoundEvent> FAKE_DYING = registerSoundEvent("dying1");
    public static final Supplier<SoundEvent> DYING = registerSoundEvent("dying2");
    public static final Supplier<SoundEvent> PREPARE = registerSoundEvent("prepare");
    public static final Supplier<SoundEvent> REPEL = registerSoundEvent("repel");
    public static final Supplier<SoundEvent> RESURRECT = registerSoundEvent("resurrect");
    public static final Supplier<SoundEvent> ELECTRIC_SHOCK = registerSoundEvent("electric_shock");
    public static final Supplier<SoundEvent> SHOCK = registerSoundEvent("shock");
    public static final Supplier<SoundEvent> SPAWN = registerSoundEvent("spawn");
    public static final Supplier<SoundEvent> WEAK = registerSoundEvent("weak");

    public static final Supplier<SoundEvent> DISCORDED_FLOWER_EAT = registerSoundEvent("discorded_flower_eat");

    public static final Supplier<SoundEvent> SUCCESSFUL_TAME = registerSoundEvent("successful_tame");
    public static final Supplier<SoundEvent> MAGIC_GROWTH = registerSoundEvent("magic_growth");
    public static final Supplier<SoundEvent> SINGING_SPROUTLING_SOUND = registerSoundEvent("singing_sproutling_sound");
    public static final Supplier<SoundEvent> SPROUTLING_TWINKLE = registerSoundEvent("sproutling_twinkle");
    public static final Supplier<SoundEvent> WHISTLE_CALL = registerSoundEvent("whistle_call");
    public static final Supplier<SoundEvent> CAULDRON_BUBBLING = registerSoundEvent("cauldron_bubbling");
    public static final Supplier<SoundEvent> CAULDRON_ITEM = registerSoundEvent("cauldron_item");
    public static final Supplier<SoundEvent> VESSEL_CLICK = registerSoundEvent("vessel_click");
    public static final Supplier<SoundEvent> VESSEL_AIR = registerSoundEvent("vessel_air");
    public static final Supplier<SoundEvent> VESSEL_COLLECT = registerSoundEvent("vessel_collect");
    public static final Supplier<SoundEvent> PAN_FLUTE_ALTAR_THUNDER = registerSoundEvent("pan_flute_altar_thunder");
    public static final Supplier<SoundEvent> PAN_FLUTE_ALTAR_QUAKE = registerSoundEvent("pan_flute_altar_quake");
    public static final Supplier<SoundEvent> BOOGIE_BOMB_DANCE = registerSoundEvent("boogie_bomb_dance");
    public static final Supplier<SoundEvent> DIALOGUE = registerSoundEvent("dialogue");

    private static Supplier<SoundEvent> registerSoundEvent(String name) {
        ResourceLocation path = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(path));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}

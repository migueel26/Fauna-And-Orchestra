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

    public static final Supplier<SoundEvent> FROG_SONG = registerSoundEvent("frog_song");
    public static final Supplier<SoundEvent> RINGTAILS_SONG = registerSoundEvent("hona_bildots_eztia");

    public static final Supplier<SoundEvent> MANTIS_AMBIENT = registerSoundEvent("mantis_ambient");
    public static final Supplier<SoundEvent> MANTIS_ANGRY = registerSoundEvent("mantis_angry");

    public static final Supplier<SoundEvent> BABY_PENGUIN_AMBIENT = registerSoundEvent("baby_penguin_ambient");
    public static final Supplier<SoundEvent> SUCCESSFUL_TAME = registerSoundEvent("successful_tame");
    public static final Supplier<SoundEvent> MAGIC_GROWTH = registerSoundEvent("magic_growth");
    public static final Supplier<SoundEvent> DIALOGUE = registerSoundEvent("dialogue");

    private static Supplier<SoundEvent> registerSoundEvent(String name) {
        ResourceLocation path = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(path));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}

package net.migueel26.faunaandorchestra.particles;

import com.mojang.serialization.MapCodec;
import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModParticleTypes {
    public static DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(
            BuiltInRegistries.PARTICLE_TYPE, FaunaAndOrchestra.MOD_ID
    );

    public static final Supplier<SimpleParticleType> FAUNA_NOTES =
            PARTICLE_TYPES.register("fauna_notes", () -> new SimpleParticleType(true));
    public static final Supplier<SimpleParticleType> TREBLE_CLEF =
            PARTICLE_TYPES.register("treble_clef", () -> new SimpleParticleType(true));
    public static final Supplier<SimpleParticleType> DRIPPING_MUSIC =
            PARTICLE_TYPES.register("dripping_music", () -> new SimpleParticleType(true));
    public static final Supplier<SimpleParticleType> MAGICAL_NOTE =
            PARTICLE_TYPES.register("magical_note", () -> new SimpleParticleType(true));
    public static final Supplier<SimpleParticleType> REGULAR_NOTE =
            PARTICLE_TYPES.register("regular_note", () -> new SimpleParticleType(true));
    public static final Supplier<SimpleParticleType> STAR =
            PARTICLE_TYPES.register("star", () -> new SimpleParticleType(true));
    public static final Supplier<SimpleParticleType> CAULDRON_POP =
            PARTICLE_TYPES.register("cauldron_pop", () -> new SimpleParticleType(true));
    public static final Supplier<SimpleParticleType> VOICE_PARTICLE =
            PARTICLE_TYPES.register("voice_particle", () -> new SimpleParticleType(true));
    public static final Supplier<SimpleParticleType> SLEEP =
            PARTICLE_TYPES.register("sleep_particle", () -> new SimpleParticleType(true));
    public static final Supplier<SimpleParticleType> BASS_CLEF =
            PARTICLE_TYPES.register("bass_clef", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, ParticleType<ItemParticleOption>> SPEECH_BUBBLE =
            PARTICLE_TYPES.register("speech_bubble", () -> new ParticleType<>(false) {
                @Override
                public MapCodec<ItemParticleOption> codec() {
                    return ItemParticleOption.codec((ParticleType<ItemParticleOption>) this);
                }

                @Override
                public StreamCodec<? super RegistryFriendlyByteBuf, ItemParticleOption> streamCodec() {
                    return ItemParticleOption.streamCodec((ParticleType<ItemParticleOption>) this);
                }
            });
    public static void register(IEventBus eventBus) {
        PARTICLE_TYPES.register(eventBus);
    }
}

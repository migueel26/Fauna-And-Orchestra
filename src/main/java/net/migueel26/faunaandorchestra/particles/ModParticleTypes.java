package net.migueel26.faunaandorchestra.particles;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
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
    public static final Supplier<SimpleParticleType> STAR =
            PARTICLE_TYPES.register("star", () -> new SimpleParticleType(true));
    public static final Supplier<SimpleParticleType> CAULDRON_POP =
            PARTICLE_TYPES.register("cauldron_pop", () -> new SimpleParticleType(true));
    public static final Supplier<SimpleParticleType> VOICE_PARTICLE =
            PARTICLE_TYPES.register("voice_particle", () -> new SimpleParticleType(true));
    public static final Supplier<SimpleParticleType> SLEEP =
            PARTICLE_TYPES.register("sleep_particle", () -> new SimpleParticleType(true));
    public static void register(IEventBus eventBus) {
        PARTICLE_TYPES.register(eventBus);
    }
}

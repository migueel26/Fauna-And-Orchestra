package net.migueel26.faunaandorchestra.sound;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, FaunaAndOrchestra.MOD_ID);

    public static final Supplier<SoundEvent> VIOLIN_USE = registerSFX("violin_use");

    private static Supplier<SoundEvent> registerSFX(String name) {
        ResourceLocation path = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(path));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}

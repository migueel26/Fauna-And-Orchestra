package net.migueel26.faunaandorchestra.networking;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.custom.MusicalEntity;
import net.migueel26.faunaandorchestra.mixins.client.accessors.ClientLevelAccessor;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.migueel26.faunaandorchestra.sound.custom.InstrumentSoundInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public class ClientPayloadHandler {
    public static void handleDataOnNetwork(StartOrchestraMusicPayload payload, IPayloadContext iPayloadContext) {
        ClientLevelAccessor level = (ClientLevelAccessor) Minecraft.getInstance().level;
        UUID uuid = payload.entityID();
        SoundEvent soundEvent = BuiltInRegistries.SOUND_EVENT.get(payload.soundPath());;
        int ticksOffset = payload.tickOffset();

        if (level != null) {
            MusicalEntity entity = (MusicalEntity) level.callGetEntities().get(uuid);
            if (entity == null) {
                System.err.println("The UUID in the StartOrchestraMusicPayload is for an entity that does not exist");
            }

            System.out.println("Packet received!");
            Minecraft.getInstance().getSoundManager().play(new InstrumentSoundInstance(entity, soundEvent, ticksOffset));
        }
    }
}

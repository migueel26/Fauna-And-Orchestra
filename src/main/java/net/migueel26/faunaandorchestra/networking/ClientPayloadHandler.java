package net.migueel26.faunaandorchestra.networking;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.entity.custom.ConductorEntity;
import net.migueel26.faunaandorchestra.entity.custom.MusicalEntity;
import net.migueel26.faunaandorchestra.mixins.client.accessors.ClientLevelAccessor;
import net.migueel26.faunaandorchestra.mixins.interfaces.ISoundManagerMixin;
import net.migueel26.faunaandorchestra.sound.custom.InstrumentSoundInstance;
import net.migueel26.faunaandorchestra.util.MusicUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;
import java.util.UUID;

public class ClientPayloadHandler {
    public static void handleEmpty(CustomPacketPayload payload, IPayloadContext iPayloadContext) {

    }
    public static void handleStartOrchestraOnNetwork(StartOrchestraMusicS2CPayload payload, IPayloadContext iPayloadContext) {
        ClientLevelAccessor level = (ClientLevelAccessor) Minecraft.getInstance().level;
        UUID uuid = payload.entityID();
        SoundEvent soundEvent = BuiltInRegistries.SOUND_EVENT.get(payload.soundPath());;
        int ticksOffset = payload.tickOffset();

        if (level != null) {
            MusicalEntity entity = (MusicalEntity) level.callGetEntities().get(uuid);
            if (entity == null) {
                System.err.println("The UUID in the StartOrchestraMusicPayload is for an entity that does not exist");
            }

            //System.out.println("Packet received!");
            Minecraft.getInstance().getSoundManager().play(new InstrumentSoundInstance(entity, soundEvent, 1.0F, ticksOffset));
        }
    }

    public static void handleRestartOrchestraOnNetwork(RestartOrchestraMusicS2CPayload payload, IPayloadContext context) {
        List<UUID> UUIDorchestra = payload.orchestra();
        UUID conductorUUID = payload.conductor();
        float volume = payload.volume();
        int tickOffset = payload.tickOffset();
        ClientLevelAccessor level = (ClientLevelAccessor) Minecraft.getInstance().level;

        if (level != null) {
            ConductorEntity conductor = (ConductorEntity) level.callGetEntities().get(conductorUUID);
            if (conductor != null) {
                Item newSheetMusic = conductor.getSheetMusic();
                // If the newSheet is empty we return
                if (newSheetMusic == Items.AIR) {
                    MusicUtil.deleteOrchestra(conductorUUID);
                    return;
                }

                // If not, we update it in MusicUtil
                boolean newSong = MusicUtil.updateNewSheet(conductorUUID, newSheetMusic);

                // If it's a new song, we start it from the beginning
                if (newSong) tickOffset = 0;
                else newSheetMusic = conductor.getSheetMusic();

                List<MusicalEntity> orchestra = UUIDorchestra.stream().map(uuid -> (MusicalEntity) level.callGetEntities().get(uuid)).toList();
                for (MusicalEntity musician : orchestra) {
                    // For each musician, we get the location of its song
                    ResourceLocation musician_song = ResourceLocation.fromNamespaceAndPath(FaunaAndOrchestra.MOD_ID,
                            MusicUtil.getLocation(newSheetMusic, musician.getInstrument().get()));

                    // We stop all current instrument sounds
                    ((ISoundManagerMixin) Minecraft.getInstance().getSoundManager()).faunaStopMusic(musician.getUUID());

                    // We play them with the new volume / sheet music
                    Minecraft.getInstance().getSoundManager().play(
                            new InstrumentSoundInstance(
                                    musician,
                                    BuiltInRegistries.SOUND_EVENT.get(musician_song),
                                    volume, tickOffset));
                }
            }
        }
    }
}

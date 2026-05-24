package net.migueel26.faunaandorchestra.networking;

import net.migueel26.faunaandorchestra.FaunaAndOrchestra;
import net.migueel26.faunaandorchestra.block.entity.OwnableBlockEntity;
import net.migueel26.faunaandorchestra.entity.custom.*;
import net.migueel26.faunaandorchestra.entity.custom.boss.TheGreatComposer;
import net.migueel26.faunaandorchestra.entity.custom.jazzy_dammys.DanB;
import net.migueel26.faunaandorchestra.mixins.client.accessors.ClientLevelAccessor;
import net.migueel26.faunaandorchestra.mixins.interfaces.ISoundManagerMixin;
import net.migueel26.faunaandorchestra.sound.ModSounds;
import net.migueel26.faunaandorchestra.sound.custom.BossSoundInstance;
import net.migueel26.faunaandorchestra.sound.custom.FrogSongSoundInstance;
import net.migueel26.faunaandorchestra.sound.custom.InstrumentSoundInstance;
import net.migueel26.faunaandorchestra.sound.custom.TravellingMusicianSoundInstance;
import net.migueel26.faunaandorchestra.util.MusicUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;
import java.util.UUID;

public class ClientPayloadHandler {
    public static void handleEmpty(CustomPacketPayload payload, IPayloadContext iPayloadContext) {

    }
    public static void handleStartOrchestraOnNetwork(StartOrchestraMusicS2CPayload payload, IPayloadContext iPayloadContext) {
        iPayloadContext.enqueueWork(() -> {
            ClientLevelAccessor level = (ClientLevelAccessor) Minecraft.getInstance().level;
            UUID uuid = payload.entityID();
            SoundEvent soundEvent = BuiltInRegistries.SOUND_EVENT.get(payload.soundPath());;
            int ticksOffset = payload.tickOffset();

            if (level != null) {
                MusicalEntity entity = (MusicalEntity) level.callGetEntities().get(uuid);
                if (entity == null) {
                    System.err.println("The UUID in the StartOrchestraMusicPayload is for an entity that does not exist");
                }

                ((ISoundManagerMixin) Minecraft.getInstance().getSoundManager()).faunaStopMusic(uuid);
                Minecraft.getInstance().getSoundManager().play(new InstrumentSoundInstance(entity, soundEvent, 1.0F, ticksOffset));
            }
        });
    }

    public static void handleRestartOrchestraOnNetwork(RestartOrchestraMusicS2CPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            List<UUID> UUIDorchestra = payload.orchestra();
            UUID conductorUUID = payload.conductor();
            float volume = payload.volume();
            int tickOffset = payload.tickOffset();
            ClientLevelAccessor level = (ClientLevelAccessor) Minecraft.getInstance().level;

            if (level != null) {
                ConductorEntity conductor = (ConductorEntity) level.callGetEntities().get(conductorUUID);

                if (conductor != null) {
                    Item newSheetMusic = MusicUtil.getSheet(payload.sheetName());
                    // If the newSheet is empty we return
                    if (newSheetMusic == Items.AIR) {
                        MusicUtil.deleteOrchestra(conductorUUID);
                        return;
                    }
                    // We save the current volume
                    conductor.setCurrentVolume(volume);

                    // If it's a new song, we update it in MusicUtil
                    boolean newSong = MusicUtil.updateNewSheet(conductorUUID, newSheetMusic);

                    // If it's a new song, we start it from the beginning
                    if (newSong) tickOffset = 0;

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
        });
    }

    public static void handleStopMusicOnNetwork(StopMusicS2CPayload payload, IPayloadContext iPayloadContext) {
        UUID uuid = payload.entityUUID();
        ClientLevelAccessor level = (ClientLevelAccessor) Minecraft.getInstance().level;

        if (level != null) {
            Entity entity = level.callGetEntities().get(uuid);
            if (entity instanceof QuirkyFrogEntity) {
                ((ISoundManagerMixin) Minecraft.getInstance().getSoundManager()).faunaStopFrogMusic(uuid);
            } else if (entity instanceof TravellingMusician) {
                ((ISoundManagerMixin) Minecraft.getInstance().getSoundManager()).faunaStopTravellingMusicianMusic(uuid);
            }

        }
    }

    public static void handleStartAmbientMusicOnNetwork(StartAmbientMusicS2CPayload payload, IPayloadContext iPayloadContext) {
        UUID uuid = payload.conductorUUID();
        ClientLevelAccessor level = (ClientLevelAccessor) Minecraft.getInstance().level;

        if (level != null) {
            Entity musician = level.callGetEntities().get(uuid);

            if (musician instanceof QuirkyFrogEntity quirkyFrog) {
                Minecraft.getInstance().getSoundManager().play(new FrogSongSoundInstance(
                        ModSounds.FROG_SONG.get(),
                        quirkyFrog));
            } else if (musician instanceof Faust faust) {
                Minecraft.getInstance().getSoundManager().play(new TravellingMusicianSoundInstance(
                        ModSounds.RINGTAILS_SONG.get(),
                        faust
                ));
            } else if (musician instanceof TheGreatComposer theGreatComposer) {
                SoundEvent soundEvent = theGreatComposer.isFinalPhase() ? ModSounds.THE_GREAT_COMPOSER_FINAL_THEME.get() : ModSounds.THE_GREAT_COMPOSER_THEME.get();
                Minecraft.getInstance().getSoundManager().play(new BossSoundInstance(
                        soundEvent, theGreatComposer
                ));
            } else if (musician instanceof DanB danB) {
                Minecraft.getInstance().getSoundManager().play(new TravellingMusicianSoundInstance(
                        ModSounds.JAZZY_DAMMYS_SONG.get(),
                        danB
                ));
            } else {
                System.err.println("The UUID in the StartAmbientMusicPayload is for an entity that does not exist");
            }
        }
    }

    public static void handleStopOrchestraOnNetwork(StopOrchestraMusicS2CPayload payload, IPayloadContext iPayloadContext) {
        List<UUID> orchestraUUID = payload.orchestra();
        ClientLevelAccessor level = (ClientLevelAccessor) Minecraft.getInstance().level;

        if (level != null) {
            ISoundManagerMixin soundManager = (ISoundManagerMixin) Minecraft.getInstance().getSoundManager();
            for (UUID musicianUUID : orchestraUUID) {
                soundManager.faunaStopMusic(musicianUUID);
            }
        }
    }

    public static void handleShowTitleOnNetwork(ShowTitlePlayerS2CPayload payload, IPayloadContext context) {
        Gui gui = Minecraft.getInstance().gui;
        String title = payload.title();
        String subtitle = payload.subtitle();

        gui.setTitle(Component.literal(title));
        gui.setSubtitle(Component.literal(subtitle).withStyle(ChatFormatting.GREEN));
    }

    public static void handleSyncOwnableBEOnNetwork(SyncOwnableBEPayloadS2C payload, IPayloadContext context) {
        UUID uuid = payload.owner();
        BlockPos blockPos = new BlockPos(payload.x(), payload.y(), payload.z());
        ClientLevel level = Minecraft.getInstance().level;

        if (level != null) {
            Entity entity = ((ClientLevelAccessor) level).callGetEntities().get(uuid);
            if (entity != null && level.getBlockEntity(blockPos) instanceof OwnableBlockEntity be) {
                be.setOwner(uuid);
            }
        }
    }
}

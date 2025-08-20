package net.migueel26.faunaandorchestra.mixins.interfaces;

import java.util.UUID;

public interface ISoundManagerMixin {
    void faunaStopMusic(UUID entityID);
    void faunaStopFrogMusic(UUID entityUUID);
    void faunaStopTravellingMusicianMusic(UUID entityUUID);

    boolean faunaIsThereAnOrchestra();
}

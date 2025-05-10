package net.migueel26.faunaandorchestra.mixins.interfaces;

import java.util.UUID;

public interface ISoundEngineMixin {
    void faunaStopMusic(UUID entityID);
    void faunaStopFrogMusic(UUID entityUUID);

    boolean faunaIsThereAnOrchestra();
}

package net.migueel26.faunaandorchestra.entity.custom;

public interface ListeningEntity {
    // These entities are waiting for an orchestra to start or end
    void onStartListening(ConductorEntity conductor);
    void onStopListening();
}

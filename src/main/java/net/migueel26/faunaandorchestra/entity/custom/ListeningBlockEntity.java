package net.migueel26.faunaandorchestra.entity.custom;

public interface ListeningBlockEntity {
    // These blocks are waiting for an orchestra to start or end
    void onStartListening(ConductorEntity conductor);
    void onStopListening();
    boolean isListening();
}

package net.migueel26.faunaandorchestra.entity.custom.variants;

import java.util.Arrays;
import java.util.Comparator;

public enum MantisVariant {
    NORMAL(0),
    ORCHID(1);

    private static final MantisVariant[] BY_ID = Arrays.stream(values()).sorted(
            Comparator.comparingInt(MantisVariant::getId)).toArray(MantisVariant[]::new);
    private final int id;

    MantisVariant(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static MantisVariant byId(int id) {
        return BY_ID[id % BY_ID.length];
    }
}

package net.migueel26.faunaandorchestra.entity.custom.variants;

import java.util.Arrays;
import java.util.Comparator;

public enum ButterflyVariant {
    BLUE(0),
    ORANGE(1),
    MAGENTA(2);

    private static final ButterflyVariant[] BY_ID = Arrays.stream(values()).sorted(
            Comparator.comparingInt(ButterflyVariant::getId)).toArray(ButterflyVariant[]::new);
    private final int id;

    ButterflyVariant(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static ButterflyVariant byId(int id) {
        return BY_ID[id % BY_ID.length];
    }
}

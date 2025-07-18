package net.migueel26.faunaandorchestra.block.properties;

import net.minecraft.util.StringRepresentable;

public enum GravestonePart implements StringRepresentable {
    HEAD("head"),
    FOOT("foot");

    private final String name;

    private GravestonePart(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public String getSerializedName() {
        return null;
    }
}

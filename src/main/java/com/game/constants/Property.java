package com.game.constants;

public enum Property {
    SYMBIOSIS,
    PIRACY,
    GRAZING,
    TAIL_LOSS,
    HIBERNATION,
    POISONOUS,
    COMMUNICATION,
    PREDATOR,
    FAT,
    SCAVENGER,
    RUNNING,
    MIMICRY,
    CAMOUFLAGE,
    BURROWING,
    SHARP_VISION,
    PARASITE,
    COOPERATION,
    BIG,
    SWIMMING,
    DELETE_PROPERTY;

    public static boolean isPropertyDouble(Property property) {
        return property.equals(Property.COOPERATION) || property.equals(Property.COMMUNICATION) || property.equals(Property.SYMBIOSIS);
    }
}

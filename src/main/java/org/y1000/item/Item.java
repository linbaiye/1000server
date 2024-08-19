package org.y1000.item;


import java.util.Optional;

public interface Item {
    String name();

    ItemType itemType();

    String description();

    default Optional<String> dropSound() {
        return Optional.empty();
    }

    default Optional<String> eventSound() {
        return Optional.empty();
    }

    default int color() {
        return 0;
    }
}

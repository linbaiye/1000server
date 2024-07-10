package org.y1000.entities;

import java.util.Optional;

public interface AttributeProvider {
    int life();
    int avoidance();
    int recovery();
    int attackSpeed();
    int wanderingRange();
    int armor();
    int hit();

    default int walkSpeed() {
        return 0;
    }

    default Optional<String> attackSound() {
        return Optional.empty();
    }

    int damage();

    String hurtSound();

    default Optional<String> normalSound() {
        return Optional.empty();
    }



    default Optional<String> dieSound() {
        return Optional.empty();
    }
}

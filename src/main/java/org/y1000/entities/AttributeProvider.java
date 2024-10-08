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

    int viewWidth();

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

    default String animate() {
        return null;
    }

    default String shape() {
        return null;
    }

    default String idName() {
        return null;
    }

    default Optional<String> dieSound() {
        return Optional.empty();
    }

    default int escapeLife() {
        return 0;
    }
}

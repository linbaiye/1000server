package org.y1000.entities.players;

import org.y1000.entities.creatures.CreatureState;

public interface PlayerState extends CreatureState<PlayerImpl> {

    default void afterHurt(PlayerImpl player) {

    }

    default boolean canUseFootKungFu() {
        return true;
    }

    default boolean canSitDown() {
        return false;
    }

    default boolean canStandUp() {
        return false;
    }
}

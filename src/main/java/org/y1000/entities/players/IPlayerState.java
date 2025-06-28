package org.y1000.entities.players;

import org.y1000.entities.creatures.ICreatureState;

public interface IPlayerState extends ICreatureState<PlayerImpl> {

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

    default void handleInput(PlayerImpl player, Object input) {

    }

}

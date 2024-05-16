package org.y1000.entities.players;

import org.y1000.entities.creatures.CreatureState;

public interface PlayerState extends CreatureState<PlayerImpl> {

    default void afterHurt(PlayerImpl player) {

    }
}

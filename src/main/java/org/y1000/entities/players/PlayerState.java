package org.y1000.entities.players;

import org.y1000.entities.creatures.CreatureState;
import org.y1000.kungfu.KungFu;

public interface PlayerState extends CreatureState<PlayerImpl> {

    default void attackKungFuTypeChanged(PlayerImpl player) {

    }

    default void afterHurt(PlayerImpl player) {

    }

    default void toggleFootKungFu(KungFu kungFu) {

    }

    default void handleSit(PlayerImpl player) {

    }

}

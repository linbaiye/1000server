package org.y1000.kungfu;

import org.y1000.entities.players.Player;

public interface KungFu {
    String name();

    int level();

    default void use(Player player) {

    }

    KungFuType kungFuType();
}

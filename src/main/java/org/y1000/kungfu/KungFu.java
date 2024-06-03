package org.y1000.kungfu;

import org.y1000.entities.players.PlayerImpl;

public interface KungFu {
    String name();

    int level();

    default boolean hasEnoughResources(PlayerImpl player) {
        return true;
    }

    default boolean useResources(PlayerImpl player) {
        return true;
    }

    KungFuType kungFuType();
}

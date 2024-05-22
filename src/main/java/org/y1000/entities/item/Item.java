package org.y1000.entities.item;

import org.y1000.entities.players.Player;

public interface Item {
    long id();

    String name();

    ItemType type();

    default void doubleClicked(Player player) {

    }

}

package org.y1000.factory;

import org.y1000.entities.players.Player;

public interface PlayerFactory {

    Player create(String name, boolean male);

}

package org.y1000.entities.players;

import org.y1000.entities.players.Player;

public interface PlayerRepository {

    Player load(long id);

}

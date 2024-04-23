package org.y1000.entities.repository;

import org.y1000.entities.players.Player;

public interface PlayerRepository {

    Player load(long id);

    Player load();

    void save(Player player);

}

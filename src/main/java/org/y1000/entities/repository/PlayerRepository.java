package org.y1000.entities.repository;

import org.y1000.entities.players.Player;
import org.y1000.network.Connection;

public interface PlayerRepository {

    Player load(long id);

    Player load(Connection connection);

    void save(Player player);

}

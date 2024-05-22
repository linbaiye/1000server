package org.y1000.entities.repository;

import org.y1000.entities.players.Player;
import org.y1000.network.Connection;

public interface PlayerRepository {

    Player load(String token);

    void save(Player player);

}

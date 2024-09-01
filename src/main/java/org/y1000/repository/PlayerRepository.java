package org.y1000.repository;

import jakarta.persistence.EntityManager;
import org.y1000.entities.players.Player;

public interface PlayerRepository {

    Player load(String token);

    void update(Player player);

    void update(EntityManager entityManager, Player player);

    void save(EntityManager entityManager, int accountId, Player player);

    int countByName(EntityManager entityManager, String name);

}

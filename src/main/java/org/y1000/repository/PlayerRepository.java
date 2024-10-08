package org.y1000.repository;

import jakarta.persistence.EntityManager;
import org.apache.commons.lang3.tuple.Pair;
import org.y1000.entities.players.Player;

import java.util.Optional;

public interface PlayerRepository {

    Optional<Pair<Player, Integer>> find(int accountId, String charName);

    void update(Player player);

    long save(EntityManager entityManager, int accountId, Player player);

    int countByName(EntityManager entityManager, String name);

    int countByAccount(EntityManager entityManager, int accountId);

}

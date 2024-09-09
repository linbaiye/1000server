package org.y1000.repository;

import jakarta.persistence.EntityManager;
import org.y1000.entities.players.Player;

public interface ItemRepository {
    void save(EntityManager entityManager, Player player);

}

package org.y1000.repository;

import jakarta.persistence.EntityManager;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.inventory.Inventory;

public interface ItemRepository {
    void save(EntityManager entityManager, Player player);

    Inventory findInventory(EntityManager entityManager, long playerId);
}
